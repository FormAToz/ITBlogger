package main.controller;

import main.api.request.CommentRequest;
import main.api.request.ModerationRequest;
import main.api.request.ProfileRequest;
import main.api.response.CalendarResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.StatisticsResponse;
import main.api.response.result.ResultResponse;
import main.api.response.tag.TagListResponse;
import main.service.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api")
public class ApiGeneralController {
    private final TagService tagService;
    private final ImageService imageService;
    private final PostService postService;
    private final UserService userService;
    private final SettingsService settingsService;

    public ApiGeneralController(TagService tagService, ImageService imageService, PostService postService, UserService userService, SettingsService settingsService) {
        this.tagService = tagService;
        this.imageService = imageService;
        this.postService = postService;
        this.userService = userService;
        this.settingsService = settingsService;
    }

    @GetMapping("/init")
    public ResponseEntity<InitResponse> init() {
        return new ResponseEntity<>(settingsService.init(), HttpStatus.OK);
    }

    @GetMapping("/settings")
    public ResponseEntity<SettingsResponse> getSettings() {
        return new ResponseEntity<>(settingsService.getSettings(), HttpStatus.OK);
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void saveSettings(@RequestBody SettingsResponse settingsResponse) {
        settingsService.saveSettings(settingsResponse);
    }

    @GetMapping("/tag")
    public ResponseEntity<TagListResponse> getListOfTags() {
        return new ResponseEntity<>(tagService.tags(), HttpStatus.OK);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> loadImage(MultipartFile image) throws IOException {
        return new ResponseEntity<>(imageService.loadImage(image).getPath(),HttpStatus.OK);
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> SetPostComment(@RequestBody CommentRequest commentRequest) {
        return new ResponseEntity<>(postService.addComment(commentRequest), HttpStatus.OK);
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultResponse> moderatePost(@RequestBody ModerationRequest request) {
        boolean result = postService.moderate(request.getPostId(), request.getDecision());
        return new ResponseEntity<>(new ResultResponse(result), HttpStatus.OK);
    }

    @GetMapping("/calendar")
    public ResponseEntity<CalendarResponse> getPostsByYear(int year) {
        return new ResponseEntity<>(postService.getAllPostsForCalendar(year), HttpStatus.OK);
    }

    @PostMapping(value = "/profile/my",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> editMyProfile(@RequestParam("photo") MultipartFile image,
                                                        @ModelAttribute ProfileRequest profileRequest) throws IOException {
        return new ResponseEntity<>(userService.editMyProfile(image, profileRequest), HttpStatus.OK);
    }

    @PostMapping("/profile/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> editMyProfile(@RequestBody ProfileRequest profileRequest) {
        return new ResponseEntity<>(userService.editMyProfile(profileRequest), HttpStatus.OK);
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        return new ResponseEntity<>(userService.getMyStatistics(), HttpStatus.OK);
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticsResponse> getAllStatistics() {
        return new ResponseEntity<>(postService.getGlobalStatistics(), HttpStatus.OK);
    }
}