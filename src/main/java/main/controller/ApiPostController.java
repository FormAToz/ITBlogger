package main.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.api.response.post.PostCountResponse;
import main.api.response.post.PostFullResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.model.Post;
import main.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/post")
public class ApiPostController {
    private final PostService postService;

    public ApiPostController(PostService postService) {
        this.postService = postService;
    }

    // Все посты для отображения
    @GetMapping
    public ResponseEntity<PostCountResponse> getAllPosts(int offset, int limit, String mode) {
        return postService.getPosts(offset, limit, mode);
    }

    // Добавление поста
    @PostMapping
    public ResponseEntity<ResultResponse> addPost(@RequestBody Post post) {
        return postService.addPost(post);
    }

    // Поиск поста
    @GetMapping("/search")
    public ResponseEntity<PostCountResponse> searchPost(int offset, int limit, String query) {
        return postService.searchPosts(offset, limit, query);
    }

    // Получение поста
    @GetMapping("/{id}")
    public ResponseEntity<PostFullResponse> getPostById(@PathVariable int id) {
        return postService.getPostById(id);
    }

    // Редактирование поста
    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> updatePost(@PathVariable int id, @RequestBody Post post) {
        return postService.updatePost(id, post);
    }

    // Список постов за указанную дату
    @GetMapping("/byDate")
    public ResponseEntity<PostCountResponse> getAllPostsByDate(int offset, int limit, String date) {
        return postService.getPostsByDate(offset, limit, date);
    }

    // Список постов по тэгу
    @GetMapping("/byTag")
    public ResponseEntity<PostCountResponse> getAllPostsByTag(int offset, int limit, String tag) {
        return postService.getPostsByTag(offset, limit, tag);
    }

    // Список постов на модерацию
    @GetMapping("/moderation")
    public ResponseEntity<PostCountResponse> getPostsForModeration(int offset, int limit, String status) {
        return postService.getPostsForModeration(offset, limit, status);
    }

    // Список моих постов
    @GetMapping("/my")
    public ResponseEntity<PostCountResponse> getMyPosts(int offset, int limit, String status) {
        return postService.getMyPosts(offset, limit, status);
    }

    // Лайк поста
    @PostMapping("/like")
    public ResponseEntity<ResultResponse> likePost(@JsonProperty("post_id") int postId) {
        return postService.likePost(postId);
    }

    // Дизлайк поста
    @PostMapping("/dislike")
    public ResponseEntity<ResultResponse> dislikePost(@JsonProperty("post_id") int postId) {
        return postService.dislikePost(postId);
    }
}
