package com.florian.controllers;

import com.florian.commands.RecipeCommand;
import com.florian.exceptions.NotFoundException;
import com.florian.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.exceptions.TemplateInputException;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

/**
 * Created by jt on 6/19/17.
 */
@Slf4j
@Controller
public class RecipeController {

    private static final String RECIPE_RECIPEFORM_URL = "recipe/recipeform";
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/recipe/{id}/show")
    public String showById(@PathVariable String id, Model model){

        model.addAttribute("recipe", recipeService.findById(id));

        return "recipe/show";
    }

    @GetMapping("recipe/new")
    public String newRecipe(Model model){
        model.addAttribute("recipe", new RecipeCommand());

        return "recipe/recipeform";
    }

    @GetMapping("recipe/{id}/update")
    public Mono<String> updateRecipe(@PathVariable String id, Model model){
        return recipeService.findById(id)
                .map(recipe -> {
                    model.addAttribute("recipe", recipe);
                    return RECIPE_RECIPEFORM_URL;
                });
    }

    @PostMapping("recipe")
    public Mono<String> saveOrUpdate(@ModelAttribute("recipe") RecipeCommand command){
        return recipeService.saveRecipeCommand(command).map(recipeCommand -> "redirect:/recipe/" + recipeCommand.getId() + "/show");
    }

    @GetMapping("recipe/{id}/delete")
    public String deleteById(@PathVariable String id){

        log.debug("Deleting id: " + id);

        recipeService.deleteById(id).subscribe();
        return "redirect:/";
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler({NotFoundException.class, TemplateInputException.class, NoSuchElementException.class})
    public String handleNotFound(Exception exception, Model model){

        log.error("Handling not found exception");
        log.error(exception.getMessage());
        model.addAttribute("exception", exception);

        return "404error";
    }

}
