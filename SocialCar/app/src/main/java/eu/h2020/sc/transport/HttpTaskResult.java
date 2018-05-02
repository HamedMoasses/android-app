package eu.h2020.sc.transport;

/**
 * Created by fminori on 25/09/15.
 */
public class HttpTaskResult {

    private String content;
    private int httpStatusCode;
    private byte[] pictureData;

    public HttpTaskResult() {
    }

    public HttpTaskResult(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(int httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getPictureData() {
        return pictureData;
    }

    public void setPictureData(byte[] pictureData) {
        this.pictureData = pictureData;
    }
}
