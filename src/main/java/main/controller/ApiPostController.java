package main.controller;

import main.api.response.PostCountResponse;
import main.api.response.PostResponse;
import main.model.Post;
import main.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Класс обрабатывает все запросы /api/post/*
 * */
@RestController
@RequestMapping("api/post")
public class ApiPostController {
    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    // Все посты для отображения
    @GetMapping
    public ResponseEntity<PostCountResponse> getPosts(int offset, int limit, String mode) {
        return postService.getPosts(offset, limit, mode);
    }

    // Добавление поста
    @PostMapping
    public ResponseEntity addPost(@RequestBody Post post) {
        return postService.addPost(post);
    }

    // Поиск поста
    @GetMapping("/search")
    public ResponseEntity<PostCountResponse> searchPost(int offset, int limit, String query) {
        return postService.searchPosts(offset, limit, query);
    }

    // Получение поста
    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable int id) {
        return postService.getPostById(id);
    }

    // Список постов за указанную дату
    @GetMapping("/byDate")
    public ResponseEntity<PostCountResponse> getPostsByDate(int offset, int limit, String date) {
        return postService.getPostsByDate(offset, limit, date);
    }

    // Список постов по тэгу
    @GetMapping("/byTag")
    public ResponseEntity<PostCountResponse> getPostsByTag(int offset, int limit, String tag) {
        return postService.getPostsByTag(offset, limit, tag);
    }
}
