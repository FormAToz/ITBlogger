package main.api.response.result;

/**
 * Формат ответа:
 *
 * {
 * 	"result": true,
 * 	"path": "df/df/fhf/image.png"
 * }
 */
public class ImageResultResponse extends ResultResponse{
    private String path;

    public ImageResultResponse(boolean result) {
        super(result);
    }

    public ImageResultResponse(boolean result, String path) {
        super(result);
        this.path = path;
    }

    @Override
    public boolean isResult() {
        return super.isResult();
    }

    public String getPath() {
        return path;
    }
}
