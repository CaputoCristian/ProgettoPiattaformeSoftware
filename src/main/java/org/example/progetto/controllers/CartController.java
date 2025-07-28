package org.example.progetto.controllers;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import org.antlr.v4.runtime.misc.NotNull;
import org.example.progetto.DTO.ProductInCartDTO;
import org.example.progetto.exceptions.InvalidOperationException;
import org.example.progetto.exceptions.InvalidQuantityException;
import org.example.progetto.exceptions.ProductNotFoundException;
import org.example.progetto.exceptions.UserNotFoundException;
import org.example.progetto.repositories.CartRepository;
import org.example.progetto.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
)
@RestController
@RequestMapping("/cart")
public class CartController {


    @Autowired
    private CartService cartService;
    @Autowired
    private CartRepository cartRepository;


    @PreAuthorize("isAuthenticated()")
    @PostMapping("/add")
    public ResponseEntity<Map<String, String>> addToCart(
            @RequestParam @NotNull @Positive int idProdotto, Authentication authentication) {

        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");


        try {
            cartService.addToCart(email, idProdotto);

            return creaRisposta("Prodotto aggiunto al carrello con successo.", HttpStatus.OK);
        } catch (UserNotFoundException e) {
            return creaRisposta("Utente non trovato.", HttpStatus.NOT_FOUND);
        } catch (ProductNotFoundException e) {
            return creaRisposta("Prodotto non trovato.", HttpStatus.NOT_FOUND);
        } catch (InvalidQuantityException e) {
            return creaRisposta(e.getMessage(), HttpStatus.BAD_REQUEST);
        } //catch (Exception e) {
        // e.printStackTrace(); // Log dell'errore per il debugging
        // return creaRisposta("Errore interno del server.", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    //Si invia una risposta html e non direttamente la stringa, altrimenti si avrebbe un errore lato frontend
    private ResponseEntity<Map<String, String>> creaRisposta(String message, HttpStatus status) {
        Map<String, String> response = new HashMap<>();
        response.put("message", message);
        return ResponseEntity.status(status).body(response);
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/removeAll")
    public ResponseEntity<Map<String, String>> emptyCart(Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        try {
            cartService.emptyCart(email);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Carrello svuotato con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidOperationException e) {
            throw new RuntimeException(e);
        }
    }


    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/removeItem")
    public ResponseEntity<Map<String, String>> removeItem(
            @RequestParam @NotNull @Positive int idProdotto, Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        System.out.println("Rimozione prodotto dal carrello: " + email + ";"+ idProdotto);

        try {
            cartService.rimuoviDalCarrello(email, idProdotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Prodotto rimosso dal carrello con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidOperationException e) {

            System.out.println("Errore rimozione prodotto dal carrello: " + email + ";"+ idProdotto);

            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }

    // Diminuisce la quantità di un prodotto nel carrello
    @PutMapping("/minus")
    public ResponseEntity<Map<String, String>> decreaseProductQuantity(
            @RequestParam @NotNull @Positive int idProdotto, Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        try {
            cartService.decreaseProductQuantity(email, idProdotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Quantità ridotta con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidOperationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        }
    }



    @PreAuthorize("isAuthenticated()")
    @GetMapping("/items")
    public ResponseEntity<List<ProductInCartDTO>> getCartItems(Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        try {
            List<ProductInCartDTO> cartItems = cartService.getCartItemsByEmail(email);
            return ResponseEntity.ok(cartItems);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(404).body(Collections.emptyList());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(Collections.emptyList());
        }
    }


    // Effettua un ordine
    @PostMapping("/buy")
    public ResponseEntity<Map<String, String>> buyCart(@RequestParam @NotNull @Min(1) int metodoPagamento, @RequestParam @NotNull String indirizzoSpedizione, Authentication authentication) {

        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        try {
            System.out.println("Inizio effettuazione ordine: " + email);
            cartService.buyCart(email, metodoPagamento, indirizzoSpedizione);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Ordine effettuato con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidOperationException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            System.out.println("Errore effettuazione ordine: " + email);
            return ResponseEntity.status(400).body(response);
        }
    }



    // Aumento la quantità di un prodotto nel carrello (plus adding)
    @PutMapping("/plus")
    public ResponseEntity<Map<String, String>> incrementProductQuantity(
            @RequestParam @NotNull @Positive int idProdotto, Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        try {
            System.out.println("Richiesta di plus ricevuta:" + email + ";"+ idProdotto);
            cartService.incrementProductQuantity(email, idProdotto);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Quantità aumentata con successo.");
            return ResponseEntity.ok(response);
        } catch (UserNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Utente non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (ProductNotFoundException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Prodotto non trovato.");
            return ResponseEntity.status(404).body(response);
        } catch (InvalidQuantityException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(400).body(response);
        } catch (InvalidOperationException e) {
            throw new RuntimeException(e);
        }
    }



}