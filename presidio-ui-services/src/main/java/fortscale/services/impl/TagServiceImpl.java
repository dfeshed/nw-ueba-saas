package fortscale.services.impl;

import fortscale.domain.core.Tag;
import fortscale.services.TagService;
import fortscale.temp.TagsMockBuilder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

/**
 * Created by shays on 06/07/2017.
 */
@Service
public class TagServiceImpl implements TagService {

    private List<Tag> getMockTags(){
        return Arrays.asList(new Tag[]{
                new TagsMockBuilder(1).createInstance(),
                new TagsMockBuilder(2).createInstance(),
                new TagsMockBuilder(3).createInstance(),
                new TagsMockBuilder(4).createInstance(),
                new TagsMockBuilder(5).setName("admin").createInstance()
        });
    }

    private Tag getMockTag(){
        return  new TagsMockBuilder(4).createInstance();

    }

    @Override
    public List<Tag> getAllTags(boolean includeDeleted) {
        return getMockTags();
    }

    @Override
    public boolean addTag(Tag tag) {
        return true;
    }

    @Override
    public Tag getTag(String name) {
        return getMockTag();
    }

    @Override
    public boolean updateTag(Tag tag) {
        return true;
    }

    @Override
    public boolean deleteTag(String name) {
        return true;
    }
}
