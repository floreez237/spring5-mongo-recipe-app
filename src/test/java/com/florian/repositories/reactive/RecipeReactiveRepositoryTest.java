package com.florian.repositories.reactive;

import com.florian.domain.Recipe;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class RecipeReactiveRepositoryTest {

    @Autowired
    private RecipeReactiveRepository recipeReactiveRepository;

    @Before
    public void setUp() throws Exception {
        recipeReactiveRepository.deleteAll().block();
    }

    @Test
    public void testSave() {
        Recipe recipe = new Recipe();
        recipe.setDescription("recipe");
        recipe = recipeReactiveRepository.save(recipe).block();

        assertNotNull(recipe);
        assertNotNull(recipe.getId());
        assertEquals(new Long("1"),recipeReactiveRepository.count().block());
    }

    @Test
    public void testFindByDescription() {
        Recipe recipe = new Recipe(), recipe1 = new Recipe();
        recipe.setDescription("recipe");
        recipe1.setDescription("recipe1");
        recipe = recipeReactiveRepository.save(recipe).block();
        recipe1 = recipeReactiveRepository.save(recipe1).block();

        assertNotNull(recipe);
        assertNotNull(recipe.getId());
        assertNotNull(recipe1);
        assertNotNull(recipe1.getId());
        assertEquals(new Long("2"),recipeReactiveRepository.count().block());
        assertEquals(new Long("1"),recipeReactiveRepository.findAll().filter(recipe2 -> recipe2.getDescription().equals("recipe")).count().block());

    }
}