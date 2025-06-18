package org.example.progetto.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.LockModeType;
import org.example.progetto.entities.Cart;
import org.example.progetto.entities.Product;
import org.example.progetto.entities.ProductInCart;
import org.example.progetto.entities.User;
import org.example.progetto.repositories.CartRepository;
import org.example.progetto.repositories.ProductInCartRepository;
import org.example.progetto.repositories.ProductRepository;
import org.example.progetto.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;


@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


//    @Autowired
//    private OrdineRepository ordineRepository;
//
//    @Autowired
//    private transazioneRepository transazioneRepository;
//
//    @Autowired
//    private spedizioneRepository spedizioneRepository;
//
//    @Autowired
//    private prodottiordinatiRepository prodottiordinatiRepository;


    @Autowired
    private ProductInCartRepository productInCartRepository;

    @Autowired
    private EntityManager entityManager;
//    @Autowired
//    private com.progettopswcp.ProgettoPSW.repositories.metododipagamentoRepository metododipagamentoRepository;

    private static final Random RANDOM = new Random();

    @Transactional
    public void aggiungiAlCarrello(String idUtente, int idProdotto, int quantita)
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException {

        // Recupero l'utente dal database.
        User user = userRepository.findByEmail(idUtente);
        if (user == null) {
            throw new UserNotFoundException("Cliente non trovato!");
        }

        // Recupero o creo il carrello dell'utente
        Cart cart = cartRepository.findByIdCliente(user.getId());
        if (cart == null) {
            cart = new Cart();
            cart.setIdCliente(user.getId());
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

        if (quantita > disponibilitaProd) {
            throw new InvalidQuantityException("Impossibile aggiungere al carrello: " +
                    "il prodotto non è disponibile per la quantità desiderata");
        }

        // Controllo se il prodotto è già presente nel carrello

        ProductInCart cp = productInCartRepository.findByCarrelloAndProdotto(cart,prod);
        if (cp != null) {
            // Lock dell'elemento del carrello
            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);

            // Aggiorno la quantità
            int nuovaQuantita = cp.getQuantity() + quantita;
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
            aggiunta.setQuantity(quantita);
            aggiunta.setCartId(cart.getIdCarrello());

            productInCartRepository.save(aggiunta);
        }
    }

    @Transactional
    public void incrementaquantitaprodottocarrello(String email, int idProdotto)
            throws UserNotFoundException, ProductNotFoundException, InvalidQuantityException, InvalidOperationException {

        User cliente = userRepository.findByEmail(email);
        if (cliente == null) {
            throw new UserNotFoundException("Cliente non trovato!");
        }

        int idUtente = cliente.getId();

        // Recupero e locko il carrello
        Cart carrello = cartRepository.findByIdCliente(idUtente);
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko l'elemento del carrello
        ProductInCart cp = productInCartRepository.findByCarrelloAndProdottoId(carrello, idProdotto);
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

        User cliente = userRepository.findByEmail(email);
        if (cliente == null) {
            throw new UserNotFoundException("Cliente non trovato!");
        }

        // Recupero e locko il carrello
        Cart carrello = cartRepository.findByIdCliente(cliente.getId());
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko l'elemento del carrello
        ProductInCart cp = productInCartRepository.findByCarrelloAndProdottoId(carrello, prodottoID);
        if (cp == null) {
            throw new InvalidOperationException("Il prodotto non è presente nel carrello.");
        }
        entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);

        // Rimuovo il prodotto dal carrello
        productInCartRepository.delete(cp);
    }

    @Transactional
    public void decrementaquantitaprodottocarrello(String email, int idProdotto)
            throws UserNotFoundException, InvalidOperationException {

        User cliente = userRepository.findByEmail(email);
        if (cliente == null) {
            throw new UserNotFoundException("Cliente non trovato!");
        }

        int idUtente = cliente.getId();

        // Recupero e locko il carrello
        Cart carrello = cartRepository.findByIdCliente(idUtente);
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko l'elemento del carrello
        ProductInCart cp = productInCartRepository.findByCarrelloAndProdottoId(carrello, idProdotto);
        if (cp == null) {
            throw new InvalidOperationException("Il prodotto non è presente nel carrello.");
        }
        entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);

        if (!cp.getCart().equals(carrello)) {
            throw new InvalidOperationException("Operazione non valida: il carrello non corrisponde");
        }

        // Decremento o rimuovo il prodotto dal carrello
        if (cp.getQuantity() > 1) {
            cp.setQuantity(cp.getQuantity() - 1);
            productInCartRepository.save(cp);
        } else {
            productInCartRepository.delete(cp);
        }
    }

    @Transactional
    public void svuotaCarrello(String email) throws UserNotFoundException, InvalidOperationException {
        User cliente = userRepository.findByEmail(email);
        if (cliente == null) {
            throw new UserNotFoundException("Cliente non trovato!");
        }

        // Recupero e locko il carrello
        Cart carrello = cartRepository.findByIdCliente(cliente.getId());
        if (carrello == null) {
            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
        }
        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);

        // Recupero e locko tutti gli elementi del carrello
        Set<ProductInCart> cartProducts = productInCartRepository.findByCarrelloId(carrello.getIdCarrello());
        for (ProductInCart cp : cartProducts) {
            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);
        }

        // Svuoto il carrello
        productInCartRepository.deleteAllByCarrello(carrello);
    }

