package com.florian.services;

import com.florian.commands.RecipeCommand;
import com.florian.converters.RecipeCommandToRecipe;
import com.florian.converters.RecipeToRecipeCommand;
import com.florian.exceptions.NotFoundException;
import com.florian.repositories.RecipeRepository;
import com.florian.domain.Recipe;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Created by jt on 6/13/17.
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;

    public RecipeServiceImpl(RecipeRepository recipeRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand) {
        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
    }

    @Override
    public Set<Recipe> getRecipes() {
        log.debug("I'm in the service");

        Set<Recipe> recipeSet = new HashSet<>();
        recipeRepository.findAll().iterator().forEachRemaining(recipeSet::add);
        return recipeSet;
    }

    @Override
    public Recipe findById(String id) {

        Optional<Recipe> recipeOptional = recipeRepository.findById(id);

        if (!recipeOptional.isPresent()) {
            throw new NotFoundException("Recipe Not Found. For ID value: " + id );
        }

        return recipeOptional.get();
    }

    @Override
    @Transactional
    public RecipeCommand findCommandById(String id) {
        return recipeToRecipeCommand.convert(findById(id));
    }

    @Override
    @Transactional
    public RecipeCommand saveRecipeCommand(RecipeCommand command) {
        Recipe detachedRecipe = recipeCommandToRecipe.convert(command);

        Recipe savedRecipe = recipeRepository.save(detachedRecipe);
        log.debug("Saved RecipeId:" + savedRecipe.getId());
        return recipeToRecipeCommand.convert(savedRecipe);
    }

    @Override
    public void deleteById(String idToDelete) {
        recipeRepository.deleteById(idToDelete);
    }
}
