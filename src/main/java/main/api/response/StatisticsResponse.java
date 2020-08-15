package main.api.response;

import java.time.LocalDateTime;

/**
 * Формат ответа:
 *
 * {
 *  "postsCount": 7,
 *  "likesCount": 15,
 *  "dislikesCount": 2,
 *  "viewsCount": 58,
 *  "firstPublication": "2018-07-16 17:35"
 * }
 */
public class StatisticsResponse {
    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private String firstPublication;

    public int getPostsCount() {
        return postsCount;
    }

    public void setPostsCount(int postsCount) {
        this.postsCount = postsCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public void setDislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public void setViewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
    }

    public String getFirstPublication() {
        return firstPublication;
    }

    public void setFirstPublication(String firstPublication) {
        this.firstPublication = firstPublication;
    }
}
