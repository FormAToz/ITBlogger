package main.api.response;

import java.util.List;
import java.util.Map;

/**
 * Формат ответа:
 *
 * {
 * 	    "years": [2017, 2018, 2019, 2020],
 * 	    "posts": {
 * 		    "2019-12-17": 56,
 * 		    "2019-12-14": 11,
 * 		    "2019-06-17": 1,
 * 		    "2020-03-12": 6
 *      }
 * }
 */
public class CalendarResponse {
    private final List<Integer> years;
    private final Map<String, Long> posts;

    public CalendarResponse(List<Integer> years, Map<String, Long> posts) {
        this.years = years;
        this.posts = posts;
    }

    public List<Integer> getYears() {
        return years;
    }

    public Map<String, Long> getPosts() {
        return posts;
    }
}
