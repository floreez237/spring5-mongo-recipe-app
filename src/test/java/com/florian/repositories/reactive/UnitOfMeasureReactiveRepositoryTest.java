package com.florian.repositories.reactive;

import com.florian.domain.Category;
import com.florian.domain.Recipe;
import com.florian.domain.UnitOfMeasure;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Mono;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@DataMongoTest
public class UnitOfMeasureReactiveRepositoryTest {

    @Autowired
    private UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    @Before
    public void setUp() throws Exception {
        unitOfMeasureReactiveRepository.deleteAll().block();
    }

    @Test
    public void testSave() {
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setDescription("recipe");
        unitOfMeasure = unitOfMeasureReactiveRepository.save(unitOfMeasure).block();

        assertNotNull(unitOfMeasure);
        assertNotNull(unitOfMeasure.getId());
        assertEquals(new Long("1"),unitOfMeasureReactiveRepository.count().block());
    }

    @Test
    public void testFindInFlux() {
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure(), unit1 = new UnitOfMeasure();
        unitOfMeasure.setDescription("unitOfMeasure");
        unit1.setDescription("unit1");
        unitOfMeasure = unitOfMeasureReactiveRepository.save(unitOfMeasure).block();
        unit1 = unitOfMeasureReactiveRepository.save(unit1).block();

        assertNotNull(unitOfMeasure);
        assertNotNull(unitOfMeasure.getId());
        assertNotNull(unit1);
        assertNotNull(unit1.getId());
        assertEquals(new Long("2"),unitOfMeasureReactiveRepository.count().block());
        assertEquals(new Long("1"),unitOfMeasureReactiveRepository.findAll().filter(unit -> unit.getDescription().equals("unitOfMeasure")).count().block());

    }

    @Test
    public void findByDescription() {
        UnitOfMeasure unitOfMeasure = new UnitOfMeasure();
        unitOfMeasure.setDescription("unitOfMeasure");
        unitOfMeasureReactiveRepository.save(unitOfMeasure).block();

        Mono<UnitOfMeasure> unitOfMeasureMono = unitOfMeasureReactiveRepository.findByDescription("unitOfMeasure");
        assertNotNull(unitOfMeasureMono.block());
    }
}