package com.florian.services;

import com.florian.commands.UnitOfMeasureCommand;
import com.florian.converters.UnitOfMeasureToUnitOfMeasureCommand;
import com.florian.repositories.reactive.UnitOfMeasureReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Created by jt on 6/28/17.
 */
@Service
public class UnitOfMeasureServiceImpl implements UnitOfMeasureService {

    private final UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand;
    private final UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository;

    public UnitOfMeasureServiceImpl(UnitOfMeasureToUnitOfMeasureCommand unitOfMeasureToUnitOfMeasureCommand, UnitOfMeasureReactiveRepository unitOfMeasureReactiveRepository) {
        this.unitOfMeasureToUnitOfMeasureCommand = unitOfMeasureToUnitOfMeasureCommand;
        this.unitOfMeasureReactiveRepository = unitOfMeasureReactiveRepository;
    }

    @Override
    public Flux<UnitOfMeasureCommand> listAllUoms() {
return unitOfMeasureReactiveRepository.findAll().map(unitOfMeasureToUnitOfMeasureCommand::convert);

//        return StreamSupport.stream(unitOfMeasureRepository.findAll()
//                .spliterator(), false)
//                .map(unitOfMeasureToUnitOfMeasureCommand::convert)
//                .collect(Collectors.toSet());
    }
}
