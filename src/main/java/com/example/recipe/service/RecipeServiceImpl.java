package com.example.recipe.service;

import com.example.recipe.model.Recipe;
import com.example.recipe.model.User;
import com.example.recipe.repository.RecipeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeServiceImpl implements RecipeService {

    @Autowired
    private RecipeRepository recipeRepository;

    @Override
    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    @Override
    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id).orElse(null);
    }

    @Override
    public Recipe saveRecipe(Recipe recipe, User user) {
        recipe.setUser(user);
        return recipeRepository.save(recipe);
    }

    @Override
    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    @Override
    public List<Recipe> searchRecipes(String keyword) {
        return recipeRepository.findByNameContainingIgnoreCase(keyword);
    }

	@Override
	public List<Recipe> getRecipeByuserId(Long userid) {
		return recipeRepository.findByUserId(userid);
	}
}
