package org.example.progetto.repositories;

import org.example.progetto.entities.Cart;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.ProductInCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Set;

@Repository
public interface ProductInCartRepository extends JpaRepository<ProductInCart, Integer> {

    ProductInCart findByCarrelloAndProdotto(Cart carrello, Product prod);
    ProductInCart findByCarrelloAndProdottoId(Cart carrello, int prod);

    @Query("SELECT cp FROM ProductInCart cp WHERE cp.cart.idCarrello = :idCarrello")
    Set<ProductInCart> findByCarrelloId(@Param("idCarrello") int idCarrello);

    void deleteAllByCarrello(Cart carrello);
}