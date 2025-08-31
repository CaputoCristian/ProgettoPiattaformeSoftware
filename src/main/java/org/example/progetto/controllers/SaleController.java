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
