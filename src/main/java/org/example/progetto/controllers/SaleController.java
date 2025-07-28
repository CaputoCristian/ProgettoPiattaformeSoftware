package org.example.progetto.controllers;


import org.example.progetto.DTO.SaleDTO;
import org.example.progetto.entities.SaleAlert;
import org.example.progetto.services.SaleService;
import org.example.progetto.exceptions.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
)
@RequestMapping("/sales")
public class SaleController {

    @Autowired
    private SaleService saleService;


    //TODO vedi cosa restituisce sto metodo.

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}/check")
    public ResponseEntity<Void> toggleCheck(@PathVariable Long id, Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        SaleAlert sale = saleService.showById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordine non trovato"));

        if (!sale.getSeller().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non sei autorizzato a visualizzare questo ordine");
        }
        else {
            saleService.toggleViewed(sale);
        }
        return new ResponseEntity<>(HttpStatus.OK);

    }

    ///  Non necessario
//    @PostMapping
//    public ResponseEntity addPurchase(@RequestBody Purchase purchase) {
//        Purchase addedPurchase = purchaseService.addPurchase(purchase); //Non serve tornare l'utente se si ha il .ok (lazy method)
//        return ResponseEntity.ok(addedPurchase);
//    }


//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/getAll")
//    public ResponseEntity showAllPurchase(Authentication authentication) { //Prende id utente
//        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");
//
//        List<Purchase> purchases = purchaseService.showAllPurchase(email);
//
//        return ResponseEntity.ok(purchases);
//
//    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/getAll")
    public ResponseEntity<List<SaleDTO>> getAllSales(Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");
        try {
            List<SaleDTO> vendite = saleService.getAllSalesForUser(email);
            return ResponseEntity.ok(vendite);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


//    @GetMapping("/allPurchase/paged")
//    public ResponseEntity getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, Authentication authentication) {
//        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");
//
//        List<Purchase> result = saleService.showAllSales(email,pageNumber, pageSize, sortBy);
//        if ( result.size() <= 0 ) {
//            return new ResponseEntity<>("No results!", HttpStatus.OK);
//        }
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }

    @GetMapping("/{id}")
    public ResponseEntity getSale(@PathVariable Long id, Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        SaleAlert sale = saleService.showById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordine non trovato"));

        if (!sale.getSeller().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non sei autorizzato a visualizzare questo ordine");
        }

        return new ResponseEntity<>(sale, HttpStatus.OK);

    }


//    @GetMapping("/{id}/filtered")
//    public ResponseEntity getAllBetween(@PathVariable Long id, @RequestBody LocalDateTime startDate, @RequestBody LocalDateTime endDate, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
//        List<Purchase> result = saleService.showAllSalesBetween(id, startDate, endDate, pageNumber, pageSize, sortBy);
//        if ( result.size() <= 0 ) {
//            return new ResponseEntity<>("No results!", HttpStatus.OK);
//        }
//        return new ResponseEntity<>(result, HttpStatus.OK);
//    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("/hasNotifications")
    public ResponseEntity<Boolean> hasNotifications(Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");
        try {
            return ResponseEntity.ok(saleService.hasNotifications(email));
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

}
