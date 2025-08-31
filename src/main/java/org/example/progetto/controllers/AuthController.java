package org.example.progetto.controllers;

import org.example.progetto.entities.User;
import org.example.progetto.jwt.CustomJwt;
import org.example.progetto.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @GetMapping("/home")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, String>> home() {

      var jwt = (CustomJwt) SecurityContextHolder.getContext().getAuthentication();

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

        Map<String, String> response = new HashMap<>();
        response.put("messaggio", "Utente loggato correttamente e presente nel database");
        return ResponseEntity.ok(response);
    }
}
