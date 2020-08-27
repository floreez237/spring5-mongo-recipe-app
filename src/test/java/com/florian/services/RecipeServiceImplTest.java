package com.florian.services;


import com.florian.commands.RecipeCommand;
import com.florian.converters.RecipeCommandToRecipe;
import com.florian.converters.RecipeToRecipeCommand;
import com.florian.domain.Recipe;
import com.florian.exceptions.NotFoundException;
import com.florian.repositories.RecipeRepository;
import com.florian.repositories.reactive.RecipeReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

/**
 * Created by jt on 6/17/17.
 */
public class RecipeServiceImplTest {

    RecipeServiceImpl recipeService;

    @Mock
    RecipeRepository recipeRepository;

    @Mock
    RecipeToRecipeCommand recipeToRecipeCommand;

    @Mock
    RecipeCommandToRecipe recipeCommandToRecipe;

    @Mock
    RecipeReactiveRepository recipeReactiveRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        recipeService = new RecipeServiceImpl(recipeRepository, recipeCommandToRecipe, recipeToRecipeCommand, recipeReactiveRepository);
    }

    @Test
    public void getRecipeByIdTest() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1");
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipeOptional.get()));

        Mono<Recipe> recipeReturned = recipeService.findById("1");

        assertNotNull("Null recipe returned", recipeReturned);
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, never()).findAll();
    }

    @Test(expected = NotFoundException.class)
    public void getRecipeByIdTestNotFound() throws Exception {


        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.empty());

        Mono<Recipe> recipeReturned = recipeService.findById("1");

        //should go boom
    }

    @Test
    public void getRecipeCommandByIdTest() throws Exception {
        Recipe recipe = new Recipe();
        recipe.setId("1");
        Optional<Recipe> recipeOptional = Optional.of(recipe);

        when(recipeReactiveRepository.findById(anyString())).thenReturn(Mono.just(recipeOptional.get()));

        RecipeCommand recipeCommand = new RecipeCommand();
        recipeCommand.setId("1");

        when(recipeToRecipeCommand.convert(any())).thenReturn(recipeCommand);

        Mono<RecipeCommand> commandById = recipeService.findCommandById("1");

        assertNotNull("Null recipe returned", commandById);
        verify(recipeReactiveRepository, times(1)).findById(anyString());
        verify(recipeReactiveRepository, never()).findAll();
    }

    @Test
    public void getRecipesTest() throws Exception {

        Recipe recipe = new Recipe();
        HashSet<Recipe> receipesData = new HashSet();
        receipesData.add(recipe);

        when(recipeService.getRecipes()).thenReturn(Flux.fromIterable(receipesData));

        Flux<Recipe> recipes = recipeService.getRecipes();

        assertEquals(recipes.count().block(), new Long("1"));
        verify(recipeReactiveRepository, times(1)).findAll();
        verify(recipeReactiveRepository, never()).findById(anyString());
    }

    @Test
    public void testDeleteById() throws Exception {

        //given
        String idToDelete = "2";

        //when
        recipeService.deleteById(idToDelete);

        //no 'when', since method has void return type

        //then
        verify(recipeReactiveRepository, times(1)).deleteById(anyString());
    }
}