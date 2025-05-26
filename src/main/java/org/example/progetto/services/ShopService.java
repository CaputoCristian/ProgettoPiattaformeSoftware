package org.example.progetto.services;

import org.example.progetto.entities.Product;
import org.example.progetto.entities.Shop;
import org.example.progetto.repositories.ProductRepository;
import org.example.progetto.repositories.ShopRepository;
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

}
