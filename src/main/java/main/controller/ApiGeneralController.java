package main.controller;

import main.Main;
import main.api.request.CommentRequest;
import main.api.request.ModerationRequest;
import main.api.request.ProfileRequest;
import main.api.response.CalendarResponse;
import main.api.response.InitResponse;
import main.api.response.SettingsResponse;
import main.api.response.StatisticsResponse;
import main.api.response.result.ErrorResultResponse;
import main.api.response.result.ResultResponse;
import main.api.response.tag.TagListResponse;
import main.exception.*;
import main.service.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("api")
public class ApiGeneralController {
    private static final Logger LOGGER = LogManager.getLogger(Main.class);
    private static final Marker MARKER = MarkerManager.getMarker("APP_INFO");

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
        try {
            return new ResponseEntity<>(settingsService.getSettings(), HttpStatus.OK);

        } catch (SettingNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/settings")
    @PreAuthorize("hasAuthority('user:moderate')")
    public void saveSettings(@RequestBody SettingsResponse settingsResponse) {
        try {
            settingsService.saveSettings(settingsResponse);

        } catch (SettingNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
        }
    }

    @GetMapping("/tag")
    public ResponseEntity<TagListResponse> getListOfTags() {
        return new ResponseEntity<>(tagService.tags(), HttpStatus.OK);
    }

    @PostMapping(value = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> loadImage(MultipartFile image) {
        try {
            return new ResponseEntity<>(imageService.loadImage(image).getPath(),HttpStatus.OK);

        } catch (IOException | InvalidParameterException e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of("image", e.getMessage())),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/comment")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<?> SetPostComment(@RequestBody CommentRequest commentRequest) {
        try {
            return new ResponseEntity<>(postService.addComment(commentRequest), HttpStatus.OK);

        } catch (UserNotFoundException | PostNotFoundException | CommentNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(HttpStatus.OK);

        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(new ErrorResultResponse(false,
                            Map.of(e.getType(), e.getMessage())), HttpStatus.OK);
        }
    }

    @PostMapping("/moderation")
    @PreAuthorize("hasAuthority('user:moderate')")
    public ResponseEntity<ResultResponse> moderatePost(@RequestBody ModerationRequest request) {
        try {
            postService.moderate(request.getPostId(), request.getDecision());
        }
        catch (UserNotFoundException | PostNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(new ResultResponse(false), HttpStatus.OK);
        }

        return new ResponseEntity<>(new ResultResponse(true), HttpStatus.OK);
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
                                                        @ModelAttribute ProfileRequest profileRequest) {

        try {
            return new ResponseEntity<>(userService.editMyProfile(image, profileRequest), HttpStatus.OK);

        } catch (IOException e) {
            e.printStackTrace();
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of("image", e.getMessage())),
                    HttpStatus.BAD_REQUEST);

        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of(e.getType(), e.getMessage())),
                    HttpStatus.BAD_REQUEST);

        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of("user", e.getMessage())),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/profile/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<ResultResponse> editMyProfile(@RequestBody ProfileRequest profileRequest) {
        try {
            return new ResponseEntity<>(userService.editMyProfile(profileRequest), HttpStatus.OK);

        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of("user", e.getMessage())),
                    HttpStatus.BAD_REQUEST);

        } catch (InvalidParameterException e) {
            return new ResponseEntity<>(
                    new ErrorResultResponse(false, Map.of(e.getType(), e.getMessage())),
                    HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/statistics/my")
    @PreAuthorize("hasAuthority('user:write')")
    public ResponseEntity<StatisticsResponse> getMyStatistics() {
        try {
            return new ResponseEntity<>(userService.getMyStatistics(), HttpStatus.OK);

        } catch (UserNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(HttpStatus.OK);
        }
    }

    @GetMapping("/statistics/all")
    public ResponseEntity<StatisticsResponse> getAllStatistics() {
        try {
            return new ResponseEntity<>(postService.getGlobalStatistics(), HttpStatus.OK);

        } catch (ApplicationException | UserNotFoundException e) {
            LOGGER.info(MARKER, e.getMessage());
            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  // FIXME убрать, после security
        }
    }
}