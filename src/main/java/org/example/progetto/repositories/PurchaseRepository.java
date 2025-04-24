package org.example.progetto.repositories;

import org.example.progetto.entities.Product;
import org.example.progetto.entities.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {
    List<Purchase> findByBuyer_Id(Integer buyerId);
    List<Purchase> findByProductsInPurchase(Product product); // Ha senso???
    List<Purchase> findByPurchaseTimeBetween(String purchaseTime, String purchaseTime2);

}
