package main.service;

import main.api.response.result.ErrorResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Service
public class ImageService {
    public ResponseEntity loadImage(MultipartFile image) {
        if (true) {
            // TODO загрузка завершена успешно
            System.out.println(image);

            return new ResponseEntity<>("/upload/ab/cd/ef/52461.jpg", HttpStatus.OK);
        }
        else {
            // TODO в случае ошибки
            Map<String, String> errors = new HashMap<>();

            return new ResponseEntity<>(new ErrorResultResponse(false, errors), HttpStatus.BAD_REQUEST);
        }
    }
}
