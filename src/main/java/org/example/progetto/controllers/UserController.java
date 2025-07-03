package org.example.progetto.controllers;


import org.example.progetto.DTO.UserUpdateRequest;
import org.example.progetto.entities.User;
import org.example.progetto.exceptions.EmailAlreadyExistException;
import org.example.progetto.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

// import jakarta.validation.Valid; Controllare a cosa serve

import java.util.List;

@RestController
@CrossOrigin(
        origins = "http://localhost:4200",
        allowedHeaders = "*",
        methods = { RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE }
)
@RequestMapping("")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/test")
    public ResponseEntity addUser(@RequestBody User user) {
        try {
            User addedUser = userService.addUser(user); //Non serve tornare l'utente se si ha il .ok (lazy method)
            return ResponseEntity.ok(addedUser);
        } catch (EmailAlreadyExistException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }
    @GetMapping("/test")
    public List<User> showAllUsers () {
        return userService.showAllUsers();
    }

    @GetMapping("/profile")
    public ResponseEntity<User> showUser(Authentication authentication) {
        JwtAuthenticationToken token = (JwtAuthenticationToken) authentication;
        String email = token.getToken().getClaimAsString("email");

        User user = userService.findByEmail(email);

        System.out.println("Invio user:" + user);

        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}/profile")
    public ResponseEntity<User> updateProfile(@PathVariable Integer id,
                                              @RequestBody UserUpdateRequest request) {
        User updatedUser = userService.updateUserProfile(id, request);
        return ResponseEntity.ok(updatedUser);
    }
}