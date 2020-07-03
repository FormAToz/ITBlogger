package main.model.service.impl;

import main.model.entity.Post;
import main.model.repository.PostRepository;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserService userService;

    /**
     * Список постов - GET /api/post/
     * <p>
     * Вывод только активных (поле is_active в таблице Posts равно 1),
     * утверждённых модератором (поле moderation_status равно ACCEPTED)
     * постов с датой публикации не позднее текущего момента (движок должен позволять откладывать публикацию).
     *
     * @param offset - сдвиг от 0 для постраничного вывода
     * @param limit  - количество постов, которое надо вывести (10)
     * @param mode   - режим вывода (сортировка):
     *               recent - сортировать по дате публикации, выводить сначала новые
     *               popular - сортировать по убыванию количества комментариев
     *               best - сортировать по убыванию количества лайков
     *               early - сортировать по дате публикации, выводить сначала старые
     * @return - JSON object
     */
    public JSONObject getPosts(int offset, int limit, String mode) {
        JSONObject jObj = new JSONObject();
        JSONArray jPostsArray = new JSONArray();
        List<Post> filteredPosts = postRepository.findAllFilteredPosts();
        int count = filteredPosts.size();

        sortFilteredPosts(filteredPosts, mode).stream()
                .skip(offset)
                .limit(limit)
                .forEach(post -> {
                    JSONObject jUser = new JSONObject();
                    JSONObject jPost = new JSONObject();
                    String postAnnounce = post.getText().substring(0, 40).concat("...");
                    String postTime = DateTimeFormatter.ofPattern("dd MMMM yyyy, HH:mm").format(post.getTime().minusHours(3));

                    // инициализация юзера
                    jUser.put("id", post.getUserId().getId());
                    jUser.put("name", post.getUserId().getName());
                    // инициализация поста
                    jPost.put("id", post.getId());
                    jPost.put("time", postTime);
                    jPost.put("user", jUser);
                    jPost.put("title", post.getTitle());
                    jPost.put("announce", postAnnounce);
                    jPost.put("likeCount", 0);
                    jPost.put("dislikeCount", 0);
                    jPost.put("commentCount", 0);
                    jPost.put("viewCount", 0);
                    // добавление поста в список постов
                    jPostsArray.add(jPost);
                });
        // итоговая инициализация объекта
        jObj.put("count", count);
        jObj.put("posts", jPostsArray);
        return jObj;
    }

    // Добавление поста - POST /api/post
    public ResponseEntity addPost(Post post) {
        JSONObject response = new JSONObject();
        JSONObject error = new JSONObject();

        response.put("result", false);
        if (post.getTitle().length() < 3) {
            error.put("title", "Заголовок менее 3 символов");
            response.put("errors", error);
            return ResponseEntity.badRequest().body(response);
        }
        if (post.getText().length() < 50) {
            error.put("text", "Текст публикации менее 50 символов");
            response.put("errors", error);
            return ResponseEntity.badRequest().body(response);
        }

        response.put("result", true);
        // Проверка времени публикации
        if (post.getTime().isBefore(LocalDateTime.now())) {
            post.setTime(LocalDateTime.now());
        }
        post.setTime(post.getTime().plusHours(3));
        post.setModerationStatus(Post.ModerationStatus.NEW);
        post.setUserId(userService.getUserById(1));
        post.setViewCount(0);
        postRepository.save(post);

        return ResponseEntity.ok().body(response);
    }

    // Выбор сортировки отображаемых постов
    private List<Post> sortFilteredPosts(List<Post> list, String mode) {
        Comparator<Post> postComparator = null;

        // Сортировать по дате публикации, выводить сначала новые
        if (mode.equals("recent")) {
            postComparator = Comparator.comparing(Post::getTime).reversed();
        }
        // Сортировать по убыванию количества комментариев
        if (mode.equals("popular")) {
            // TODO
        }
        // Сортировать по убыванию количества лайков
        if (mode.equals("best")) {
            // TODO
        }
        // Сортировать по дате публикации, выводить сначала старые
        if (mode.equals("early")) {
            postComparator = Comparator.comparing(Post::getTime);
        }
        return list.stream().sorted(postComparator).collect(Collectors.toList());
    }
}
