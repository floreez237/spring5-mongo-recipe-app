package com.florian.services;

import com.florian.commands.RecipeCommand;
import com.florian.converters.RecipeCommandToRecipe;
import com.florian.converters.RecipeToRecipeCommand;
import com.florian.domain.Recipe;
import com.florian.exceptions.NotFoundException;
import com.florian.repositories.RecipeRepository;
import com.florian.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    private final RecipeReactiveRepository recipeReactiveRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository, RecipeCommandToRecipe recipeCommandToRecipe, RecipeToRecipeCommand recipeToRecipeCommand, RecipeReactiveRepository recipeReactiveRepository) {
        this.recipeRepository = recipeRepository;
        this.recipeCommandToRecipe = recipeCommandToRecipe;
        this.recipeToRecipeCommand = recipeToRecipeCommand;
        this.recipeReactiveRepository = recipeReactiveRepository;
    }

    @Override
    public Flux<Recipe> getRecipes() {
        log.debug("I'm in the service");

        Set<Recipe> recipeSet = new HashSet<>();
        return recipeReactiveRepository.findAll();
//        return Flux.fromIterable(recipeSet);
    }

    @Override
    public Mono<Recipe> findById(String id) {

        Optional<Recipe> recipeOptional = recipeReactiveRepository.findById(id).blockOptional();

        if (!recipeOptional.isPresent()) {
            throw new NotFoundException("Recipe Not Found. For ID value: " + id );
        }

        return Mono.just(recipeOptional.get());
    }

    @Override
    @Transactional
    public Mono<RecipeCommand> findCommandById(String id) {
        return findById(id).map(recipeToRecipeCommand::convert);
    }

    @Override
    @Transactional
    public Mono<RecipeCommand> saveRecipeCommand(RecipeCommand command) {
        Recipe detachedRecipe = recipeCommandToRecipe.convert(command);

        Mono<Recipe> savedRecipe = recipeReactiveRepository.save(detachedRecipe);
        log.debug("Saved RecipeId:" + savedRecipe.block().getId());
        return savedRecipe.map(recipeToRecipeCommand::convert);
    }

    @Override
    public Mono<Void> deleteById(String idToDelete) {
       return recipeReactiveRepository.deleteById(idToDelete);
    }
}
