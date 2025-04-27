package org.example.progetto.repositories;

import org.example.progetto.entities.ProductInPurchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ProductInPurchaseRepository extends JpaRepository<ProductInPurchase, Long> {
    List<ProductInPurchase> findByRelatedPurchase_Id(Long purchaseId);
    List<ProductInPurchase> findByProductsInPurchase_Id(Long purchaseId);
    List<ProductInPurchase> findByTimeBetween(Date startTime, Date endTime);
}
