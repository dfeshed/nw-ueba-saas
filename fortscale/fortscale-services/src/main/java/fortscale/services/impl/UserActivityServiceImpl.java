package fortscale.services.impl;

import fortscale.domain.core.ApplicationConfiguration;
import fortscale.domain.core.activities.*;
import fortscale.domain.core.dao.UserActivityRepository;
import fortscale.services.ApplicationConfigurationService;
import fortscale.services.UserActivityService;
import fortscale.utils.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service("UserActivityService")
public class UserActivityServiceImpl implements UserActivityService {

    private static final Logger logger = Logger.getLogger(UserActivityServiceImpl.class);

    private static final String BLACKLISTED_DIRECTORIES_KEY = "user_activity.directories.blacklisted_directories";
    private final UserActivityRepository userActivityRepository;

    @Autowired
    private ApplicationConfigurationService applicationConfigurationService;

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

    @Override
    public List<UserActivityTopApplicationsDocument> getUserActivityTopApplicationsEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityTopApplicationsEntries(id, timeRangeInDays);
    }

    @Override
    public List<UserActivityTopDirectoriesDocument> getUserActivityTopDirectoriesEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityTopDirectoriesEntries(id, timeRangeInDays);
    }

    @Override
    public List<UserActivityTopDirectoriesDocument> getUserActivityTopDirectoriesEntriesWithBlacklistFiltering(String id, int timeRangeInDays) {
        final List<UserActivityTopDirectoriesDocument> userActivityTopDirectoriesEntries = getUserActivityTopDirectoriesEntries(id, timeRangeInDays);
        if(!filterBlacklistedDirectories(userActivityTopDirectoriesEntries)) {
            logger.error("Failed to getUserActivityTopDirectoriesEntriesWithBlacklistFiltering for id:{}, timeRangeInDays:{}", id, timeRangeInDays);
            return Collections.emptyList();
        }

        return userActivityTopDirectoriesEntries;

    }

    @Override
    public Set<String> getUserIdByUserLocation(List<String> userLocations) {
        return userActivityRepository.getUserIdByLocation(userLocations);
    }

    public List<UserActivityTargetDeviceDocument> getUserActivityTargetDeviceEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityTargetDeviceEntries(id, timeRangeInDays);
    }

    protected boolean filterBlacklistedDirectories(List<UserActivityTopDirectoriesDocument> userActivityTopDirectoriesEntries) {
        for (UserActivityTopDirectoriesDocument userActivityTopDirectoriesEntry : userActivityTopDirectoriesEntries) {
            final Map<String, Double> histogram = userActivityTopDirectoriesEntry.getHistogram();
            final Set<String> directories = histogram.keySet();
            final ApplicationConfiguration blacklistedDirectoriesConfiguration = applicationConfigurationService.getApplicationConfiguration(BLACKLISTED_DIRECTORIES_KEY);
            if (blacklistedDirectoriesConfiguration == null) {
                final String msg = "Can't filter blacklisted directories because there's no configuration for blacklisted directories. key : " + BLACKLISTED_DIRECTORIES_KEY;
                logger.error(msg);
                return false;
            }
            final String blacklistedDirectories = blacklistedDirectoriesConfiguration.getValue();
            final Map<String, Double> filteredHistogram = new HashMap<>();
            for (String directory : directories) {
                if (!blacklistedDirectories.contains(directory)) {
                    final Double directoryCount = histogram.get(directory);
                    filteredHistogram.put(directory, directoryCount);
                }
            }
            userActivityTopDirectoriesEntry.getDirectories().setDirectoriesHistogram(filteredHistogram);
        }

        return true;
    }
}