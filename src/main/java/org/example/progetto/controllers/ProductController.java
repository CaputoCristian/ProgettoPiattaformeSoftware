package org.example.progetto.controllers;

import jakarta.validation.Valid;
import org.example.progetto.DTO.ProductUpdateRequest;
import org.example.progetto.DTO.UserUpdateRequest;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.Shop;
import org.example.progetto.entities.User;
import org.example.progetto.exceptions.*;
import org.example.progetto.services.ProductService;
import org.example.progetto.services.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
)
@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ShopService shopService;

//    @PostMapping("/addProduct")
//    public ResponseEntity addProduct(@RequestBody Product product) {
//        try {
//            Product addedProduct = productService.addProduct(product); //Non serve tornare l'utente se si ha il .ok (lazy method)
//            return ResponseEntity.ok(addedProduct);
//        } catch (BarcodeAlreadyExistException e) {
//            return new ResponseEntity<>("Barcode already exist", HttpStatus.BAD_REQUEST);
//        }
//    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/addProduct")
    public ResponseEntity<?> addProduct(@Valid @RequestBody ProductUpdateRequest request, Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        try {
//            Shop shop = shopService.getShopByUserEmail(email);
//            System.out.println("Shop trovato");
            Product addedProduct = productService.addProduct(email, request);

            return ResponseEntity.ok(addedProduct);
        } catch (ShopNotFoundException e) {
            return new ResponseEntity<>("Shop non trovato per l'utente", HttpStatus.BAD_REQUEST);
        }
    }


    @PreAuthorize("isAuthenticated()")
    @GetMapping("")
    public List<Product> getAll() {
        return productService.showAllProducts();
    }



    @GetMapping("/paged")
    public ResponseEntity getAll(@RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize, @RequestParam(value = "sortBy", defaultValue = "id") String sortBy) {
        List<Product> result = productService.showAllProducts(pageNumber, pageSize, sortBy);
        if ( result.size() <= 0 ) {
            return new ResponseEntity<>("No results!", HttpStatus.OK);
        }
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/{id}")
    public ResponseEntity<?> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request, Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");
        try {
            Product updatedProduct = productService.updateProduct(email, id, request);


            return ResponseEntity.ok(updatedProduct);

        } catch (ShopNotFoundException e) {
            return new ResponseEntity<>("Shop non trovato per l'utente", HttpStatus.BAD_REQUEST);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>("Utente inesistente", HttpStatus.BAD_REQUEST);
        } catch (InvalidOperationException e) {
            return new ResponseEntity<>("Accesso non consentito", HttpStatus.UNAUTHORIZED);

        }

    }

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteProduct(@PathVariable Long id, Authentication authentication) {
        String email = ((JwtAuthenticationToken) authentication).getToken().getClaimAsString("email");

        try {
            productService.deleteProduct(email, id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (ShopNotFoundException e) {
            return ResponseEntity.badRequest().body("Shop non trovato per l'utente");
        } catch (InvalidOperationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Accesso non consentito");
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Prodotto non trovato");
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(
            @RequestParam("q") String query,
            @RequestParam(name = "minPrice", required = false) Float minPrice,
            @RequestParam(name = "maxPrice", required = false) Float maxPrice,
            @RequestParam(name = "availableOnly", required = false, defaultValue = "false") Boolean availableOnly) {

        System.out.println("Ricevuta richiesta con: " + query + " " + minPrice + " " + maxPrice + " " + availableOnly);

        try {
            List<Product> results = productService.searchProducts(query, minPrice, maxPrice, availableOnly);

            System.out.println("Invio risultati: " + results.toString());

            return ResponseEntity.ok(results);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Parametri non validi");
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
