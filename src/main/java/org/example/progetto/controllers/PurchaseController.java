package org.example.progetto.controllers;

import org.example.progetto.DTO.ProductUpdateRequest;
import org.example.progetto.DTO.PurchaseDTO;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.Purchase;
import org.example.progetto.exceptions.BarcodeAlreadyExistException;
import org.example.progetto.services.ProductService;
import org.example.progetto.services.PurchaseService;
import org.example.progetto.services.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
)
@RequestMapping("/purchases")
public class PurchaseController {

    @Autowired
    private PurchaseService purchaseService;



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
    public ResponseEntity<List<PurchaseDTO>> getAllPurchases(Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");
        try {
            List<PurchaseDTO> acquisti = purchaseService.getAllPurchasesForUser(email);
            return ResponseEntity.ok(acquisti);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }


    @GetMapping("/allPurchase/paged")
    public ResponseEntity getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy, Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        List<Purchase> result = purchaseService.showAllPurchase(email,pageNumber, pageSize, sortBy);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>("No results!", HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity getPurchase(@PathVariable Long id, Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

//        Optional<Purchase> result = purchaseService.showById(id);
//        if ( result.getBuyer().getEmail == email ) {
//            return new ResponseEntity<>("No results!", HttpStatus.OK);
//        }
//        return new ResponseEntity<>(result, HttpStatus.OK);

        Purchase purchase = purchaseService.showById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ordine non trovato"));

        if (!purchase.getBuyer().getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Non sei autorizzato a visualizzare questo ordine");
        }

        return new ResponseEntity<>(purchase, HttpStatus.OK);

    }


    @GetMapping("/{id}/filtered")
    public ResponseEntity getAllBetween(@PathVariable Long id, @RequestBody LocalDateTime startDate, @RequestBody LocalDateTime endDate, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Purchase> result = purchaseService.showAllPurchaseBetween(id, startDate, endDate, pageNumber, pageSize, sortBy);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>("No results!", HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

}
