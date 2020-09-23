package main.api.response;

/**
 * Формат ответа:
 *
 * {
 *  "postsCount": 7,
 *  "likesCount": 15,
 *  "dislikesCount": 2,
 *  "viewsCount": 58,
 *  "firstPublication": 1590217200 (время в формате UTC)
 * }
 */
public class StatisticsResponse {
    private long postsCount;
    private long likesCount;
    private long dislikesCount;
    private long viewsCount;
    private long firstPublication;

    public StatisticsResponse() {
    }

    public StatisticsResponse postsCount(long postsCount) {
        this.postsCount = postsCount;
        return this;
    }

    public StatisticsResponse likesCount(long likesCount) {
        this.likesCount = likesCount;
        return this;
    }

    public StatisticsResponse dislikesCount(long dislikesCount) {
        this.dislikesCount = dislikesCount;
        return this;
    }

    public StatisticsResponse viewsCount(long viewsCount) {
        this.viewsCount = viewsCount;
        return this;
    }

    public StatisticsResponse firstPublication(long firstPublication) {
        this.firstPublication = firstPublication;
        return this;
    }

    public long getPostsCount() {
        return postsCount;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public long getDislikesCount() {
        return dislikesCount;
    }

    public long getViewsCount() {
        return viewsCount;
    }

    public long getFirstPublication() {
        return firstPublication;
    }
}
