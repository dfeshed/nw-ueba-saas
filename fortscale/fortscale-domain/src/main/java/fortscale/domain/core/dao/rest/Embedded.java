package fortscale.domain.core.dao.rest;

import java.io.Serializable;

/**
 * Created by rans on 22/06/15.
 * This bean is used to serialize json for any 'list' request in the REST API
 */
public class Embedded<T> implements Serializable {
    private static final long serialVersionUID = 1932251758894613227L;
    private T data;

    public Embedded(T data) {
        this.data = data;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
