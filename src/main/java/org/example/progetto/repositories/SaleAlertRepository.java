package org.example.progetto.repositories;

import org.example.progetto.entities.Purchase;
import org.example.progetto.entities.SaleAlert;
import org.example.progetto.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SaleAlertRepository extends JpaRepository<SaleAlert, Integer> {
    List<SaleAlert> findBySeller(User seller);
    List<SaleAlert> findBySeller_Email(String email);
    Page<SaleAlert> findBySeller_Email(String email, Pageable paging);
    Optional<SaleAlert> findById(Long id);
}
