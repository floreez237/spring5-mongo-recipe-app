package com.florian.controllers;

import com.florian.domain.Recipe;
import com.florian.services.RecipeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Created by jt on 6/1/17.
 */
@Slf4j
@Controller
public class IndexController {

    private final RecipeService recipeService;

    public IndexController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @RequestMapping({"", "/", "/index"})
    public String getIndexPage(Model model) {
        log.debug("Getting Index page");
//        List<Recipe> listOfRecipes = new ArrayList<>();
//        recipeService.getRecipes().subscribe(listOfRecipes::add);
        model.addAttribute("recipes",recipeService.getRecipes());

        return "index";
    }
}
