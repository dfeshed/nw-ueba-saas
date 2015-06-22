package fortscale.domain.core.dao.rest;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rans on 22/06/15.
 * This bean is used to serialize json for links in the REST API
 */
public class Links implements Serializable {
    private static final long serialVersionUID = -4941089327875820384L;
    private List<LinkUrl> links;

    public Links(List<LinkUrl> links) {
        this.links = links;
    }

    public List<LinkUrl> getLinks() {
        return links;
    }

    public void setLinks(List<LinkUrl> links) {
        this.links = links;
    }
}
