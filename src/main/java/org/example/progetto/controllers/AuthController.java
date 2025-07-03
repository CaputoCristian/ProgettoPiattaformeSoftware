package org.example.progetto.controllers;


import org.example.progetto.entities.User;
import org.example.progetto.jwt.CustomJwt;
import org.example.progetto.repositories.ProductRepository;
import org.example.progetto.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@RestController

@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
)

@RequestMapping("") //Serve?
public class AuthController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProductRepository productRepository;


//    @GetMapping("/home")
//    @PreAuthorize("isAuthenticated()")
//    public ResponseEntity<Map<String, String>> home(@AuthenticationPrincipal JwtAuthenticationToken authentication) {
//        JwtAuthenticationToken token = authentication;
//
//        String email = token.getToken().getClaimAsString("email");
//        String firstName = token.getToken().getClaimAsString("firstName");
//        String lastName = token.getToken().getClaimAsString("lastName");
//
//        User loggato = userRepository.findByEmail(email);
//        if (loggato == null) {
//            User nuovo = new User();
//            nuovo.setEmail(email);
//            nuovo.setFirstName(firstName);
//            nuovo.setLastName(lastName);
//            userRepository.save(nuovo);
//        }
//
//        Map<String, String> response = new HashMap<>();
//        response.put("messaggio", "Utente loggato correttamente e presente nel database");
//        return ResponseEntity.ok(response);
//    }
//}

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> home() {

//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (!(authentication instanceof JwtAuthenticationToken)) {
//            throw new RuntimeException("Autenticazione non valida");
//        }

      var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();
//        var jwt = (JwtAuthenticationToken) authentication;
        String email = jwt.getToken().getClaimAsString("email");

        //String email = jwt.getName();

      System.out.println(jwt.getToken().getClaims());
      User loggato = userRepository.findByEmail(jwt.getEmail());
      if (loggato == null) {
          User nuovo = new User();
          nuovo.setEmail(jwt.getEmail());
          nuovo.setFirstName(jwt.getFirstName());
          nuovo.setLastName(jwt.getLastName());
          nuovo.setAddress(jwt.getAddress());
          nuovo.setTelephoneNumber(jwt.getTelephoneNumber());

          SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
          Date defaultDate;
            try {
                defaultDate = dateFormat.parse(jwt.getBirthDate());
            } catch (ParseException e) {
                System.out.println("errore nel parsing della data di nascita: " + e.getMessage());
                defaultDate = new Date();
            }
            nuovo.setBirthDate(defaultDate);
          System.out.println(" Ricevuto utente: " + nuovo);
          userRepository.save(nuovo);
      }
//        User loggato = userRepository.findByEmail(email);
//        if (loggato == null) {
//            User nuovo = new User();
//            nuovo.setEmail(email);
//            nuovo.setFirstName(jwt.getToken().getClaimAsString("given_name"));
//            nuovo.setLastName(jwt.getToken().getClaimAsString("family_name"));
//            // Imposta i campi obbligatori
//            nuovo.setAddress("Da aggiornare"); // Poiché address è NOT NULL nel database
//            userRepository.save(nuovo);
//
//            // Imposta la data di nascita con un formato specifico
//            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
//            Date defaultDate;
//            try {
//                defaultDate = dateFormat.parse("01/01/2000");
//            } catch (ParseException e) {
//                defaultDate = new Date();
//            }
//            nuovo.setBirthDate(defaultDate);
//
//            // Il telefono può essere null quindi non lo impostiamo
//            nuovo.setTelephoneNumber(null);
//
//
//        }


        Map<String, String> response = new HashMap<>();
        response.put("messaggio", "Utente loggato correttamente e presente nel database");
        return ResponseEntity.ok(response);
    }
}
