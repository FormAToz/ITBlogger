package main.api.response;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Формат ответа:
 *
 * {
 *  	"MULTIUSER_MODE": false,
 * 	    "POST_PREMODERATION": true,
 * 	    "STATISTICS_IS_PUBLIC": true
 * }
 */
public class SettingsResponse {
    @JsonProperty("MULTIUSER_MODE")
    private final boolean multiUserMode;
    @JsonProperty("POST_PREMODERATION")
    private final boolean postPreModeration;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private final boolean statisticsIsPublic;

    public SettingsResponse(boolean multiUserMode, boolean postPreModeration, boolean statisticsIsPublic) {
        this.multiUserMode = multiUserMode;
        this.postPreModeration = postPreModeration;
        this.statisticsIsPublic = statisticsIsPublic;
    }

    public boolean isMultiUserMode() {
        return multiUserMode;
    }

    public boolean isPostPreModeration() {
        return postPreModeration;
    }

    public boolean isStatisticsIsPublic() {
        return statisticsIsPublic;
    }
}
