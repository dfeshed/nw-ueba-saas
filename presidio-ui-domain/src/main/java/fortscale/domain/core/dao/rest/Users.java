package fortscale.domain.core.dao.rest;


import fortscale.domain.core.Alert;
import fortscale.domain.core.User;

import java.io.Serializable;
import java.util.List;

/**
 * Created by rans on 22/06/15.
 * This bean is used to serialize json for alerts request in the REST API
 */
public class Users implements Serializable {
    private static final long serialVersionUID = 9109074252875664042L;
    private List<User> users;
    private long totalCount;

    public Users(List<User> users, long totalCount) {
        this.users = users;
        this.totalCount = totalCount;
    }

    public Users(List<User> users){
        this.users= users;
    }

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
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
