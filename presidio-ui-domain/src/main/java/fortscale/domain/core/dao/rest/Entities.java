package fortscale.domain.core.dao.rest;


import fortscale.domain.core.Entity;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rans on 22/06/15.
 * This bean is used to serialize json for alerts request in the REST API
 */
public class Entities implements Serializable {
    private static final long serialVersionUID = 9109074252875664042L;
    private List<Entity> entities;
    private long totalCount;

    public Entities(List<Entity> entities, long totalCount) {
        this.entities = entities;
        this.totalCount = totalCount;
    }

    public Entities(List<Entity> entities){
        this.entities = entities;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }
}
