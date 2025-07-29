package org.example.progetto.controllers;

import jakarta.validation.Valid;
import org.example.progetto.DTO.ProductUpdateRequest;
import org.example.progetto.DTO.UserUpdateRequest;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.Shop;
import org.example.progetto.entities.User;
import org.example.progetto.exceptions.BarcodeAlreadyExistException;
import org.example.progetto.exceptions.ShopNotFoundException;
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
    public ResponseEntity<?> addProduct( @RequestBody ProductUpdateRequest request, Authentication authentication) {
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

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        Product updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }

//    @GetMapping("/search")
//    public ResponseEntity<List<Product>> searchProducts(
//            @RequestParam("q") String query,
//            @RequestParam(value = "minPrice", required = false) Float minPrice,
//            @RequestParam(value = "maxPrice", required = false) Float maxPrice,
//            @RequestParam(value = "availableOnly", defaultValue = "false") boolean availableOnly
//    ) {
//        List<Product> results = productService.advancedSearch(query, minPrice, maxPrice, availableOnly);
//        return ResponseEntity.ok(results);
//    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(
            @RequestParam("q") String query,
            @RequestParam(name = "minPrice", required = false) Float minPrice,
            @RequestParam(name = "maxPrice", required = false) Float maxPrice,
            @RequestParam(name = "availableOnly", required = false) Boolean availableOnly) {

        List<Product> results = productService.searchProducts(query, minPrice, maxPrice, availableOnly != null && availableOnly);
        return ResponseEntity.ok(results);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errors = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
}
