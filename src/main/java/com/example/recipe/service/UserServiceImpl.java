package com.example.recipe.service;

import com.example.recipe.model.User;
import com.example.recipe.repository.UserRepository;
import com.example.recipe.PasswordHasher;

import java.util.Optional;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void save(User user) {
        // Hash the password before saving
        userRepository.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public boolean authenticate(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user != null) {
            String hashedPassword = PasswordHasher.hashPassword(password);
            return hashedPassword != null && hashedPassword.equals(user.getPassword());
        }
        return false;
    }
    @Override
    public User findById(Long id) {
        Optional<User> userOptional = userRepository.findById(id);
        return userOptional.orElse(null);
    }

}
