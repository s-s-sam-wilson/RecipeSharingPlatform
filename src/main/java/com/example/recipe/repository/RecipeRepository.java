package com.example.recipe.repository;

import com.example.recipe.model.Recipe;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
	List<Recipe> findByNameContainingIgnoreCase(String keyword);
	List<Recipe> findByUserId(Long userId);
}