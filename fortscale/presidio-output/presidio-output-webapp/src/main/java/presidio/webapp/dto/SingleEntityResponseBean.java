package presidio.webapp.dto;

import org.apache.http.HttpStatus;

import java.io.Serializable;

public class SingleEntityResponseBean<T> extends ResponseBean implements Serializable {

    private T data;

    public SingleEntityResponseBean(String errorMessage, T data) {
        super(errorMessage);
        this.data = data;
    }

    public SingleEntityResponseBean() {
        this.setStatus(HttpStatus.SC_OK);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
