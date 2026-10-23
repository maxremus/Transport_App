package org.example.transport_saas.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Собствена страница за грешки вместо Whitelabel Error Page.
 * Показва на езика на потребителя какво се е случило и накъде да продължи,
 * без да изважда stack trace или вътрешни детайли.
 */
@Controller
@RequiredArgsConstructor
public class CustomErrorController implements ErrorController {

    private static final Set<Integer> KNOWN_STATUSES = Set.of(400, 403, 404, 405, 500);

    private final ErrorAttributes errorAttributes;

    @RequestMapping(value = "/error", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView errorPage(HttpServletRequest request) {

        int status = resolveStatus(request);
        Throwable cause = resolveCause(request);

        ModelAndView mav = new ModelAndView("error");
        mav.setStatus(HttpStatus.resolve(status) != null ? HttpStatus.resolve(status) : HttpStatus.INTERNAL_SERVER_ERROR);

        mav.addObject("status", status);
        mav.addObject("statusKey", KNOWN_STATUSES.contains(status) ? String.valueOf(status) : "generic");
        mav.addObject("time", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")));
        mav.addObject("loggedIn", isLoggedIn());

        Object path = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        mav.addObject("path", path != null ? path.toString() : "");

        if (cause != null) {
            mav.addObject("errorCode", cause.getClass().getSimpleName());

            if (cause instanceof DataIntegrityViolationException) {
                mav.addObject("causeKey", "error.cause.data");
            } else if (cause.getClass().getName().startsWith("feign.")) {
                mav.addObject("causeKey", "error.cause.service");
            } else if (isDeliberateMessage(cause)) {
                // съобщения, които самото приложение хвърля нарочно (напр. "Няма избрани курсове")
                mav.addObject("userMessage", cause.getMessage());
            }
        }

        return mav;
    }

    // Заявки, които не искат HTML (иконки, JSON и т.н.) - само статус, без тяло
    @RequestMapping("/error")
    public ResponseEntity<Void> errorNoBody(HttpServletRequest request) {
        HttpStatus status = HttpStatus.resolve(resolveStatus(request));
        return ResponseEntity.status(status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR).build();
    }

    private int resolveStatus(HttpServletRequest request) {
        Object code = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        return code instanceof Integer i ? i : 500;
    }

    private Throwable resolveCause(HttpServletRequest request) {
        Throwable ex = errorAttributes.getError(new ServletWebRequest(request));
        if (ex == null) {
            Object attr = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
            ex = attr instanceof Throwable t ? t : null;
        }
        while (ex instanceof ServletException && ex.getCause() != null) {
            ex = ex.getCause();
        }
        return ex;
    }

    private boolean isDeliberateMessage(Throwable ex) {
        Class<?> c = ex.getClass();
        boolean ours = c == RuntimeException.class
                || c == IllegalArgumentException.class
                || c == IllegalStateException.class;
        return ours && ex.getMessage() != null && !ex.getMessage().isBlank();
    }

    private boolean isLoggedIn() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.isAuthenticated() && !(auth instanceof AnonymousAuthenticationToken);
    }
}
