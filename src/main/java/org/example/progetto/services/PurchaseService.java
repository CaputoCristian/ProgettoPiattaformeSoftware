package org.example.progetto.services;

import org.example.progetto.DTO.ProductPurchasedDTO;
import org.example.progetto.DTO.PurchaseDTO;
import org.example.progetto.entities.ProductInPurchase;
import org.example.progetto.entities.Purchase;
import org.example.progetto.entities.User;
import org.example.progetto.exceptions.UserNotFoundException;
import org.example.progetto.repositories.ProductInPurchaseRepository;
import org.example.progetto.repositories.PurchaseRepository;
import org.example.progetto.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service("purchaseService")
@Transactional
public class PurchaseService {
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private ProductInPurchaseRepository productInPurchaseRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = false)
    public Purchase addPurchase(Purchase purchase) {
        purchaseRepository.save(purchase);
        return purchase;
    }

    @Transactional(readOnly = true)
    public List<Purchase> showAllPurchase(String email) { //Ricerca gli acquisti fatti da un dato user
        List<Purchase> purchaseList = purchaseRepository.findByBuyer_Email(email);
        return purchaseList;
    }

    @Transactional(readOnly = true)
    public List<Purchase> showAllPurchase(String email, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Purchase> pagedResult = purchaseRepository.findByBuyer_Email(email, paging);
        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public Optional<Purchase> showById(Long id) {
        Optional<Purchase> result = purchaseRepository.findById(id);
        return result;
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

    @Transactional(readOnly = true)
    public List<PurchaseDTO> getAllPurchasesForUser(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("Utente non trovato");

        List<Purchase> acquisti = purchaseRepository.findByBuyer(user);

        return acquisti.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private PurchaseDTO mapToDTO(Purchase purchase) {
        List<ProductInPurchase> prodotti = productInPurchaseRepository.findByRelatedPurchase(purchase);

        List<ProductPurchasedDTO> prodottiDTO = prodotti.stream()
                .map(p -> new ProductPurchasedDTO(p.getProduct().getName(), p.getQuantity()))
                .collect(Collectors.toList());

        BigDecimal totale = prodotti.stream()
                .map(p -> p.getProduct().getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        PurchaseDTO dto = new PurchaseDTO();
        dto.setId(purchase.getId());
        dto.setDate(purchase.getTime());
        dto.setTotalPrice(totale);
        dto.setProducts(prodottiDTO);

        return dto;
    }

}
