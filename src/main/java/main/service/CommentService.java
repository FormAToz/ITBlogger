package main.service;

import main.api.request.CommentRequest;
import main.api.response.IdResponse;
import main.api.response.result.ErrorResultResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class CommentService {
    public ResponseEntity addComment(CommentRequest commentRequest) {
        if (true) {
            // TODO В случае успешного ответа
            int id = commentRequest.getParentId(); // id комментария

            return new ResponseEntity<>(new IdResponse(id), HttpStatus.OK);
        }
        else {
            // TODO В случае ошибки
            Map<String, String> errors = new HashMap<>();

            return new ResponseEntity<>(new ErrorResultResponse(false, errors), HttpStatus.OK);
        }
    }
}
