package presidio.webapp.dto;

import java.util.List;

public class ListResponseBean<T> extends ResponseBean {

    private List<T> data;
    private long total;
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

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }
}
