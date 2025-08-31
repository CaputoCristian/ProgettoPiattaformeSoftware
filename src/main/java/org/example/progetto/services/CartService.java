package org.example.progetto.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.progetto.DTO.ProductInCartDTO;
import org.example.progetto.entities.*;
import org.example.progetto.exceptions.InvalidOperationException;
import org.example.progetto.exceptions.InvalidQuantityException;
import org.example.progetto.exceptions.ProductNotFoundException;
import org.example.progetto.exceptions.UserNotFoundException;
import org.example.progetto.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Transactional
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private PurchaseRepository purchaseRepository;
    @Autowired
    private SaleAlertRepository saleAlertRepository;
    @Autowired
    private ProductInSaleRepository productInSaleRepository;
    @Autowired
    private ProductInPurchaseRepository productInPurchaseRepository;
    @Autowired
    private ProductInCartRepository productInCartRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public void addToCart(String idUtente, int idProdotto )
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException {

        // Recupero l'utente dal database.
        User user = userRepository.findByEmail(idUtente);
        if (user == null) {
            throw new UserNotFoundException("Cliente non trovato!");
        }

        // Recupero o creo il carrello dell'utente
        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(user.getId());
            cart = cartRepository.save(cart);
        } else {
            // Lock del carrello per evitare accessi concorrenti
            entityManager.lock(cart, LockModeType.PESSIMISTIC_WRITE);
        }

        Product prod = productRepository.findAll()  // Recuperi la lista completa dei prodotti
                .stream()  // Crei uno stream dalla lista
                .filter(p -> p.getId() == idProdotto)  // Filtro per idProdotto
                .findFirst()  // Trova il primo prodotto che soddisfa il filtro
                .orElseThrow(() -> new ProductNotFoundException("Prodotto con ID " + idProdotto + " non trovato"));

        // Verifico la disponibilità del prodotto
        int disponibilitaProd = prod.getQuantity();

        if (disponibilitaProd <= 0) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: " +
                    "il prodotto non è disponibile per la quantità desiderata");
        }

        // Controllo se il prodotto è già presente nel carrello

        ProductInCart cp = productInCartRepository.findByCartAndProduct(cart,prod);
        if (cp != null) {
            // Lock dell'elemento del carrello
            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);

            // Aggiorno la quantità
            int nuovaQuantita = cp.getQuantity() + 1;
            if (nuovaQuantita <= prod.getQuantity()) {
                cp.setQuantity(nuovaQuantita);
                productInCartRepository.save(cp);
            } else {
                throw new InvalidQuantityException("Quantità totale superiore alla disponibilità del prodotto");
            }
        } else {
            // Aggiungo il prodotto al carrello
            ProductInCart aggiunta = new ProductInCart();
            aggiunta.setCart(cart);
            aggiunta.setProduct(prod);
            aggiunta.setProductId(prod.getId());
            aggiunta.setQuantity(1); //Prima aggiunta
            aggiunta.setCartId(cart.getCartId());

            productInCartRepository.save(aggiunta);
        }
    }

    @Transactional
    public void incrementProductQuantity(String email, int idProdotto)
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException, InvalidOperationException {

        User cliente = userRepository.findByEmail(email);
        if (cliente == null) {
            throw new UserNotFoundException("Cliente non trovato!");
        }

        int idUtente = cliente.getId();

        // Recupero e locko il carrello
        Cart carrello = cartRepository.findByUserId(idUtente);
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko l'elemento del carrello
        ProductInCart cp = productInCartRepository.findByCartAndProductId(carrello, idProdotto);
        if (cp == null) {
            throw new InvalidOperationException("Il prodotto non è presente nel carrello.");
        }
        entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);


        Product prod = productRepository.findAll()  // Recuperi la lista completa dei prodotti
                .stream()  // Crei uno stream dalla lista
                .filter(p -> p.getId() == idProdotto)  // Filtro per idProdotto
                .findFirst()  // Trova il primo prodotto che soddisfa il filtro
                .orElseThrow(() -> new ProductNotFoundException("Prodotto con ID " + idProdotto + " non trovato"));

        int disponibilitaProd = prod.getQuantity();

        if (cp.getQuantity() + 1 > disponibilitaProd) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: " +
                    "il prodotto non è disponibile per la quantità desiderata");
        }
        cp.setQuantity(cp.getQuantity() + 1);
        productInCartRepository.save(cp);
    }

    @Transactional
    public void rimuoviDalCarrello(String email, int prodottoID)
            throws UserNotFoundException, InvalidOperationException {
        try {

            User cliente = userRepository.findByEmail(email);
            if (cliente == null) {
                throw new UserNotFoundException("Cliente non trovato!");
            }

            // Recupero e locko il carrello
            Cart carrello = cartRepository.findByUserId(cliente.getId());
            if (carrello == null) {
                throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
            }

            // Utilizziamo la query personalizzata per eliminare direttamente
            productInCartRepository.deleteByCartAndProductId(carrello, prodottoID);

            // Forza il flush per assicurarsi che la modifica venga scritta nel database
            entityManager.flush();

            // Log per debug
//            System.out.println("Prodotto " + prodottoID + " rimosso dal carrello dell'utente " + email);
        } catch (Exception e) {
//            System.err.println("Errore durante la rimozione del prodotto: " + e.getMessage());
            throw new InvalidOperationException("Errore durante la rimozione del prodotto: " + e.getMessage());
        }
}

    @Transactional
    public void decreaseProductQuantity(String email, int idProdotto)
            throws UserNotFoundException, InvalidOperationException {
        try {

            User cliente = userRepository.findByEmail(email);
            if (cliente == null) {
                throw new UserNotFoundException("Cliente non trovato!");
            }

            // Recupero e locko il carrello
            Cart carrello = cartRepository.findByUserId(cliente.getId());
            if (carrello == null) {
                throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
            }

            ProductInCart cp = productInCartRepository.findByCartAndProductId(carrello, idProdotto);
            if (cp == null) {
                throw new InvalidOperationException("Prodotto non trovato nel carrello.");
            }

            if (cp.getQuantity() <= 1) {
                // Se la quantità è 1 o meno, eliminiamo direttamente il prodotto
                productInCartRepository.deleteByCartAndProductId(carrello, idProdotto);
            } else {
                cp.setQuantity(cp.getQuantity() - 1);
                productInCartRepository.save(cp);
            }

            // Forza il flush per assicurarsi che le modifiche vengano scritte nel database
            entityManager.flush();

            System.out.println("Quantità aggiornata per il prodotto " + idProdotto);

        } catch (Exception e) {

            System.err.println("Errore durante la diminuzione della quantità: " + e.getMessage());
            throw new InvalidOperationException("Errore durante la diminuzione della quantità: " + e.getMessage());
        }

    }

    @Transactional
    public void emptyCart(String email) throws UserNotFoundException, InvalidOperationException {

        try {

                User cliente = userRepository.findByEmail(email);
            if (cliente == null) {
                throw new UserNotFoundException("Cliente non trovato!");
            }

            // Recupero e locko il carrello
            Cart carrello = cartRepository.findByUserId(cliente.getId());
            if (carrello == null) {
                throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
            }


            // Utilizziamo la query personalizzata per eliminare direttamente
            productInCartRepository.deleteByCart(carrello);
            entityManager.flush();


//            System.out.println("Carrello svuotato per l'utente: "+ email);
        } catch (Exception e) {
//            System.err.println("Errore durante lo svuotamento del carrello: " + e.getMessage());
            throw new InvalidOperationException("Errore durante lo svuotamento del carrello: " + e.getMessage());
        }

    }

    @Transactional
    public void buyCart(String email, int paymentMethod, String shippingAddress)
            throws UserNotFoundException, InvalidOperationException {

        // Fase 1: Verifica preliminare e lock
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("Cliente non trovato!");
        }

        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            throw new InvalidOperationException("Carrello non trovato.");
        }
        entityManager.lock(cart, LockModeType.PESSIMISTIC_WRITE);

        List<ProductInCart> userProducts = productInCartRepository.findByCartId(cart.getCartId());
        if (userProducts.isEmpty()) {
            throw new InvalidOperationException("Il carrello è vuoto.");
        }

        // Fase 2: Verifica disponibilità e lock prodotti
        Map<Product, Integer> productQuantityMap = new HashMap<>();
        for (ProductInCart cp : userProducts) {
            Product product = cp.getProduct();
            entityManager.lock(product, LockModeType.PESSIMISTIC_WRITE);

            if (product.getQuantity() < cp.getQuantity()) {
                // Rollback automatico grazie all'annotazione @Transactional
                throw new InvalidOperationException(
                        "Quantità non disponibile per il prodotto: " + product.getName());
            }

            productQuantityMap.put(product, cp.getQuantity());
        }

        // Fase 3: Verifica pagamento
        if (!isPaymentMethodValid(paymentMethod)) {
            throw new InvalidOperationException("Metodo di pagamento non valido o rifiutato.");
        }

        try {
            // Fase 4: Creazione e salvataggio dell'acquisto
            Purchase purchase = new Purchase();
            purchase.setBuyer(user);
            purchase.setTime(LocalDateTime.now());
            purchase = purchaseRepository.save(purchase);
            entityManager.flush(); // Forza il flush per rilevare eventuali errori

            BigDecimal totalePerAcquisto = BigDecimal.ZERO;

            // Fase 5: Creazione e salvataggio dei ProductInPurchase/ProductInSale
            Map<User, List<ProductInSale>> venditePerVenditore = new HashMap<>();
            List<ProductInPurchase> acquisti = new ArrayList<>();
            for (Map.Entry<Product, Integer> entry : productQuantityMap.entrySet()) {

                Product product = entry.getKey();
                int quantity = entry.getValue();

                // Creazione ProductInPurchase
                totalePerAcquisto = totalePerAcquisto.add((BigDecimal.valueOf(quantity ).multiply(product.getPrice())));

                // Aggiorna la quantità del prodotto
                product.setQuantity(product.getQuantity() - quantity);
                productRepository.save(product);

                ProductInPurchase pip = new ProductInPurchase();
                pip.setProduct(product);
                pip.setQuantity(quantity);
                pip.setRelatedPurchase(purchase);
                acquisti.add(pip);

                // Inserimento dati in mappa Seller-Avviso
                User venditore = product.getShop().getSeller();

                //Creazione prodotto da aggiungere
                ProductInSale pis = new ProductInSale();
                pis.setProduct(product);
                pis.setQuantity(quantity);
                pis.setPrice(product.getPrice());

                //Se non è nella mappa lo aggiunge
                if (!venditePerVenditore.containsKey(venditore)) {
                    venditePerVenditore.put(venditore, new ArrayList<>());

                }
                venditePerVenditore.get(venditore).add(pis);

            }


            productInPurchaseRepository.saveAll(acquisti);
            entityManager.flush(); // Forza il flush per rilevare eventuali errori

            // Fase 6: Aggiornamento dell'acquisto con i prodotti
            purchase.setProductsInPurchase(acquisti);
            purchase.setTotalPrice(totalePerAcquisto);
            purchaseRepository.save(purchase);

            for (Map.Entry<User, List<ProductInSale>> entry : venditePerVenditore.entrySet()) {
                User venditore = entry.getKey();
                List<ProductInSale> prodotti = entry.getValue();

                BigDecimal totale = prodotti.stream()
                        .map(p -> p.getProduct().getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                //Creazione vendita
                SaleAlert sa = new SaleAlert();
                sa.setSeller(venditore);
//              sa.setPurchase(purchase);
                sa.setShippingAddress(shippingAddress);
                sa.setTotalAmount(totale.floatValue());
                saleAlertRepository.save(sa);

                //Creazione prodotti da aggiungerea alla vendita
                for (ProductInSale pis : prodotti) {
                    pis.setSaleAlert(sa);
                    productInSaleRepository.save(pis);
                }

                //Assegnamento valori, postumo per vincoli di relazione.

                sa.setProducts(prodotti);
                sa.setViewed(false);

            }

            // Fase 8: Pulizia carrello
            productInCartRepository.deleteAll(userProducts);

        } catch (Exception e) {
            // Il rollback avverrà automaticamente grazie all'annotazione @Transactional
            throw new InvalidOperationException("Errore durante l'acquisto: " + e.getMessage());
        }
    }


    private boolean isPaymentMethodValid(int paymentMethod) {
        // Simulazione approvazione casuale
        return new Random().nextBoolean();
    }

    @Transactional
    public List<ProductInCartDTO> getCartItemsByEmail(String email) throws UserNotFoundException {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("Cliente non trovato!");
        }

        Cart cart = cartRepository.findByUserId(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setUserId(user.getId());
            cart = cartRepository.save(cart);
        }

        List<ProductInCart> prodottiUser = productInCartRepository.findByCartId(cart.getCartId())
                .stream()
                .filter(p -> p.getQuantity() > 0)
                .collect(Collectors.toList());

        return prodottiUser.stream()
                .map(cp -> {
                    ProductInCartDTO dto = new ProductInCartDTO();
                    dto.setId(cp.getProduct().getId());
                    dto.setName(cp.getProduct().getName());
                    dto.setPrice(cp.getProduct().getPrice().floatValue());
                    dto.setQuantity(cp.getQuantity());
                    return dto;
                })
                .collect(Collectors.toList());
    }


}
