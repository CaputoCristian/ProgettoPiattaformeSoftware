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
        var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();

        String email = jwt.getName();
        User loggato = userRepository.findByEmail(email);
        if (loggato == null) {
            User nuovo = new User();
            nuovo.setEmail(email);
            nuovo.setFirstName(jwt.getFirstName());
            nuovo.setLastName(jwt.getLastName());
            userRepository.save(nuovo);
        }

        Map<String, String> response = new HashMap<>();
        response.put("messaggio", "Utente loggato correttamente e presente nel database");
        return ResponseEntity.ok(response);
    }
}
