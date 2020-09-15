package main.api.response.post;

import java.util.List;

/**
 * Формат ответа:
 *
 * {
 * 	"count": 390,
 * 	"posts": [
 *                {
 * 			"id": 345,
 * 			"timestamp": "02 августа 2020, 22:34",
 * 			"user":
 *                {
 * 				"id": 88,
 * 				"name": "Дмитрий Петров"
 *                },
 * 			"title": "Заголовок поста",
 * 			"announce": "Текст анонса поста без HTML-тэгов",
 * 			"likeCount": 36,
 * 			"dislikeCount": 3,
 * 			"commentCount": 15,
 * 			"viewCount": 55
 *        },
 *        {...}
 * 	]
 * }
 */
public class PostCountResponse {
    private final int count;
    private final List<PostResponse> posts;

    public PostCountResponse(int count, List<PostResponse> posts) {
        this.count = count;
        this.posts = posts;
    }

    public int getCount() {
        return count;
    }

    public List<PostResponse> getPosts() {
        return posts;
    }
}
