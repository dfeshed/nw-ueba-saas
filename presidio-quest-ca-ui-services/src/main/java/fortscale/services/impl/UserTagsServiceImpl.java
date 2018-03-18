package fortscale.services.impl;

import fortscale.services.UserTagService;
import fortscale.temp.HardCodedMocks;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by shays on 06/07/2017.
 */
@Service
public class UserTagsServiceImpl implements UserTagService {


    @Override
    public void update() throws Exception {

    }

    @Override
    public void addUserTags(String userName, List<String> tags) throws Exception {

    }

    @Override
    public void addUserTagsRegex(String userRegex, List<String> tags) throws Exception {

    }

    @Override
    public void removeUserTags(String userName, List<String> tags) {

    }

    @Override
    public int removeTagFromAllUsers(String tag) {
        return HardCodedMocks.REMOVED_USERS;
    }
}
