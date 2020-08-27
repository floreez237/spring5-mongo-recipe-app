package com.florian.repositories.reactive;

import com.florian.domain.Recipe;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface RecipeReactiveRepository extends ReactiveMongoRepository<Recipe, String> {

}
