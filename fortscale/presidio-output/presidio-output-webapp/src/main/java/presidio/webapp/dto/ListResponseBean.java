package presidio.webapp.dto;

import java.io.Serializable;
import java.util.List;

public class ListResponseBean<T> extends ResponseBean implements Serializable {

    private List<T> data;
    private int total;
    private int page;

    public ListResponseBean() {
    }

    public ListResponseBean(String errorMessage, List<T> data, int total, int page) {
        super(errorMessage);
        this.data = data;
        this.total = total;
        this.page = page;
    }

    public List<T> getData() {
        return data;
    }

    public void setData(List<T> data) {
        this.data = data;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
