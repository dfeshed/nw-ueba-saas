package presidio.webapp.dto;

public class SingleEntityResponseBean<T> extends ResponseBean {

    private T data;

    public SingleEntityResponseBean(String errorMessage, T data) {
        super(errorMessage);
        this.data = data;
    }

    public SingleEntityResponseBean() {
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
