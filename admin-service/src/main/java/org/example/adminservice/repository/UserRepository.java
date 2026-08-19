package org.example.adminservice.repository;

import org.example.adminservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByCompanyId(Long companyId);
}
