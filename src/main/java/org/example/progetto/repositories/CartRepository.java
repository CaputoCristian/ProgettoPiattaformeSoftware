package org.example.progetto.repositories;

import org.example.progetto.entities.Cart;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Integer> {
    Cart findByUserId(int userId);
    @Query("SELECT c FROM Cart c LEFT JOIN FETCH c.cartProducts WHERE c.userId = :userId")
    Cart findByUserIdWithProducts(@Param("userId") int userId);


}