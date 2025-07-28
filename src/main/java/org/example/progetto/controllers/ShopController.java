package org.example.progetto.controllers;

import org.example.progetto.entities.Product;
import org.example.progetto.entities.Shop;
//import org.example.progetto.exceptions.UserAlreadyHasShopException;
import org.example.progetto.exceptions.ShopNotFoundException;
import org.example.progetto.services.ShopService;
import org.example.progetto.exceptions.UserNotFoundException;
import org.example.progetto.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
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

    @Autowired
    private ShopService shopService;
    @Autowired
    private UserService userService;

//    @PostMapping
//    public ResponseEntity Create(@RequestBody Shop shop) {
//        try {
//            Shop createdShop = shopService.addShop(shop);
//            return ResponseEntity.ok(createdShop);
//        } catch (UserAlreadyHasShopException e) {
//            return new ResponseEntity<>("This user already has a shop", HttpStatus.BAD_REQUEST);
//        }
//    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("allProducts")
    public ResponseEntity<?> getProductListFromUser(Authentication authentication) throws UserNotFoundException {

        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String email = token.getToken().getClaimAsString("email");

        Shop shop = new Shop();

        try{
            shop = shopService.getShopByUserEmail(email);

        } catch (ShopNotFoundException e) {

            shop.setSeller(userService.findByEmail(email));
            shop.setProducts(new ArrayList<Product>());
            shopService.addShop(shop);
        }


//        if (shop == null) {
//            //return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Shop not found for this user.");
//          shop.setSeller(userService.findByEmail(email));
//          shop.setProducts(new ArrayList<Product>());
//
//          shopService.addShop(shop);
//        }

        List<Product> products = shopService.getProductFromShop(shop.getId());
//
//        if (products.isEmpty()) {
//            return ResponseEntity.ok("No products available in this shop.");
//        }

        return ResponseEntity.ok(products);

    }

    @GetMapping("/user/{userId}/products")
    public List<Product> getProductsByUserId(@PathVariable Integer userId) {
        Shop shop = shopService.getShopByUserId(userId);
        return shopService.getProductFromShop(shop.getId());
    }




}
