package com.florian.services;

import com.florian.commands.IngredientCommand;
import com.florian.commands.RecipeCommand;
import com.florian.converters.IngredientToIngredientCommand;
import com.florian.converters.IngredientCommandToIngredient;
import com.florian.domain.Ingredient;
import com.florian.domain.Recipe;
import com.florian.domain.UnitOfMeasure;
import com.florian.repositories.reactive.UnitOfMeasureReactiveRepository;
import com.florian.repositories.reactive.RecipeReactiveRepository;
import jdk.internal.dynalink.MonomorphicCallSite;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

/**
 * Created by jt on 6/28/17.
 */
@Slf4j
@Service
public class IngredientServiceImpl implements IngredientService {

    private final IngredientToIngredientCommand ingredientToIngredientCommand;
    private final IngredientCommandToIngredient ingredientCommandToIngredient;
    private final UnitOfMeasureReactiveRepository unitOfMeasureRepository;
    private final RecipeReactiveRepository recipeReactiveRepository;

    public IngredientServiceImpl(IngredientToIngredientCommand ingredientToIngredientCommand,
                                 IngredientCommandToIngredient ingredientCommandToIngredient,
                                 UnitOfMeasureReactiveRepository unitOfMeasureRepository, RecipeReactiveRepository recipeReactiveRepository) {
        this.ingredientToIngredientCommand = ingredientToIngredientCommand;
        this.ingredientCommandToIngredient = ingredientCommandToIngredient;
        this.unitOfMeasureRepository = unitOfMeasureRepository;
        this.recipeReactiveRepository = recipeReactiveRepository;
    }

    @Override
    public Mono<IngredientCommand> findByRecipeIdAndIngredientId(String recipeId, String ingredientId) {

        return recipeReactiveRepository.findById(recipeId)
                .flatMapIterable(Recipe::getIngredients)
                .filter(ingredient -> ingredient.getId().equalsIgnoreCase(ingredientId))
                .single()
                .map(ingredient -> {
                    IngredientCommand command = ingredientToIngredientCommand.convert(ingredient);
                    command.setRecipeId(recipeId);
                    return command;
                });
    }

    @Override
    public Mono<IngredientCommand> saveIngredientCommand(IngredientCommand command) {
        String recipeId = command.getRecipeId();
        Ingredient ingredientToSave = ingredientCommandToIngredient.convert(command);
        Mono<Recipe> recipeMono = recipeReactiveRepository.findById(recipeId);
        Mono<Ingredient> ingredientMono = unitOfMeasureRepository.findById(command.getUom().getId())
                .map(unitOfMeasure -> {
                    ingredientToSave.setUom(unitOfMeasure);
                    return ingredientToSave;
                });
        return ingredientMono.map(ingredient -> {
            log.debug("UOM: " + ingredient.getUom().getDescription());
            recipeMono.subscribe(recipe -> {
                recipe.getIngredients().removeIf(ingredientInRecipe -> ingredientInRecipe.getId().equalsIgnoreCase(ingredient.getId()));
                recipe.addIngredient(ingredient);
                recipeReactiveRepository.save(recipe).subscribe();
            });
            return ingredientToIngredientCommand.convert(ingredient);
        });

    }

    @Override
    public void deleteById(String recipeId, String idToDelete) {

            /*log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

            Optional<Recipe> recipeOptional = recipeReactiveRepository.findById(recipeId).blockOptional();

            if (recipeOptional.isPresent()) {
                Recipe recipe = recipeOptional.get();
                log.debug("found recipe");

                Optional<Ingredient> ingredientOptional = recipe
                        .getIngredients()
                        .stream()
                        .filter(ingredient -> ingredient.getId().equals(idToDelete))
                        .findFirst();

                if (ingredientOptional.isPresent()) {
                    log.debug("found Ingredient");
                    Ingredient ingredientToDelete = ingredientOptional.get();
                    // ingredientToDelete.setRecipe(null);
                    recipe.getIngredients().remove(ingredientOptional.get());
                    recipeReactiveRepository.save(recipe).block();
                }
            } else {
                log.debug("Recipe Id Not found. Id:" + recipeId);
            }*/

        Mono<Recipe> recipeMono = recipeReactiveRepository.findById(recipeId);
        recipeMono = recipeMono.map(recipe -> {
            recipe.getIngredients().removeIf(ingredient -> ingredient.getId().equalsIgnoreCase(idToDelete));
            return recipe;
        });
        recipeReactiveRepository.saveAll(recipeMono).subscribe();


    }
}

