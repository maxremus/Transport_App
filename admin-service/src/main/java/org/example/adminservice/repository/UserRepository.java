package org.example.adminservice.repository;

import org.example.adminservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByCompanyId(Long companyId);

    // JOIN FETCH зарежда company в същата заявка, за да не гърми
    // LazyInitializationException в темплейта (open-in-view=false)
    @Query("select u from User u left join fetch u.company")
    List<User> findAllWithCompany();
}
