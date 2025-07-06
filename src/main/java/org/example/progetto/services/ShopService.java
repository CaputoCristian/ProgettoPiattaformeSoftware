package org.example.progetto.services;

import jakarta.persistence.EntityNotFoundException;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.Shop;
import org.example.progetto.entities.User;
import org.example.progetto.exceptions.ShopNotFoundException;
import org.example.progetto.repositories.ProductRepository;
import org.example.progetto.repositories.ShopRepository;
import org.example.progetto.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.List;

@Service("shopService")
@Transactional
public class ShopService {

    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Shop> showAllShop() {
        return shopRepository.findAll();
    }

    @Transactional(readOnly = false)
    public Shop addShop(Shop shop) {
        shopRepository.save(shop);
        return shop;
    }

    @Transactional(readOnly = true)
    public List<Shop> showUsersShop(Integer sellerId) {
        return shopRepository.findBySeller_Id(sellerId);
    }

    public List<Product> getProductFromShop(Integer id) {
        return shopRepository.findById(id).getProducts(); //TODO RIMETTERE CON LA QUERY CHE RITORNA IL PRODOTTO E NON IL LISTA DEI PRODOTTI
    }

    public List<Product> getProductListFromUser(User seller) {
        return shopRepository.findBySeller(seller).getProducts();
    }

//    public Shop getShopByUserEmail(String email) throws UserNotFoundException {
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            throw new UserNotFoundException("User not found");
//        }
//        Shop shop = shopRepository.findBySeller(user);
//        if (shop == null) {
//            throw new EntityNotFoundException("Shop not found for user");
//        }
//        return shop;
//    }

    public Shop getShopByUserEmail(String email) {
        return shopRepository.findBySellerEmail(email)
                .orElseThrow(() -> new ShopNotFoundException("Nessuno shop associato all'email: " + email));
    }


    public Shop getShopByUserId(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        Shop shop = shopRepository.findBySeller(user);
        if (shop == null) {
            throw new EntityNotFoundException("Shop not found for user");
        }
        return shop;
    }

}
