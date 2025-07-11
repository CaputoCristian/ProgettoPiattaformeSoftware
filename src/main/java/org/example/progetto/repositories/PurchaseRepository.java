package org.example.progetto.repositories;

import org.example.progetto.entities.Product;
import org.example.progetto.entities.Purchase;
import org.example.progetto.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByBuyer_Id(Long buyerId);
    Page<Purchase> findByBuyer_Id(Long buyerId, Pageable pageable );
    Page<Purchase> findByBuyer_IdAndTimeBetween(Long buyerId,LocalDateTime startTime, LocalDateTime endTime, Pageable pageable);
    List<Purchase> findByBuyer(User user);
    List<Purchase> findByBuyer_Email(String email);
    Page<Purchase> findByBuyer_Email(String email, Pageable pageable);
}
