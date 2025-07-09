package org.example.progetto.repositories;

import org.example.progetto.entities.SaleAlert;
import org.example.progetto.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SaleAlertRepository extends JpaRepository<SaleAlert, Integer> {
    List<SaleAlert> findBySeller(User seller);
}
