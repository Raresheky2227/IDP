package com.example.eventmanager.repository;

import com.example.eventmanager.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByUserIdAndEventId(String userId, String eventId);
    List<Subscription> findByUserId(String userId);

    // Add this line for delete support:
    void deleteByEventId(String eventId);

    // (You probably already have this one, but for unsubscribe)
    void deleteByUserIdAndEventId(String userId, String eventId);
}
