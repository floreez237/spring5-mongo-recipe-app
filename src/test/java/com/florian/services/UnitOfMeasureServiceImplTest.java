package com.florian.services;

import com.florian.commands.UnitOfMeasureCommand;
import com.florian.converters.UnitOfMeasureToUnitOfMeasureCommand;
import com.florian.domain.UnitOfMeasure;
import com.florian.repositories.UnitOfMeasureRepository;
import com.florian.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Flux;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class UnitOfMeasureServiceImplTest {

    UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand = new UnitOfMeasureToUnitOfMeasureCommand();
    UnitOfMeasureService service;

    @Mock
    UnitOfMeasureReactiveRepository unitOfMeasureRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        service = new UnitOfMeasureServiceImpl(unitOfMeasureToUnitOfMeasureCommand, unitOfMeasureRepository);
    }

    @Test
    public void listAllUoms() throws Exception {
        //given
        UnitOfMeasure uom1 = new UnitOfMeasure();
        uom1.setId("1");

        UnitOfMeasure uom2 = new UnitOfMeasure();
        uom2.setId("2");

        when(unitOfMeasureRepository.findAll()).thenReturn(Flux.just(uom1,uom2));

        //when
        Flux<UnitOfMeasureCommand> commands = service.listAllUoms();

        //then
        assertEquals(new Long("2"), commands.count().block());
        verify(unitOfMeasureRepository, times(1)).findAll();
    }

}