package fortscale.services.impl;

import fortscale.domain.core.activities.*;
import fortscale.domain.core.dao.UserActivityRepository;
import fortscale.services.UserActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("UserActivityService")
public class UserActivityServiceImpl implements UserActivityService {

    private final UserActivityRepository userActivityRepository;

    @Autowired
    public UserActivityServiceImpl(UserActivityRepository userActivityRepository) {
        this.userActivityRepository = userActivityRepository;
    }

    @Override
    public List<UserActivityLocationDocument> getUserActivityLocationEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityLocationEntries(id, timeRangeInDays);
    }

    @Override
    public List<UserActivityNetworkAuthenticationDocument> getUserActivityNetworkAuthenticationEntries(String id,
			int timeRangeInDays) {
        return userActivityRepository.getUserActivityNetworkAuthenticationEntries(id, timeRangeInDays);
    }

    @Override
    public List<OrganizationActivityLocationDocument> getOrganizationActivityLocationEntries(int timeRangeInDays) {
        return userActivityRepository.getOrganizationActivityLocationEntries(timeRangeInDays);
    }

    @Override
    public List<UserActivityWorkingHoursDocument> getUserActivityWorkingHoursEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityWorkingHoursEntries(id, timeRangeInDays);
    }

    @Override
    public List<UserActivitySourceMachineDocument> getUserActivitySourceMachineEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivitySourceMachineEntries(id, timeRangeInDays);
    }

    @Override
    public List<UserActivityDataUsageDocument> getUserActivityDataUsageEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityDataUsageEntries(id, timeRangeInDays);
    }

    @Override public Set<String> getUserIdByUserLocation(List<String> userLocations) {
        return userActivityRepository.getUserIdByLocation(userLocations);
    }

    public List<UserActivityTargetDeviceDocument> getUserActivityTargetDeviceEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityTargetDeviceEntries(id, timeRangeInDays);
    }

}