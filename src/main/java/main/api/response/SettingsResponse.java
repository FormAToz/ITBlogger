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
    private final boolean postPremoderation;
    @JsonProperty("STATISTICS_IS_PUBLIC")
    private final boolean statisticsIsPublic;

    public SettingsResponse(boolean multiuserMode, boolean postPremoderation, boolean statisticsIsPublic) {
        this.multiUserMode = multiuserMode;
        this.postPremoderation = postPremoderation;
        this.statisticsIsPublic = statisticsIsPublic;
    }

    public boolean isMultiUserMode() {
        return multiUserMode;
    }

    public boolean isPostPremoderation() {
        return postPremoderation;
    }

    public boolean isStatisticsIsPublic() {
        return statisticsIsPublic;
    }
}