//    @Transactional
//    public void ordina(String email, int metodoDiPagamento, String indirizzoSpedizione)
//            throws UserNotFoundException, InvalidOperationException {
//
//        User cliente = userRepository.findByEmail(email);
//        if (cliente == null) {
//            throw new UserNotFoundException("Cliente non trovato!");
//        }
//        int idCliente = cliente.getId();
//
//        // Recupero e locko il carrello
//        Cart carrello = cartRepository.findByIdCliente(cliente.getId());
//        if (carrello == null) {
//            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
//        }
//        entityManager.lock(carrello, LockModeType.PESSIMISTIC_WRITE);
//
//        // Recupero e locko gli elementi del carrello
//        Set<ProductInCart> prodottiUser = productInCartRepository.findByCarrelloId(carrello.getIdCarrello());
//        if (prodottiUser.isEmpty()) {
//            throw new InvalidOperationException("Il carrello è vuoto. Aggiungi prodotti prima di procedere all'ordine.");
//        }
//
//        // Ordino gli elementi per evitare deadlock
//        List<ProductInCart> prodottiUserList = new ArrayList<>(prodottiUser);
//        prodottiUserList.sort(Comparator.comparingInt(ProductInCart::getId));
//
//        // Verifico la disponibilità e locko i prodotti
//        for (ProductInCart cp : prodottiUserList) {
//            // Lock dell'elemento del carrello
//            entityManager.lock(cp, LockModeType.PESSIMISTIC_WRITE);
//
//            Product prodotto = productRepository.findById(cp.getId())
//            // Lock del prodotto
//            entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);
//
//            // Verifico la disponibilità
//            if (prodotto.getQuantity() < cp.getQuantity()) {
//                throw new InvalidOperationException("La quantità del prodotto '" + prodotto.getName() + "' non è sufficiente per completare l'ordine.");
//            }
//
//            // Decremento la disponibilità del prodotto
//            prodotto.setQuantity(prodotto.getQuantity() - cp.getQuantity());
//            productRepository.save(prodotto);
//        }
//
//        // Creazione dell'ordine e della transazione
//        ordine ordine = new ordine();
//        transazione transazione = new transazione();
//
//        metododipagamento met = metododipagamentoRepository.findById(metodoDiPagamento);
//        if (met == null) {
//            throw new InvalidOperationException("Metodo di pagamento non trovato");
//        }
//
//        ordine.setId_carrello(carrello.getIdCarrello());
//        ordine.setIdCliente(cliente.getIdCliente());
//        ordine.setOra(LocalTime.now());
//        ordine.setData(LocalDateTime.now());
//        ordine.setStato("Processamento in corso...");
//        ordineRepository.save(ordine);
//
//        transazione.setMetodoDiPagamento(met);
//        transazione.setIdOrdine(ordine.getIdOrdine());
//        transazione.setOra(LocalTime.now());
//        transazione.setData(Instant.now());
//        transazione.setImporto(calcolaImporto(prodottiUser));
//
//        spedizione spedizione = new spedizione();
//        spedizione.setIdOrdine(ordine.getIdOrdine());
//        spedizione.setIndirizzoSpedizione(indirizzoSpedizione);
//        spedizione.setDataPrevista(Instant.now().plus(7, ChronoUnit.DAYS));
//        spedizione.setStato("In corso...");
//
//        boolean esitoPagamento = processaPagamento(met, transazione.getImporto());
//
//        if (esitoPagamento) {
//            transazione.setEsito(true);
//            ordine.setStato("Pagamento completato");
//            svuotaCarrello(email);
//
//            // Salvo i prodotti ordinati
//            for (carrelloprodotto cp : prodottiUserList) {
//                prodottiordinati po = new prodottiordinati();
//                po.setIdProdotto(cp.getProdottoId());
//                po.setIdUtente(cliente.getIdCliente());
//                po.setIdOrdine(ordine.getIdOrdine());
//                po.setQuantita(cp.getQuantita());
//                prodottiordinatiRepository.save(po);
//            }
//
//        } else {
//            transazione.setEsito(false);
//            ordine.setStato("Pagamento fallito");
//
//            // Ripristino la quantità dei prodotti
//            for (carrelloprodotto cp : prodottiUserList) {
//                prodotto prodotto = productRepository.findById(cp.getProdottoId())
//                        .orElseThrow(() -> new InvalidOperationException("Prodotto non trovato"));
//                // Lock del prodotto
//                entityManager.lock(prodotto, LockModeType.PESSIMISTIC_WRITE);
//
//                prodotto.setQuantita(prodotto.getQuantita() + cp.getQuantita());
//                productRepository.save(prodotto);
//            }
//
//            throw new InvalidOperationException("Il pagamento è fallito. Riprovare.");
//        }
//
//        // Salvo le transazioni e la spedizione
//        transazioneRepository.save(transazione);
//        spedizioneRepository.save(spedizione);
//        ordineRepository.save(ordine);
//    }

