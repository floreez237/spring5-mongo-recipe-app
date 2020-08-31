package com.florian.controllers;

import com.florian.commands.RecipeCommand;
import com.florian.services.ImageService;
import com.florian.services.RecipeService;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.ServletServerHttpResponse;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by jt on 7/3/17.
 */
@Controller
public class ImageController {

    private final ImageService imageService;
    private final RecipeService recipeService;

    public ImageController(ImageService imageService, RecipeService recipeService) {
        this.imageService = imageService;
        this.recipeService = recipeService;
    }

    @GetMapping("recipe/{id}/image")
    public String showUploadForm(@PathVariable String id, Model model){
        model.addAttribute("recipe", recipeService.findCommandById(id));
        return "recipe/imageuploadform";
    }

    @PostMapping("recipe/{id}/image")
    public Mono<String> handleImagePost(@PathVariable String id, @RequestPart("imagefile")Mono<FilePart> file){

        return file.map(filePart -> {
            imageService.saveImageFile(id, filePart);
            return "redirect:/recipe/" + id + "/show";
        });

    }

    /*@GetMapping("recipe/{id}/recipeimage")
    public void renderImageFromDB(@PathVariable String id, HttpServerResponse response) throws IOException {
        RecipeCommand recipeCommand = recipeService.findCommandById(id).block();
//        exchange.get
        if (recipeCommand.getImage() != null) {
            byte[] byteArray = new byte[recipeCommand.getImage().length];
            int i = 0;

            for (Byte wrappedByte : recipeCommand.getImage()){
                byteArray[i++] = wrappedByte; //auto unboxing
            }
            response.

            response.setContentType("image/jpeg");
            InputStream is = new ByteArrayInputStream(byteArray);
            IOUtils.copy(is, response.getOutputStream());
        }
    }*/
}
