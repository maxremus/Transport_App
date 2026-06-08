package org.example.transport_saas.repository;

import org.example.transport_saas.entity.Subscription;
import org.example.transport_saas.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    Optional<Subscription> findByUserEmail(String email);


    List<Subscription> findByUser(User user);
}
