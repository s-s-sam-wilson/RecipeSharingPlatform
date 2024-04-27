package com.example.recipe.service;


import com.example.recipe.model.Recipe;
import com.example.recipe.model.User;

import java.util.List;

public interface RecipeService {
    List<Recipe> getAllRecipes();
    Recipe getRecipeById(Long id);
    Recipe saveRecipe(Recipe recipe, User user);
    void deleteRecipe(Long id);
    List<Recipe> searchRecipes(String keyword);
    List<Recipe> getRecipeByuserId(Long userid);
}
