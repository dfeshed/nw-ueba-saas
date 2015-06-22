package fortscale.domain.core.dao.rest;

import java.io.Serializable;

/**
 * Created by rans on 22/06/15.
 * This bean is used to serialize json for a single link in the REST API
 */
public class LinkUrl implements Serializable{
    private static final long serialVersionUID = -8537161850894306703L;
    
    private String name;
    private String href;

    public LinkUrl(String name, String href) {
        this.name = name;
        this.href = href;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }
}
