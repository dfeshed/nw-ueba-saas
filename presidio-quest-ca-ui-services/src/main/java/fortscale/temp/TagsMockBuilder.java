package fortscale.temp;

import fortscale.domain.core.Severity;
import fortscale.domain.core.Tag;
import fortscale.domain.core.User;
import org.apache.commons.collections.ListUtils;
import org.apache.commons.collections.SetUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by shays on 05/07/2017.
 */
public class TagsMockBuilder {


    private String id;
    private String name;


    public TagsMockBuilder(int serial){
        this.id="id"+serial;
        this.name="tag"+serial;

    }

    public TagsMockBuilder setId(String id) {
        this.id = id;
        return this;
    }

    public TagsMockBuilder setName(String name) {
        this.name= name;
        return this;
    }


    public Tag createInstance(){
        Tag tag = new Tag(this.name);
        tag.setDeleted(false);
        tag.setCreatesIndicator(true);
        tag.setIsAssignable(true);
        tag.setPredefined(true);
        tag.setRules(ListUtils.EMPTY_LIST);
        tag.setMockId(this.id);

        return tag;
    }
}
