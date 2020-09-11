package main.api.response.post;

import main.api.response.CommentResponse;

import java.util.List;

/**
 * Формат ответа:
 *
 * {
 * 		"id": 345,
 * 		"time": "02 августа 2020, 22:34",
 * 	    "active": true,
 * 		"user":
 *          {
 * 			"id": 88,
 * 			"name": "Дмитрий Петров",
 *           },
 * 		"title": "Заголовок поста",
 * 	    "text": "Текст поста в формате HTML",
 * 		"announce": "Текст анонса поста без HTML-тэгов",
 * 		"likeCount": 36,
 * 		"dislikeCount": 3,
 * 		"commentCount": 15,
 * 		"viewCount": 55,
 *      "comments": [
 *          {
 *              "id": 776,
 *              "time": "Вчера, 17:32",
 *              "text": "Текст комментария в формате HTML",
 *              "user":
 *                  {
 *                      "id": 88,
 *                      "name": "Дмитрий Петров",
 *                      "photo": "/avatars/ab/cd/ef/52461.jpg"
 *                  }
 *          },
 *          {...}
 *  ],
 *  "tags": ["Статьи", "Java"]
 * }
 */
public class PostFullResponse extends PostResponse{
    private boolean active;
    private String text;
    private List<CommentResponse> comments;
    private List<String> tags;

    public PostFullResponse() {
    }

    public PostFullResponse active(boolean active) {
        this.active = active;
        return this;
    }

    public PostFullResponse text(String text) {
        this.text = text;
        return this;
    }

    public PostFullResponse comments(List<CommentResponse> comments) {
        this.comments = comments;
        return this;
    }

    public PostFullResponse tags(List<String> tags) {
        this.tags = tags;
        return this;
    }

    public boolean isActive() {
        return active;
    }

    public String getText() {
        return text;
    }

    public List<CommentResponse> getComments() {
        return comments;
    }

    public List<String> getTags() {
        return tags;
    }
}
