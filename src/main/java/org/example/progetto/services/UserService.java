package org.example.progetto.services;

import org.example.progetto.entities.User;
import org.example.progetto.exceptions.CfAlreadyExistException;
import org.example.progetto.exceptions.EmailAlreadyExistException;
import org.example.progetto.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public User addUser(User user) throws EmailAlreadyExistException, CfAlreadyExistException {
        if (userRepository.existsByCf(user.getCf())) {
            throw new EmailAlreadyExistException();
        }
        if (userRepository.existsByCf(user.getCf())) {
            throw new CfAlreadyExistException();
        }
        userRepository.save(user);
        return user;
    }
}
