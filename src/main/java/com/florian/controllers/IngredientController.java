package com.florian.controllers;

import com.florian.commands.IngredientCommand;
import com.florian.commands.RecipeCommand;
import com.florian.commands.UnitOfMeasureCommand;
import com.florian.services.IngredientService;
import com.florian.services.RecipeService;
import com.florian.services.UnitOfMeasureService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Created by jt on 6/28/17.
 */
@Slf4j
@Controller
public class IngredientController {

    private final IngredientService ingredientService;
    private final RecipeService recipeService;
    private final UnitOfMeasureService unitOfMeasureService;

    public IngredientController(IngredientService ingredientService, RecipeService recipeService, UnitOfMeasureService unitOfMeasureService) {
        this.ingredientService = ingredientService;
        this.recipeService = recipeService;
        this.unitOfMeasureService = unitOfMeasureService;
    }

    @GetMapping("/recipe/{recipeId}/ingredients")
    public String listIngredients(@PathVariable String recipeId, Model model){
        log.debug("Getting ingredient list for recipe id: " + recipeId);

        // use command object to avoid lazy load errors in Thymeleaf.
        model.addAttribute("recipe", recipeService.findCommandById(recipeId));

        return "recipe/ingredient/list";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/show")
    public String showRecipeIngredient(@PathVariable String recipeId,
                                       @PathVariable String id, Model model){
        model.addAttribute("ingredient", ingredientService.findByRecipeIdAndIngredientId(recipeId, id));
        return "recipe/ingredient/show";
    }

    @GetMapping("recipe/{recipeId}/ingredient/new")
    public String newRecipeIngredient(@PathVariable String recipeId, Model model){

        //make sure we have a good id value
        Mono<RecipeCommand> recipeCommand = recipeService.findCommandById(recipeId);
        //todo raise exception if null

        //need to return back parent id for hidden form property
        IngredientCommand ingredientCommand = new IngredientCommand();
        ingredientCommand.setRecipeId(recipeId);
        model.addAttribute("ingredient", ingredientCommand);

        //init uom
        ingredientCommand.setUom(new UnitOfMeasureCommand());

//        model.addAttribute("uomList",  unitOfMeasureService.listAllUoms());

        return "recipe/ingredient/ingredientform";
    }

    @GetMapping("recipe/{recipeId}/ingredient/{id}/update")
    public Mono<String> updateRecipeIngredient(@PathVariable String recipeId,
                                               @PathVariable String id, Model model){
        return ingredientService.findByRecipeIdAndIngredientId(recipeId,id)
                .map(ingredientCommand -> {
                    model.addAttribute("ingredient", ingredientCommand);
                    return "recipe/ingredient/ingredientform";
                });



    }

    @PostMapping("recipe/{recipeId}/ingredient")
    public Mono<String> saveOrUpdate(@ModelAttribute IngredientCommand command, @PathVariable String recipeId){
        command.setRecipeId(recipeId);
        Mono<IngredientCommand> savedCommand = ingredientService.saveIngredientCommand(command);
        return savedCommand.map(ingredientCommand -> {
            String ingredientId = ingredientCommand.getId();
            log.debug("saved ingredient id:" + ingredientId);
            return "redirect:/recipe/" + command.getRecipeId() + "/ingredient/" + ingredientId + "/show";
        });

    }

    @GetMapping("recipe/{recipeId}/ingredient/{ingredientId}/delete")
    public String deleteIngredient(@PathVariable String recipeId,
                                   @PathVariable String ingredientId){

        log.debug("deleting ingredient id:" + ingredientId);
        ingredientService.deleteById(recipeId, ingredientId);

        return "redirect:/recipe/" + recipeId + "/ingredients";
    }

    @ModelAttribute("uomList")
    public Flux<UnitOfMeasureCommand> populateUomList() {
        return unitOfMeasureService.listAllUoms();
    }
}