//    private static BigDecimal calcolaImporto(Set<carrelloprodotto> prodottiUser) {
//        BigDecimal totale = BigDecimal.ZERO;
//
//        if (prodottiUser == null || prodottiUser.isEmpty()) {
//            return totale;
//        }
//
//        for (carrelloprodotto cp : prodottiUser) {
//            prodotto prodotto = cp.getProdotto();
//            BigDecimal prezzo = prodotto.getPrezzo();
//            int quantita = cp.getQuantita();
//
//            if (prezzo == null || prezzo.compareTo(BigDecimal.ZERO) <= 0) {
//                throw new IllegalArgumentException("Prezzo del prodotto " + prodotto.getNome() + " non valido.");
//            }
//            if (quantita <= 0) {
//                throw new IllegalArgumentException("Quantità del prodotto " + prodotto.getNome() + " non valida.");
//            }
//
//            BigDecimal costoProdotto = prezzo.multiply(BigDecimal.valueOf(quantita));
//            totale = totale.add(costoProdotto);
//        }
//
//        return totale;
//    }

//    private boolean processaPagamento(metododipagamento metodoDiPagamento, BigDecimal amount) {
//
//        if (metodoDiPagamento == null) {
//            System.out.println("Errore: nessun metodo di pagamento selezionato.");
//            return false;
//        }
//
//        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
//            System.out.println("Errore: importo non valido. Valore importo: " + amount);
//            return false;
//        }
//
//        System.out.println("Pagamento in corso con il metodo selezionato: " + metodoDiPagamento.getSelezione());
//        System.out.println("Importo da pagare: " + amount);
//
//        boolean pagamentoRiuscito = RANDOM.nextInt(100) < 80;
//
//        if (pagamentoRiuscito) {
//            System.out.println("Pagamento effettuato con successo!");
//            return true;
//        } else {
//            System.out.println("Pagamento fallito.");
//            return false;
//        }
//    }

//    @Transactional(readOnly = true)
//    public List<carrelloprodottoDTO> getCartItemsByEmail(String email) throws UserNotFoundException {
//
//        cliente cliente = userRepository.findByEmail(email);
//        if (cliente == null) {
//            throw new UserNotFoundException("Cliente non trovato!");
//        }
//
//        carrello carrello = cartRepository.findByIdCliente(cliente.getIdCliente());
//        if (carrello == null) {
//            throw new InvalidOperationException("Il carrello dell'utente non è stato trovato.");
//        }
//
//        Set<carrelloprodotto> prodottiUser = productInCartRepository.findByCarrelloId(carrello.getIdCarrello());
//
//        List<carrelloprodottoDTO> cartItems = new ArrayList<>();
//
//        for (carrelloprodotto cp : prodottiUser) {
//            prodotto prodotto = cp.getProdotto();
//
//            carrelloprodottoDTO dto = new carrelloprodottoDTO();
//            dto.setIdProdotto(prodotto.getId());
//            dto.setNomeProdotto(prodotto.getNome());
//            dto.setPrezzoProdotto(prodotto.getPrezzo());
//            dto.setQuantita(cp.getQuantita());
//
//            cartItems.add(dto);
//        }
//
//        return cartItems;
//    }


}
