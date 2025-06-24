package org.example.progetto.controllers;

import org.example.progetto.entities.Product;
import org.example.progetto.entities.Purchase;
import org.example.progetto.entities.Shop;
import org.example.progetto.exceptions.BarcodeAlreadyExistException;
import org.example.progetto.exceptions.UserAlreadyHasShopException;
import org.example.progetto.services.ShopService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
)
@RequestMapping("/shop")
public class ShopController {

    private ShopService shopService;

    @PostMapping
    public ResponseEntity Create(@RequestBody Shop shop) {
        try {
            Shop createdShop = shopService.addShop(shop);
            return ResponseEntity.ok(createdShop);
        } catch (UserAlreadyHasShopException e) {
            return new ResponseEntity<>("This user already has a shop", HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    public List<Product> showAllProducts(@RequestBody Shop shop) {
        return shopService.getProductFromShop(shop.getId());
    }

}
