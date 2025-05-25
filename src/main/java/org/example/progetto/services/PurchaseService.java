package org.example.progetto.services;

import org.example.progetto.entities.Product;
import org.example.progetto.entities.Purchase;
import org.example.progetto.repositories.PurchaseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service("purchaseService")
@Transactional
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;

    @Transactional(readOnly = false)
    public Purchase addPurchase(Purchase purchase) {
        purchaseRepository.save(purchase);
        return purchase;
    }

    @Transactional(readOnly = true)
    public List<Purchase> showAllPurchase(long userId) { //Ricerca gli acquisti fatti da un dato user
        List<Purchase> purchaseList = purchaseRepository.findByBuyer_Id(userId);
        return purchaseList;
    }
    @Transactional(readOnly = true)
    public List<Purchase> showAllPurchase(long userId, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Purchase> pagedResult = purchaseRepository.findByBuyer_Id(userId,paging);
        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new ArrayList<>();
        }
    }

    public List<Purchase> showAllPurchaseBetween(long userId, LocalDateTime startTime,LocalDateTime endTime, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Purchase> pagedResult = purchaseRepository.findByBuyer_IdAndTimeBetween(userId, startTime, endTime,paging);
        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new ArrayList<>();
        }
    }

}
