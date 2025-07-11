package org.example.progetto.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.progetto.DTO.ProductInCartDTO;
import org.example.progetto.entities.*;
import org.example.progetto.repositories.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
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

//    @Autowired
//    private transazioneRepository transazioneRepository;
//
//    @Autowired
//    private spedizioneRepository spedizioneRepository;

    @Autowired
    private ProductInPurchaseRepository productInPurchaseRepository;


    @Autowired
    private ProductInCartRepository productInCartRepository;

    @Autowired
    private EntityManager entityManager;
//    @Autowired
//    private com.progettopswcp.ProgettoPSW.repositories.metododipagamentoRepository metododipagamentoRepository;

    private static final Random RANDOM = new Random();

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


//            entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);
//
//            // Recupero e locko l'elemento del carrello
//            ProductInCart cp = productInCartRepository.findByCartAndProductId(carrello, prodottoID);
//            if (cp == null) {
//                throw new InvalidOperationException("Il prodotto non è presente nel carrello.");
//            }
//            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);
//
//            // Rimuovo il prodotto dal carrello
//            productInCartRepository.delete(cp);

            // Utilizziamo la query personalizzata per eliminare direttamente
            productInCartRepository.deleteByCartAndProductId(carrello, prodottoID);


            // Forza il flush per assicurarsi che la modifica venga scritta nel database
            entityManager.flush();

            // Log per debug
            System.out.println("Prodotto " + prodottoID + " rimosso dal carrello dell'utente " + email);
        } catch (Exception e) {
            System.err.println("Errore durante la rimozione del prodotto: " + e.getMessage());
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


//            entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);
//
//            // Recupero e locko l'elemento del carrello
//            ProductInCart cp = productInCartRepository.findByCartAndProductId(carrello, idProdotto);
//            if (cp == null) {
//                throw new InvalidOperationException("Il prodotto non è presente nel carrello.");
//            }
//            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);
//            ProductInCart cp = productInCartRepository.findByCartAndProductId(carrello, idProdotto);
//            if (cp == null) {
//                throw new InvalidOperationException("Prodotto non trovato nel carrello.");
//            }
//
//            if (!cp.getCart().equals(carrello)) {
//                throw new InvalidOperationException("Operazione non valida: il carrello non corrisponde");
//            }
//
//            // Decremento o rimuovo il prodotto dal carrello
//            if (cp.getQuantity() > 1) {
//                cp.setQuantity(cp.getQuantity() - 1);
//                productInCartRepository.save(cp);
//            } else {
//                productInCartRepository.delete(cp);
//            }


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
//            entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);
//
//            // Recupero e locko tutti gli elementi del carrello
//            List<ProductInCart> cartProducts = productInCartRepository.findByCartId(carrello.getCartId());
//            for (ProductInCart cp : cartProducts) {
//                entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);
//            }
//
//            // Svuoto il carrello
//            productInCartRepository.deleteAllByCart(carrello);

            // Utilizziamo la query personalizzata per eliminare direttamente
            productInCartRepository.deleteByCart(carrello);
            entityManager.flush();


            System.out.println("Carrello svuotato per l'utente: " + email);
        } catch (Exception e) {
            System.err.println("Errore durante lo svuotamento del carrello: " + e.getMessage());
            throw new InvalidOperationException("Errore durante lo svuotamento del carrello: " + e.getMessage());
        }

    }

    /// Versione con lock più sicuri

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

            // Fase 5: Creazione e salvataggio dei ProductInPurchase
            List<ProductInPurchase> acquisti = new ArrayList<>();
            for (Map.Entry<Product, Integer> entry : productQuantityMap.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                totalePerAcquisto = totalePerAcquisto.add((BigDecimal.valueOf(quantity ).multiply(product.getPrice())));
                // Aggiorna la quantità del prodotto
                product.setQuantity(product.getQuantity() - quantity);
                productRepository.save(product);

                ProductInPurchase pip = new ProductInPurchase();
                pip.setProduct(product);
                pip.setQuantity(quantity);
                pip.setRelatedPurchase(purchase);
                acquisti.add(pip);
            }

            productInPurchaseRepository.saveAll(acquisti);
            entityManager.flush(); // Forza il flush per rilevare eventuali errori

            // Fase 6: Aggiornamento dell'acquisto con i prodotti
            purchase.setProductsInPurchase(acquisti);
            purchase.setTotalPrice(totalePerAcquisto);
            purchaseRepository.save(purchase);

            // Fase 7: Creazione notifiche venditori
            Map<User, List<ProductInCart>> venditePerVenditore = new HashMap<>();
            for (ProductInCart cp : userProducts) {
                User venditore = cp.getProduct().getShop().getSeller();
                venditePerVenditore.computeIfAbsent(venditore, k -> new ArrayList<>()).add(cp);
            }

            for (Map.Entry<User, List<ProductInCart>> entry : venditePerVenditore.entrySet()) {
                User venditore = entry.getKey();
                List<ProductInCart> prodotti = entry.getValue();

                BigDecimal totale = prodotti.stream()
                        .map(p -> p.getProduct().getPrice().multiply(BigDecimal.valueOf(p.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                SaleAlert alert = new SaleAlert();
                alert.setSeller(venditore);
                alert.setPurchase(purchase);
                alert.setShippingAddress(shippingAddress);
                alert.setTotalAmount(totale.floatValue());
                saleAlertRepository.save(alert);
            }

            // Fase 8: Pulizia carrello
            productInCartRepository.deleteAll(userProducts);

        } catch (Exception e) {
            // Il rollback avverrà automaticamente grazie all'annotazione @Transactional
            throw new InvalidOperationException("Errore durante l'acquisto: " + e.getMessage());
        }
    }














//    @Transactional
//    public void buyCart(String email, int paymentMethod, String shippingAddress)
//            throws UserNotFoundException, InvalidOperationException {
//
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            throw new UserNotFoundException("Cliente non trovato!");
//        }
//        int userId = user.getId();
//
//        // Recupero e locko il carrello
//        Cart cart = cartRepository.findByUserId(userId);
//        if (cart == null) {
//            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
//        }
//        entityManager.lock(cart, LockModeType.PESSIMISTIC_WRITE);
//
//        // Recupero e locko gli elementi del carrello
//        List<ProductInCart> userProducts = productInCartRepository.findByCartId(cart.getCartId());
//        if (userProducts.isEmpty()) {
//            throw new InvalidOperationException("Il carrello è vuoto. Aggiungi prodotti prima di procedere all'ordine.");
//        }
//
//        // Ordino gli elementi per evitare deadlock
//        userProducts.sort(Comparator.comparingInt(ProductInCart::getId));
//
//        //Prodotti da ordinare
//        List<ProductInPurchase> acquisti = new ArrayList<>();
//
//        if (!isPaymentMethodValid(paymentMethod)) {
//            throw new InvalidOperationException("Metodo di pagamento non valido o rifiutato.");
//        }
//
//        // Creazione dell'ordine e della transazione
//        Purchase purchase = new Purchase();
//
//        // Verifico la disponibilità e locko i prodotti
//        for (ProductInCart cp : userProducts) {
//            // Lock dell'elemento del carrello
//            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);
//
////          Product product = productRepository.findById(cp.getId());
//            Product product = cp.getProduct();
//
//            // Lock del prodotto
//            entityManager.lock(product, LockModeType.PESSIMISTIC_WRITE);
//
//            // Verifico la disponibilità
//            if (product.getQuantity() < cp.getQuantity()) {
//                throw new InvalidOperationException("La quantità del prodotto '" + product.getName() + "' non è sufficiente per completare l'ordine.");
//            }
//
//            // Decremento la disponibilità del prodotto
//            product.setQuantity(product.getQuantity() - cp.getQuantity());
//            productRepository.save(product);
//
//            //Conversione da productInCart e ProductInPurchase
//            ProductInPurchase pip = new ProductInPurchase();
//            pip.setProduct(cp.getProduct()); // oggetto intero, non nome
//            pip.setQuantity(cp.getQuantity()); // intero, non stringa
//            pip.setRelatedPurchase(purchase);
//            acquisti.add(pip);
//
//            // Salva i prodotti dell'acquisto
//            productInPurchaseRepository.saveAll(acquisti);
//
//            // Aggiorna l'acquisto con i prodotti
//            purchase.setProductsInPurchase(acquisti);
//            purchaseRepository.save(purchase);
//
//
//        }
//
////      SaleAllert vendita = new SaleAllert();
//
//        //TODO set/gestione metodo di pagamento
//
//        purchase.setBuyer(user);
//        purchase.setTime(LocalDateTime.now());
//        purchase.setProductsInPurchase(acquisti);
//        purchaseRepository.save(purchase); // salva tutto grazie al cascade MERGE
//
//        productInPurchaseRepository.saveAll(acquisti); // se non fai cascading su persist
//
//        userProducts.sort(Comparator.comparingInt(ProductInCart::getId));
//        Map<User, List<ProductInCart>> venditePerVenditore = new HashMap<>();
//
//        // Verifica disponibilità prodotti e costruzione struttura per alert
//        for (ProductInCart cp : userProducts) {
//            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);
//
//            Product product = productRepository.findById(cp.getProduct().getId());
//            entityManager.lock(product, LockModeType.PESSIMISTIC_WRITE);
//
//            if (product.getQuantity() < cp.getQuantity()) {
//                throw new InvalidOperationException("La quantità del prodotto '" + product.getName() + "' non è sufficiente.");
//            }
//
//            product.setQuantity(product.getQuantity() - cp.getQuantity());
//            productRepository.save(product);
//
//            User venditore = product.getShop().getSeller();
//            venditePerVenditore.computeIfAbsent(venditore, k -> new ArrayList<>()).add(cp);
//        }
//
//
//        // Creazione notifiche per venditori
//        for (Map.Entry<User, List<ProductInCart>> entry : venditePerVenditore.entrySet()) {
//            User venditore = entry.getKey();
//            List<ProductInCart> prodotti = entry.getValue();
//
//            BigDecimal totale = prodotti.stream()
//                    .map(p -> BigDecimal.valueOf(p.getProduct().getPrice()).multiply(BigDecimal.valueOf(p.getQuantity())))
//                    .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//            SaleAlert alert = new SaleAlert();
//            alert.setSeller(venditore);
//            alert.setPurchase(purchase);
//            alert.setShippingAddress(shippingAddress);
//            alert.setTotalAmount(totale.floatValue());
//            saleAlertRepository.save(alert);
//        }
//
//        productInCartRepository.deleteAll(userProducts);
//    }


    private boolean isPaymentMethodValid(int paymentMethod) {
        // Per ora simuliamo approvazione casuale
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

//        List<ProductInCart> prodottiUser = new ArrayList<>(productInCartRepository.findByCartId(cart.getCartId()));

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








    // --------------

//    @Transactional(readOnly = true)
//    public List<ProductInCartDTO> getCartItemsByEmail(String email) throws UserNotFoundException, InvalidOperationException {
//
//        User user = userRepository.findByEmail(email);
//        if (user == null) {
//            throw new UserNotFoundException("Cliente non trovato!");
//        }
//
//        Cart cart = cartRepository.findByUserId(user.getId());
//        if (cart == null) {
//            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
//        }
//
////        Set<ProductInCart> prodottiUser = productInCartRepository.findByCartId(cart.getCartId());
////        List<ProductInCartDTO> cartItems = new ArrayList<>();
////
////        for (ProductInCart cp : prodottiUser) {
////            Product prodotto = cp.getProduct();
////
////            ProductInCartDTO dto = new ProductInCartDTO();
////            dto.setId(prodotto.getId());
////            dto.setName(prodotto.getName());
////            dto.setPrice(prodotto.getPrice());
////            dto.setQuantity(cp.getQuantity());
////
////            cartItems.add(dto);
////        }
//        //return cartItems;
//
//        //Metodo alternativo per risolvere problema di concorrenza
//        List<ProductInCart> prodottiUser = new ArrayList<>(productInCartRepository.findByCartId(cart.getCartId()));
//        return prodottiUser.stream()
//                .map(cp -> {
//                    ProductInCartDTO dto = new ProductInCartDTO();
//                    dto.setId(cp.getProduct().getId());
//                    dto.setName(cp.getProduct().getName());
//                    dto.setPrice(cp.getProduct().getPrice());
//                    dto.setQuantity(cp.getQuantity());
//                    return dto;
//                })
//                .collect(Collectors.toList());
//
//
//
//    }


}
