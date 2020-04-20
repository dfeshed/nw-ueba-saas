package presidio.webapp.controllers;

@javax.annotation.Generated(value = "class io.swagger.codegen.languages.SpringCodegen", date = "2017-08-31T11:27:51.258Z")

public class NoFoundException extends ApiException {
    private int code;

    public NoFoundException(int code, String msg) {
        super(code, msg);
        this.code = code;
    }
}
