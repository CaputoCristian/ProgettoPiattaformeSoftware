package org.example.progetto.controllers;

import org.example.progetto.DTO.ProductUpdateRequest;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.Purchase;
import org.example.progetto.exceptions.BarcodeAlreadyExistException;
import org.example.progetto.services.ProductService;
import org.example.progetto.services.PurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

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

    @PostMapping
    public ResponseEntity addPurchase(@RequestBody Purchase purchase) {
        Purchase addedPurchase = purchaseService.addPurchase(purchase); //Non serve tornare l'utente se si ha il .ok (lazy method)
        return ResponseEntity.ok(addedPurchase);
    }

    @GetMapping("/{id}")
    public List<Purchase> showAllPurchase(@PathVariable Long id) { //Prende id utente
        return purchaseService.showAllPurchase(id);
    }

    @GetMapping("/{id}/paged")
    public ResponseEntity getAll(@PathVariable Long id, @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Purchase> result = purchaseService.showAllPurchase(id,pageNumber, pageSize, sortBy);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>("No results!", HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
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
