package org.example.progetto.services;


//import jakarta.transaction.Transactional;
import org.example.progetto.DTO.ProductUpdateRequest;
import org.example.progetto.DTO.UserUpdateRequest;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.User;
import org.example.progetto.repositories.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("productService")
@Transactional
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public List<Product> showAllProducts() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = false)
    public Product addProduct(Product product) {
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
        product.setPrice(updateRequest.getPrice());
        product.setQuantity(updateRequest.getQuantity());
        product.setType(updateRequest.getType());

        return productRepository.save(product);
    }

}
