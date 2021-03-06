package com.florian.services;

import com.florian.commands.IngredientCommand;
import com.florian.converters.IngredientToIngredientCommand;
import com.florian.converters.IngredientCommandToIngredient;
import com.florian.domain.Ingredient;
import com.florian.domain.Recipe;
import com.florian.repositories.reactive.UnitOfMeasureReactiveRepository;
import com.florian.repositories.reactive.RecipeReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Optional;

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
        public Mono<IngredientCommand> saveIngredientCommand (IngredientCommand command){
            Optional<Recipe> recipeOptional = recipeReactiveRepository.findById(command.getRecipeId()).blockOptional();

            if (!recipeOptional.isPresent()) {

                //todo toss error if not found!
                log.error("Recipe not found for id: " + command.getRecipeId());
                return Mono.just(new IngredientCommand());
            } else {
                Recipe recipe = recipeOptional.get();

                Optional<Ingredient> ingredientOptional = recipe
                        .getIngredients()
                        .stream()
                        .filter(ingredient -> ingredient.getId().equals(command.getId()))
                        .findFirst();

                if (ingredientOptional.isPresent()) {
                    Ingredient ingredientFound = ingredientOptional.get();
                    ingredientFound.setDescription(command.getDescription());
                    ingredientFound.setAmount(command.getAmount());
                    ingredientFound.setUom(unitOfMeasureRepository
                            .findById(command.getUom().getId()).block());
//                        .orElseThrow(() -> new RuntimeException("UOM NOT FOUND"))); //todo address this
                } else {
                    //add new Ingredient
                    Ingredient ingredient = ingredientCommandToIngredient.convert(command);
                    //  ingredient.setRecipe(recipe);
                    recipe.addIngredient(ingredient);
                }

                Recipe savedRecipe = recipeReactiveRepository.save(recipe).block();

                Optional<Ingredient> savedIngredientOptional = savedRecipe.getIngredients().stream()
                        .filter(recipeIngredients -> recipeIngredients.getId().equals(command.getId()))
                        .findFirst();

                //check by description
                if (!savedIngredientOptional.isPresent()) {
                    //not totally safe... But best guess
                    savedIngredientOptional = savedRecipe.getIngredients().stream()
                            .filter(recipeIngredients -> recipeIngredients.getDescription().equals(command.getDescription()))
                            .filter(recipeIngredients -> recipeIngredients.getAmount().equals(command.getAmount()))
                            .filter(recipeIngredients -> recipeIngredients.getUom().getId().equals(command.getUom().getId()))
                            .findFirst();
                }

                //todo check for fail
                return Mono.just(ingredientToIngredientCommand.convert(savedIngredientOptional.get()));
            }

        }

        @Override
        public Mono<Void> deleteById (String recipeId, String idToDelete){

            log.debug("Deleting ingredient: " + recipeId + ":" + idToDelete);

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
            }
            return Mono.empty();
        }
    }

