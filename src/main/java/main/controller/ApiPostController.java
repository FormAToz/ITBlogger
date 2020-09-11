package main.controller;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.api.response.post.PostCountResponse;
import main.api.response.post.PostFullResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.exception.PostNotFoundException;
import main.exception.NoSuchTextLengthException;
import main.exception.UserNotFoundException;
import main.model.Post;
import main.service.PostService;
import main.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/post")
public class ApiPostController {
    private final PostService postService;
    private final VoteService voteService;

    public ApiPostController(PostService postService, VoteService voteService) {
        this.postService = postService;
        this.voteService = voteService;
    }

    // Все посты для отображения
    @GetMapping
    public ResponseEntity<PostCountResponse> getAllPosts(int offset, int limit, String mode) {
        return new ResponseEntity<>(postService.getPosts(offset, limit, mode), HttpStatus.OK);
    }

    // Добавление поста
    @PostMapping
    public ResponseEntity<ResultResponse> addPost(@RequestBody Post post) {
        ResultResponse resultResponse = null;

        try {
            resultResponse = postService.addPost(post);

        } catch (UserNotFoundException e) {
            e.printStackTrace();

        } catch (NoSuchTextLengthException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of(e.getType(), e.getMessage())),
                    HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(resultResponse, HttpStatus.OK);
    }

    // Поиск поста
    @GetMapping("/search")
    public ResponseEntity<PostCountResponse> searchPost(int offset, int limit, String query) {
        return new ResponseEntity<>(postService.searchPosts(offset, limit, query), HttpStatus.OK);
    }

    // Получение поста
    @GetMapping("/{id}")
    public ResponseEntity<PostFullResponse> getPostById(@PathVariable int id) {
        PostFullResponse response = null;

        try {
            response = postService.getPostResponseById(id);
        }
        catch (UserNotFoundException | PostNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return  new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Редактирование поста
    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> updatePost(@PathVariable int id, @RequestBody Post post) {
        return new ResponseEntity<>(postService.updatePost(id, post), HttpStatus.OK);
    }

    // Список постов за указанную дату
    @GetMapping("/byDate")
    public ResponseEntity<PostCountResponse> getAllPostsByDate(int offset, int limit, String date) {
        return new ResponseEntity<>(postService.getPostsByDate(offset, limit, date), HttpStatus.OK);
    }

    // Список постов по тэгу
    @GetMapping("/byTag")
    public ResponseEntity<PostCountResponse> getAllPostsByTag(int offset, int limit, String tag) {
        return new ResponseEntity<>(postService.getPostsByTag(offset, limit, tag), HttpStatus.OK);
    }

    // Список постов на модерацию
    @GetMapping("/moderation")
    public ResponseEntity<PostCountResponse> getPostsForModeration(int offset, int limit, String status) {
        return new ResponseEntity<>(postService.getPostsForModeration(offset, limit, status), HttpStatus.OK);
    }

    // Список моих постов
    @GetMapping("/my")
    public ResponseEntity<PostCountResponse> getMyPosts(int offset, int limit, String status) {
        return new ResponseEntity<>(postService.getMyPosts(offset, limit, status), HttpStatus.OK);
    }

    // Лайк поста
    @PostMapping("/like")
    public ResponseEntity<ResultResponse> likePost(@JsonProperty("post_id") int postId) {
        ResultResponse resultResponse = null;

        try {
            resultResponse = voteService.likePost(postId);

        } catch (PostNotFoundException | UserNotFoundException e) {
            e.printStackTrace();
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
        return new ResponseEntity<>(resultResponse, HttpStatus.OK);
    }

    // Дизлайк поста
    @PostMapping("/dislike")
    public ResponseEntity<ResultResponse> dislikePost(@JsonProperty("post_id") int postId) {
        return new ResponseEntity<>(voteService.dislikePost(postId), HttpStatus.OK);
    }
}
