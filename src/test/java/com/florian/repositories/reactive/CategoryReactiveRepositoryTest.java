package com.florian.repositories.reactive;

import com.florian.domain.Category;
import com.mongodb.MongoNodeIsRecoveringException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@DataMongoTest
public class CategoryReactiveRepositoryTest {

    @Autowired
    private CategoryReactiveRepository categoryReactiveRepository;

    @Before
    public void setUp() throws Exception {
        categoryReactiveRepository.deleteAll().block();
    }


    @Test
    public void testSave() {
        Category category = new Category();
        category.setDescription("A category");
        category = categoryReactiveRepository.save(category).block();

        assertNotNull(category);
        assertNotNull(category.getId());
        assertEquals(new Long("1"), categoryReactiveRepository.count().block());
    }

    @Test
    public void testFindInFlux() {
        categoryReactiveRepository.deleteAll();
        Category category = new Category(), category1 = new Category();
        category.setDescription("A category");
        category1.setDescription("A category1");

        category = categoryReactiveRepository.save(category).block();
        category1 = categoryReactiveRepository.save(category1).block();

        assertNotNull(category);
        assertNotNull(category1);
        assertNotNull(category1.getId());
        assertNotNull(category.getId());
        assertEquals(new Long(2),categoryReactiveRepository.count().block());
        assertEquals(new Long(1),categoryReactiveRepository.findAll().filter(category2 -> category2.getDescription().equals("A category")).count().block());
    }

    @Test
    public void findByDescription() {
        Category category = new Category();
        category.setDescription("category");
        categoryReactiveRepository.save(category).block();

        Mono<Category> categoryMono = categoryReactiveRepository.findByDescription("category");
        assertNotNull(categoryMono.block());
    }
}