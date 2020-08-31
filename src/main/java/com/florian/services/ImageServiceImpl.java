package com.florian.services;

import com.florian.domain.Recipe;
import com.florian.repositories.reactive.RecipeReactiveRepository;
import com.mongodb.Bytes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Created by jt on 7/3/17.
 */
@Slf4j
@Service
public class ImageServiceImpl implements ImageService {


    private final RecipeReactiveRepository recipeReactiveRepository;

    public ImageServiceImpl(RecipeReactiveRepository recipeService) {

        this.recipeReactiveRepository = recipeService;
    }

    @Override
    public void saveImageFile(String recipeId, FilePart file) {

        Mono<Byte[]> imageBytes = file.content().map(dataBuffer -> {
            byte[] bytes = new byte[dataBuffer.readableByteCount()];
            dataBuffer.read(bytes);
            DataBufferUtils.release(dataBuffer);

            Byte[] boxedBytes = new Byte[bytes.length];
            for (int i = 0; i < bytes.length; i++) {
                boxedBytes[i] = bytes[i];
            }
            return boxedBytes;
        }).reduce(ArrayUtils::addAll);
        imageBytes.subscribe(bytes -> recipeReactiveRepository.findById(recipeId)
                .subscribe(recipe -> {
                    recipe.setImage(bytes);
                    recipeReactiveRepository.save(recipe).subscribe();
                }));


        /*Mono<Recipe> recipeMono = recipeReactiveRepository.findById(recipeId)
                .map(recipe -> {
                    try {

                        Byte[] byteObjects = new Byte[file.getBytes().length];

                        int i = 0;

                        for (byte b : file.getBytes()) {
                            byteObjects[i++] = b;
                        }

                        recipe.setImage(byteObjects);

                        return recipe;
                    } catch (IOException e) {
                        //todo handle better
                        log.error("Error occurred", e);
                        throw new RuntimeException(e);
                    }
                });
        recipeReactiveRepository.saveAll(recipeMono).blockFirst();*/
    }
}
