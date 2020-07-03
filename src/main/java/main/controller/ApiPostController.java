package main.controller;

import main.model.entity.Post;
import main.model.service.impl.PostService;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Класс обрабатывает все запросы /api/post/*
 * */
@RestController
@RequestMapping("api/post")
public class ApiPostController {
    @Autowired
    PostService postService;

    // все посты для отображения
    @GetMapping
    public JSONObject getPosts(int offset, int limit, String mode) {
        return postService.getPosts(offset, limit, mode);
    }

    // Добавление поста
    @PostMapping
    public ResponseEntity addPost(@RequestBody Post post) {
        return postService.addPost(post);
    }

}
