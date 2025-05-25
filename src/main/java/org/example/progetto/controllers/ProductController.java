package org.example.progetto.controllers;

import org.example.progetto.DTO.ProductUpdateRequest;
import org.example.progetto.DTO.UserUpdateRequest;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.User;
import org.example.progetto.exceptions.BarcodeAlreadyExistException;
import org.example.progetto.services.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity addProduct(@RequestBody Product product) {
        try {
            Product addedProduct = productService.addProduct(product); //Non serve tornare l'utente se si ha il .ok (lazy method)
            return ResponseEntity.ok(addedProduct);
        } catch (BarcodeAlreadyExistException e) {
            return new ResponseEntity<>("Barcode already exist", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public List<Product> showAllProducts() {
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
    public ResponseEntity<Product> updateProduct(@PathVariable Long id,
                                              @RequestBody ProductUpdateRequest request) {
        Product updatedProduct = productService.updateProduct(id, request);
        return ResponseEntity.ok(updatedProduct);
    }
}
