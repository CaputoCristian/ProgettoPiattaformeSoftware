package org.example.progetto.services;

import org.example.progetto.DTO.ProductInSaleDTO;
import org.example.progetto.DTO.ProductPurchasedDTO;
import org.example.progetto.DTO.PurchaseDTO;
import org.example.progetto.DTO.SaleDTO;
import org.example.progetto.entities.*;
import org.example.progetto.repositories.*;
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

@Service("SaleService")
@Transactional
public class SaleService {
    @Autowired
    private SaleAlertRepository saleRepository;
    @Autowired
    private ProductInSaleRepository productInSaleRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = false)
    public SaleAlert addSaleAlert(SaleAlert sale) {
        saleRepository.save(sale);
        return sale;
    }

    @Transactional(readOnly = true)
    public List<SaleAlert> showAllSales(String email) { //Ricerca gli acquisti fatti da un dato user
        List<SaleAlert> saleList = saleRepository.findBySeller_Email(email);
        return saleList;
    }

    @Transactional(readOnly = true)
    public List<SaleAlert> showAllPurchase(String email, int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<SaleAlert> pagedResult = saleRepository.findBySeller_Email(email, paging);
        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public Optional<SaleAlert> showById(Long id) {
        Optional<SaleAlert> result = saleRepository.findById(id);
        return result;
    }


//    public List<Purchase> showAllSalesBetween(long userId, LocalDateTime startTime, LocalDateTime endTime, int pageNumber, int pageSize, String sortBy) {
//        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
//        Page<Purchase> pagedResult = saleRepository.findBySeller_IdAndTimeBetween(userId, startTime, endTime,paging);
//        if ( pagedResult.hasContent() ) {
//            return pagedResult.getContent();
//        }
//        else {
//            return new ArrayList<>();
//        }
//    }

    @Transactional(readOnly = true)
    public List<SaleDTO> getAllSalesForUser(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("Utente non trovato");

        List<SaleAlert> vendite = saleRepository.findBySeller(user);

        return vendite.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    private SaleDTO mapToDTO(SaleAlert sale) {
        List<ProductInSale> prodotti = productInSaleRepository.findBySaleAlert(sale);

        List<ProductInSaleDTO> prodottiDTO = prodotti.stream()
                .map(p -> new ProductInSaleDTO(p.getProduct().getName(), p.getQuantity()))
                .collect(Collectors.toList());

        BigDecimal totale = prodotti.stream()
                .map(p -> p.getProduct().getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        SaleDTO dto = new SaleDTO();
        dto.setId(sale.getId());
        dto.setDate(sale.getCreatedAt());
        dto.setTotalPrice(totale);
        dto.setProducts(prodottiDTO);

        return dto;
    }

}