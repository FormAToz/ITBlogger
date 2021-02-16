package main.controller;

import main.Main;
import main.api.request.PostRequest;
import main.api.request.VoteRequest;
import main.api.response.post.PostCountResponse;
import main.api.response.post.PostFullResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.exception.InvalidParameterException;
import main.exception.PostNotFoundException;
import main.exception.UserNotFoundException;
import main.service.PostService;
import main.service.VoteService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("api/post")
public class ApiPostController {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");

    private final PostService postService;
    private final VoteService voteService;

    public ApiPostController(PostService postService, VoteService voteService) {
        this.postService = postService;
        this.voteService = voteService;
    }

    @GetMapping
    public ResponseEntity<PostCountResponse> getAllPosts(int offset, int limit, String mode) {
        return new ResponseEntity<>(postService.getAllSortedPosts(offset, limit, mode), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ResultResponse> addPost(@RequestBody PostRequest postRequest) {
        try {
            return new ResponseEntity<>(postService.addPost(postRequest), HttpStatus.OK);

        } catch (UserNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of(e.getType(), e.getMessage())), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/search")
    public ResponseEntity<PostCountResponse> searchPost(int offset, int limit, String query) {
        return new ResponseEntity<>(postService.searchPosts(offset, limit, query), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostFullResponse> getPostById(@PathVariable int id) {
        try {
            return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
        }
        catch (UserNotFoundException | PostNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<ResultResponse> updatePost(@PathVariable int id, @RequestBody PostRequest postRequest) {
        try {
            return new ResponseEntity<>(postService.updatePost(id, postRequest), HttpStatus.OK);

        } catch (PostNotFoundException | UserNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.NOT_FOUND);

        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of(e.getType(), e.getMessage())), HttpStatus.OK);
        }
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostCountResponse> getAllPostsByDate(int offset, int limit, String date) {
        return new ResponseEntity<>(postService.getPostsByDate(offset, limit, date), HttpStatus.OK);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostCountResponse> getAllPostsByTag(int offset, int limit, String tag) {
        return new ResponseEntity<>(postService.getPostsByTag(offset, limit, tag), HttpStatus.OK);
    }

    @GetMapping("/moderation")
    public ResponseEntity<PostCountResponse> getPostsForModeration(int offset, int limit, String status) {
        return new ResponseEntity<>(postService.getPostsForModeration(offset, limit, status), HttpStatus.OK);
    }

    @GetMapping("/my")
    public ResponseEntity<PostCountResponse> getMyPosts(int offset, int limit, String status) {
        try {
            return new ResponseEntity<>(postService.getMyPosts(offset, limit, status), HttpStatus.OK);

        } catch (UserNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/like")
    public ResponseEntity<ResultResponse> setPostLike(@RequestBody VoteRequest voteRequest) {
        try {
            return new ResponseEntity<>(voteService.likePost(voteRequest.getPostId()), HttpStatus.OK);

        } catch (PostNotFoundException | UserNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }

    @PostMapping("/dislike")
    public ResponseEntity<ResultResponse> setPostDislike(@RequestBody VoteRequest voteRequest) {
        try {
            return new ResponseEntity<>(voteService.dislikePost(voteRequest.getPostId()), HttpStatus.OK);

        } catch (PostNotFoundException | UserNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }
    }
}
