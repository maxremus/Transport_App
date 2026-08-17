function togglePassword(btn) {
    const wrap = btn.closest('.password-wrap');
    const input = wrap.querySelector('input');
    if (input.type === 'password') {
        input.type = 'text';
        btn.textContent = '🙈';
    } else {
        input.type = 'password';
        btn.textContent = '👁';
    }
}
