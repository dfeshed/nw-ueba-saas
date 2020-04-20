package fortscale.services.impl;

import fortscale.services.EntityTagService;
import fortscale.temp.HardCodedMocks;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shays on 06/07/2017.
 */
@Service
public class EntityTagsServiceImpl implements EntityTagService {


    @Override
    public void update() throws Exception {

    }

    @Override
    public void addEntityTags(String userName, List<String> tags) throws Exception {

    }

    @Override
    public void addEntityTagsRegex(String userRegex, List<String> tags) throws Exception {

    }

    @Override
    public void removeEntityTags(String userName, List<String> tags) {

    }

    @Override
    public int removeTagFromAllEntities(String tag) {
        return HardCodedMocks.REMOVED_USERS;
    }
}
