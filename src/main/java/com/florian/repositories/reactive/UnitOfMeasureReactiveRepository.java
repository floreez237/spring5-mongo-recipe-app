package com.florian.repositories.reactive;

import com.florian.domain.UnitOfMeasure;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UnitOfMeasureReactiveRepository extends ReactiveMongoRepository<UnitOfMeasure,String> {
    Mono<UnitOfMeasure> findByDescription(String description);
}
