package com.example.recipe.controller;

import com.example.recipe.model.Recipe;
import com.example.recipe.model.User;
import com.example.recipe.repository.RecipeRepository;
import com.example.recipe.service.RecipeService;
import com.example.recipe.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;

@Controller
public class RecipeController {

    @Autowired
    private RecipeService recipeService;

    @Autowired
    private UserService userService;
    
    @Autowired
    private RecipeRepository recipeRepository;

    @GetMapping("/")
    public String index(Model model, HttpServletRequest request) {
        // Check if the user is logged in
        User user = getLoggedInUser(request);
        if (user != null) {
            model.addAttribute("userName", user.getUsername());
        }
        List<Recipe> recipes = recipeService.getAllRecipes();
        model.addAttribute("recipes", recipes);
        return "index";
    }

    @GetMapping("/recipe/add")
    public String addRecipeForm(Model model, HttpServletRequest request) {
        // Check if the user is logged in
        User user = getLoggedInUser(request);
        if (user == null) {
            // Redirect to login page if user is not logged in
            return "redirect:/login";
        }
        model.addAttribute("recipe", new Recipe());
        return "add-recipe";
    }
    
    @GetMapping("/my-recipe")
    public String seeMyRecipe(Model model, HttpServletRequest request) {
    	User user = getLoggedInUser(request);
        if (user == null) {
            // Redirect to login page if user is not logged in
            return "redirect:/login";
        }
        model.addAttribute("userName", user.getUsername());
        List<Recipe> recipes = recipeService.getRecipeByuserId(user.getId());
        model.addAttribute("recipes", recipes);
        return "myrecipelist";
    }

    @PostMapping("/recipe/add")
    public String addRecipeSubmit(@ModelAttribute @Valid Recipe recipe, BindingResult bindingResult, HttpServletRequest request) {
        if (bindingResult.hasErrors()) {
            return "add-recipe";
        }
        // Check if the user is logged in
        User user = getLoggedInUser(request);
        if (user == null) {
            // Redirect to login page if user is not logged in
            return "redirect:/login";
        }
        recipeService.saveRecipe(recipe, user);
        return "redirect:/";
    }

    @GetMapping("/recipe/{id}")
    public String recipeDetails(@PathVariable("id") Long id, Model model) {
        Recipe recipe = recipeService.getRecipeById(id);
        String descWithBreaks = recipe.getDescription().replace("\n", "<br>");
        String ingrWithBreaks = recipe.getIngredients().replace("\n", "<br>");
        String dirWithBreaks = recipe.getDirections().replace("\n", "<br>");
        model.addAttribute(recipe);
        model.addAttribute("name", recipe.getName());
        model.addAttribute("description", descWithBreaks);
        model.addAttribute("ingredients",ingrWithBreaks);
        model.addAttribute("directions",dirWithBreaks);
        return "recipe-details";
    }

    @GetMapping("/about")
    public String aboutpage(Model model) {
        return "about";
    }

    @GetMapping("/search")
    public String searchRecipes(@RequestParam("query") String query, Model model) {
        // Perform the search operation using the query
        List<Recipe> searchResults = recipeRepository.findByNameContainingIgnoreCase(query);

        // Add the search results to the model
        model.addAttribute("searchResults", searchResults);

        // Add the query to the model to display it in the view
        model.addAttribute("query", query);

        return "search-results";
    }

    // Helper method to get the logged-in user from the cookie
    private User getLoggedInUser(HttpServletRequest request) {
        // Retrieve the user ID from the cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("user")) {
                    String username = new String(cookie.getValue());
                    // Retrieve the user from the database using the user ID
                    return userService.findByUsername(username);
                }
            }
        }
        return null;
    }
    
    @RequestMapping(value = "/recipe/delete/{id}", method = {RequestMethod.GET, RequestMethod.POST})
    public String deleteRecipe(@PathVariable("id") Long id, HttpServletRequest request) {
        // Check if the user is logged in
        User user = getLoggedInUser(request);
        if (user == null) {
            // Redirect to login page if user is not logged in
            return "redirect:/login";
        }
        // Check if the user owns the recipe
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            // Recipe not found, handle appropriately (e.g., show error message)
            return "redirect:/";
        }
        if (recipe.getUser().getId().equals(user.getId())) {
            // Delete the recipe
            recipeService.deleteRecipe(id);
        }
        return "redirect:/";
    }
    
    @GetMapping("/recipe/edit/{id}")
    public String editRecipeForm(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        // Check if the user is logged in
        User user = getLoggedInUser(request);
        if (user == null) {
            // Redirect to login page if user is not logged in
            return "redirect:/login";
        }
        
        // Fetch the recipe to edit
        Recipe recipe = recipeService.getRecipeById(id);
        if (recipe == null) {
            // Recipe not found, handle appropriately (e.g., show error message)
            return "redirect:/";
        }
        
        // Check if the logged-in user is the owner of the recipe
        if (!recipe.getUser().getId().equals(user.getId())) {
            // If the logged-in user is not the owner of the recipe, redirect
            return "redirect:/";
        }
        
        model.addAttribute("recipe", recipe);
        return "editrecipe";
    }

    @PostMapping("/recipe/edit")
    public String editRecipeSubmit(@ModelAttribute Recipe recipe, HttpServletRequest request) {
        // Check if the user is logged in
        User user = getLoggedInUser(request);
        if (user == null) {
            // Redirect to login page if user is not logged in
            return "redirect:/login";
        }
        
        // Fetch the original recipe from the database
        Recipe originalRecipe = recipeService.getRecipeById(recipe.getId());
        if (originalRecipe == null) {
            // Recipe not found, handle appropriately (e.g., show error message)
            return "redirect:/";
        }
        
        // Check if the logged-in user is the owner of the recipe
        if (!originalRecipe.getUser().getId().equals(user.getId())) {
            // If the logged-in user is not the owner of the recipe, redirect
            return "redirect:/";
        }
        
        // Update the recipe with the new data
        originalRecipe.setName(recipe.getName());
        originalRecipe.setDescription(recipe.getDescription());
        originalRecipe.setIngredients(recipe.getIngredients());
        originalRecipe.setDirections(recipe.getDirections());
        
        // Save the updated recipe
        recipeService.saveRecipe(originalRecipe, user);
        
        return "redirect:/";
    }

}
