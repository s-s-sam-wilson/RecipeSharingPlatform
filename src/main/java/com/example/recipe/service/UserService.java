package com.example.recipe.service;

import com.example.recipe.model.User;

public interface UserService {
    void save(User user);
    User findByUsername(String username);
    boolean authenticate(String username, String password);
	User findById(Long userId);
}
