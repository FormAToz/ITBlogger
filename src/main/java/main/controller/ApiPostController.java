package main.controller;

import main.api.request.PostRequest;
import main.api.request.VoteRequest;
import main.api.response.post.PostCountResponse;
import main.api.response.post.PostFullResponse;
import main.api.response.result.ResultResponse;
import main.model.enums.SortMode;
import main.model.enums.Status;
import main.service.PostService;
import main.service.VoteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("api/post")
public class ApiPostController {
    private final PostService postService;
    private final VoteService voteService;

    public ApiPostController(PostService postService, VoteService voteService) {
        this.postService = postService;
        this.voteService = voteService;
    }

    @GetMapping
    public ResponseEntity<PostCountResponse> getAllPosts(@RequestParam(defaultValue = "0") int offset,
                                                         @RequestParam(defaultValue = "10") int limit,
                                                         @RequestParam(defaultValue = "recent") SortMode mode) {
        //TODO проверить значения по умолчанию через postman
        return new ResponseEntity<>(postService.getAllSortedPosts(offset, limit, mode), HttpStatus.OK);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> addPost(@RequestBody PostRequest postRequest) {
        return new ResponseEntity<>(postService.addPost(postRequest), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<PostCountResponse> searchPost(@RequestParam(defaultValue = "0") int offset,
                                                        @RequestParam(defaultValue = "10") int limit,
                                                        String query) {
        //TODO проверить значения по умолчанию через postman
        return new ResponseEntity<>(postService.searchPosts(offset, limit, query), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostFullResponse> getPostById(@PathVariable int id, Principal principal) {
        return new ResponseEntity<>(postService.getPostById(id, principal), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> updatePost(@PathVariable int id, @RequestBody PostRequest postRequest) {
        return new ResponseEntity<>(postService.updatePost(id, postRequest), HttpStatus.OK);
    }

    @GetMapping("/byDate")
    public ResponseEntity<PostCountResponse> getAllPostsByDate(@RequestParam(defaultValue = "0") int offset,
                                                               @RequestParam(defaultValue = "10") int limit,
                                                               String date) {
        //TODO проверить значения по умолчанию через postman
        return new ResponseEntity<>(postService.getPostsByDate(offset, limit, date), HttpStatus.OK);
    }

    @GetMapping("/byTag")
    public ResponseEntity<PostCountResponse> getAllPostsByTag(@RequestParam(defaultValue = "0") int offset,
                                                              @RequestParam(defaultValue = "10") int limit,
                                                              String tag) {
        //TODO проверить значения по умолчанию через postman
        return new ResponseEntity<>(postService.getPostsByTag(offset, limit, tag), HttpStatus.OK);
    }

    @GetMapping("/moderation")
    public ResponseEntity<PostCountResponse> getPostsForModeration(@RequestParam(defaultValue = "0") int offset,
                                                                   @RequestParam(defaultValue = "10") int limit,
                                                                   @RequestParam(defaultValue = "new") Status status) {
        //TODO проверить значения по умолчанию через postman
        return new ResponseEntity<>(postService.getPostsForModeration(offset, limit, status), HttpStatus.OK);
    }

    @GetMapping("/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<PostCountResponse> getMyPosts(@RequestParam(defaultValue = "0") int offset,
                                                        @RequestParam(defaultValue = "10") int limit,
                                                        @RequestParam(defaultValue = "inactive") Status status) {
        //TODO проверить значения по умолчанию через postman
        return new ResponseEntity<>(postService.getMyPosts(offset, limit, status), HttpStatus.OK);
    }

    @PostMapping("/like")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> setPostLike(@RequestBody VoteRequest voteRequest) {
        return new ResponseEntity<>(voteService.likePost(voteRequest.getPostId()), HttpStatus.OK);
    }

    @PostMapping("/dislike")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> setPostDislike(@RequestBody VoteRequest voteRequest) {
        return new ResponseEntity<>(voteService.dislikePost(voteRequest.getPostId()), HttpStatus.OK);
    }
}