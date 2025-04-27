package org.example.progetto.services;

import org.example.progetto.entities.User;
import org.example.progetto.exceptions.CfAlreadyExistException;
import org.example.progetto.exceptions.EmailAlreadyExistException;
import org.example.progetto.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("productService")
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Transactional(readOnly = true)     // Non dare accesso all'utente?
    public List<User> showAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = false)
    public void addUser(User user) throws EmailAlreadyExistException, CfAlreadyExistException {
        if (userRepository.existsByCf(user.getCf())) {
            throw new EmailAlreadyExistException();
        }
        if (userRepository.existsByCf(user.getCf())) {
            throw new CfAlreadyExistException();
        }
        userRepository.save(user);
    }

    public void removeUser(User user) {
        userRepository.deleteById(user.getId());
    }
    public void updateUser(User user) {} //TODO

}
