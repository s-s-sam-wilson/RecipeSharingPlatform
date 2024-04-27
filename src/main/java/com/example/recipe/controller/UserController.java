package com.example.recipe.controller;


import com.example.recipe.PasswordHasher;
import com.example.recipe.model.User;
import com.example.recipe.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import jakarta.validation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Controller
public class UserController {

	@Autowired
    private UserService userService;


    @GetMapping("/registration")
    public String registrationForm(Model model) {
        model.addAttribute("user", new User());
        return "registration";
    }

    @PostMapping("/registration")
    public String registrationSubmit(@ModelAttribute @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "registration";
        }
        String hashedPassword = PasswordHasher.hashPassword(user.getPassword());
        System.out.print("Registering: hash:"+hashedPassword+" "+user.getPassword());
        user.setPassword(hashedPassword);
        System.out.println("check if object has changed: "+user.getPassword());
        user.setEnabled(true);
        userService.save(user);
        return "redirect:/login";
    }
    
    @GetMapping("/login")
    public String loginForm(Model model) {
        model.addAttribute("user", new User()); // You can pass a User object to pre-populate the form fields if needed
        return "login";
    }
    @PostMapping("/login")
    public String loginSubmit(@ModelAttribute User user, HttpServletResponse response, Model model) {
        // Simulated authentication logic
        boolean isAuthenticated = userService.authenticate(user.getUsername(), user.getPassword());
        if (isAuthenticated) {
            // Set session cookie
            Cookie cookie = new Cookie("user", user.getUsername());
            cookie.setMaxAge(3600); // Set cookie expiry time (in seconds)
            response.addCookie(cookie);
            return "redirect:/";
        } else {
        	model.addAttribute("errorMessage", "Incorrect username or password. Please try again.");
            return "redirect:/login?error=wrongpassword";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // Clear session cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user")) {
                    cookie.setValue("");
                    cookie.setMaxAge(0);
                    response.addCookie(cookie);
                    break;
                }
            }
        }
        return "redirect:/login?logout";
    }
    
}
