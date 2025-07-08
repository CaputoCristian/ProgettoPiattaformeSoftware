package org.example.progetto.repositories;

import org.example.progetto.entities.Cart;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.ProductInCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface ProductInCartRepository extends JpaRepository<ProductInCart, Integer> {

    ProductInCart findByCartAndProduct(Cart cart, Product prod);
    ProductInCart findByCartAndProductId(Cart cart, int prod);

    @Query("SELECT cp FROM ProductInCart cp JOIN FETCH cp.product WHERE cp.cart.cartId = :cartId")
    List<ProductInCart> findByCartId(@Param("cartId") int cartId);

    void deleteAllByCart(Cart cart);
}