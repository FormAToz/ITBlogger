package main.api.response;

/**
 * Формат ответа:
 *
 * {
 *  "postsCount": 7,
 *  "likesCount": 15,
 *  "dislikesCount": 2,
 *  "viewsCount": 58,
 *  "firstPublication": 1590217200
 * }
 */
public class StatisticsResponse {
    private int postsCount;
    private int likesCount;
    private int dislikesCount;
    private int viewsCount;
    private long firstPublication;

    public StatisticsResponse() {
    }

    public StatisticsResponse postsCount(int postsCount) {
        this.postsCount = postsCount;
        return this;
    }

    public StatisticsResponse likesCount(int likesCount) {
        this.likesCount = likesCount;
        return this;
    }

    public StatisticsResponse dislikesCount(int dislikesCount) {
        this.dislikesCount = dislikesCount;
        return this;
    }

    public StatisticsResponse viewsCount(int viewsCount) {
        this.viewsCount = viewsCount;
        return this;
    }

    public StatisticsResponse firstPublication(long firstPublication) {
        this.firstPublication = firstPublication;
        return this;
    }

    public int getPostsCount() {
        return postsCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public int getDislikesCount() {
        return dislikesCount;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public long getFirstPublication() {
        return firstPublication;
    }
}
