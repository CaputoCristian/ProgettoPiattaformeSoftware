package org.example.progetto.services;


//import jakarta.transaction.Transactional;
import org.example.progetto.DTO.ProductUpdateRequest;
import org.example.progetto.DTO.UserUpdateRequest;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.Shop;
import org.example.progetto.entities.User;
import org.example.progetto.repositories.ProductRepository;
import org.example.progetto.repositories.ShopRepository;
import org.example.progetto.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service("productService")
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<Product> showAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = false)
    public Product addProduct(String email, ProductUpdateRequest request) {

        Optional<Shop> shop = shopRepository.findBySellerEmail(email);

        Product product = new Product();

        product.setName(request.getName ());
        product.setBrand(request.getBrand());
        product.setDescription(request.getDescription());
        product.setPrice(BigDecimal.valueOf(request.getPrice()));
        product.setQuantity(request.getQuantity());

        if (!shop.isPresent()) {

            Shop newShop = new Shop();
            newShop.setSeller(userRepository.findByEmail(email));
            shopRepository.save(newShop);
            product.setShop(newShop);
        }else{
            product.setShop(shop.get());
        }

        productRepository.save(product);
        return product;
    }

    @Transactional(readOnly = true)
    public List<Product> showAllProducts(int pageNumber, int pageSize, String sortBy) {
        Pageable paging = PageRequest.of(pageNumber, pageSize, Sort.by(sortBy));
        Page<Product> pagedResult = productRepository.findAll(paging);
        if ( pagedResult.hasContent() ) {
            return pagedResult.getContent();
        }
        else {
            return new ArrayList<>();
        }
    }

    @Transactional(readOnly = true)
    public List<Product> showProductsByName(String name) {
        return productRepository.findByNameContaining(name);
    }

    @Transactional(readOnly = true)
    public List<Product> showProductByPriceBetween(Float minPrice, Float maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    @Transactional(readOnly = true)
    public List<Product> showProductsByType(String type) {
        return productRepository.findByType(type);
    }

    @Transactional(readOnly = false)
    public Product updateProduct(Long productId, ProductUpdateRequest updateRequest) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Prodotto non trovato"));

        product.setName(updateRequest.getName ());
        product.setBrand(updateRequest.getBrand());
        product.setDescription(updateRequest.getDescription());
        product.setPrice(BigDecimal.valueOf(updateRequest.getPrice()));
        product.setQuantity(updateRequest.getQuantity());

        return productRepository.save(product);
    }

//    public List<Product> searchByMultipleKeywords(String query) {
//        String[] words = query.toLowerCase().split("\\s+");
//        Set<Product> results = new HashSet<>();
//
//        for (String word : words) {
//            results.addAll(productRepository.searchByKeyword(word));
//        }
//
//        return new ArrayList<>(results);
//    }
//
//    public List<Product> advancedSearch(String query, Float minPrice, Float maxPrice, boolean availableOnly) {
//        List<Product> base = searchByMultipleKeywords(query);
//
//        return base.stream()
//                .filter(p -> minPrice == null || p.getPrice() >= minPrice)
//                .filter(p -> maxPrice == null || p.getPrice() <= maxPrice)
//                .filter(p -> !availableOnly || p.getQuantity() != null && p.getQuantity() > 0)
//                .collect(Collectors.toList());
//    }



    public List<Product> searchProducts(String query, Float minPrice, Float maxPrice, boolean availableOnly) {
        return productRepository.search(query.toLowerCase(), minPrice, maxPrice, availableOnly);
    }


}
