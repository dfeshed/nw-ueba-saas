package fortscale.domain.core.dao;

/**
 * Created by Amir Keren on 02/12/15.
 *
 * This POJO class is used as the result of the api call userTags in user controller
 */
public class Tag {

    private String id;
    private String name;

    public Tag() {}

    public Tag(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}