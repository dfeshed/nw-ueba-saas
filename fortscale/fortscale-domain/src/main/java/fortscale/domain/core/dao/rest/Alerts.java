package fortscale.domain.core.dao.rest;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rans on 22/06/15.
 * This bean is used to serialize json for alerts request in the REST API
 */
public class Alerts implements Serializable {
    private static final long serialVersionUID = 9109074252875664042L;
    private Links _links;
    private Embedded<List<Alert>> _embedded;
    private Page page;

    public Links get_links() {
        return _links;
    }

    public void set_links(Links _links) {
        this._links = _links;
    }

    public Embedded<List<Alert>> get_embedded() {
        return _embedded;
    }

    public void set_embedded(Embedded<List<Alert>> _embedded) {
        this._embedded = _embedded;
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }
}
