package fortscale.aggregation.useractivity.Impl;

import fortscale.aggregation.useractivity.data.LocationEntries;
import fortscale.aggregation.useractivity.services.UserActivityService;
import fortscale.domain.core.dao.UserActivityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("UserActivityService")
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityRepository userActivityRepository;

    @Autowired
    public UserActivityServiceImpl(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    @Override
    public List<LocationEntries> getLocationEntries() {
        userActivityRepository.findOneByKey()
    }
}
