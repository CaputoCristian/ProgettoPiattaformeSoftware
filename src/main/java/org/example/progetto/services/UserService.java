package org.example.progetto.services;

import org.example.progetto.DTO.UserUpdateRequest;
import org.example.progetto.entities.User;
import org.example.progetto.exceptions.EmailAlreadyExistException;
import org.example.progetto.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("userService")
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    //private ShopRepository shopRepository;
    @Transactional(readOnly = true)     // Non dare accesso all'utente?
    public List<User> showAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = false)
    @PreAuthorize("isAuthenticated")
    public User addUser(User user) throws EmailAlreadyExistException {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistException("Email already exist");
        }
//        if (userRepository.existsByCf(user.getCf())) {
//            throw new CfAlreadyExistException("CF already exist");
//        }
        userRepository.save(user);
        return user;
    }

    @Transactional(readOnly = true)     // Non dare accesso all'utente?
    public User showByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Transactional(readOnly = false)
    public User updateUserProfile(Integer userId, UserUpdateRequest updateRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utente non trovato"));

        user.setFirstName(updateRequest.getFirstName ());
        user.setLastName(updateRequest.getLastName ());
        user.setTelephoneNumber(updateRequest.getTelephoneNumber());
        user.setAddress(updateRequest.getAddress());
        user.setBirthDate(updateRequest.getBirthDate());

        return userRepository.save(user);
    }

}
