package model;

public class Response {

    private Result result;
    private String message;

    public Response() {}

    public Result getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setResult(Result result) {
        this.result = result;
    }
}
