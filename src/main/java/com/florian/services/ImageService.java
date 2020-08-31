package com.florian.services;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by jt on 7/3/17.
 */
public interface ImageService {

    void saveImageFile(String recipeId, FilePart file);
}
