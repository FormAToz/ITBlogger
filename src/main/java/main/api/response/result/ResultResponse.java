package main.api.response.result;

/**
 * Формат ответа:
 *
 * {
 * 	"result": true
 * }
 */
public class ResultResponse {
    protected boolean result;

    public ResultResponse(boolean result) {
        this.result = result;
    }

    public boolean isResult() {
        return result;
    }

    public void setResult(boolean result) {
        this.result = result;
    }
}
