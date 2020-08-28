package com.florian.services;

import com.florian.commands.RecipeCommand;
import com.florian.converters.RecipeCommandToRecipe;
import com.florian.converters.RecipeToRecipeCommand;
import com.florian.domain.Recipe;
import com.florian.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by jt on 6/13/17.
 */
@Slf4j
@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeCommandToRecipe recipeCommandToRecipe;
    private final RecipeToRecipeCommand recipeToRecipeCommand;
    private final RecipeReactiveRepository recipeReactiveRepository;

    public RecipeServiceImpl(RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand, RecipeReactiveRepository recipeReactiveRepository) {
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
        this.recipeReactiveRepository = recipeReactiveRepository;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        log.debug("I'm in the service");

        return recipeReactiveRepository.findAll();
    }

    @Override
    public Mono<Recipe> findById(String id) {

        return recipeReactiveRepository.findById(id);
    }

    @Override
    public Mono<RecipeCommand> findCommandById(String id) {
        return findById(id).map(recipe -> {
            RecipeCommand recipeCommand = recipeToRecipeCommand.convert(recipe);
            recipeCommand.getIngredients().forEach(ingredientCommand -> ingredientCommand.setRecipeId(recipeCommand.getId()));
            return recipeCommand;
        });
    }

    @Override
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {

        return recipeReactiveRepository.save(recipeCommandToRecipe.convert(command))
                .map(recipeToRecipeCommand::convert);
    }

    @Override
    public Mono<Void> deleteById(String idToDelete) {
       return recipeReactiveRepository.deleteById(idToDelete);
    }
}
