package fortscale.domain.rest;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

/**
 * Created by shays on 04/05/2016.
 */
public abstract class RestFilter {


    //Variables
    private Integer size;
    private Integer fromPage;



    //Getters / Setters
    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getFromPage() {
        return fromPage;
    }

    public void setFromPage(Integer fromPage) {
        this.fromPage = fromPage;
    }




}
