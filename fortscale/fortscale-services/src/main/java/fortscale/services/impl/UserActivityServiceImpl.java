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
    private static final String EMAIL_RECIPIENT_BLACKLIST_DOMAINS = "user_activity.email_recipient.blacklisted_domains";
    private static final String TOP_APPLICATIONS_BLACKLIST_DOMAINS = "user_activity.top_applications.blacklisted_applications";

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
    public List<UserActivityTopApplicationsDocument> getUserActivityTopApplicationsEntriesWithBlacklist(String id, int timeRangeInDays) {
        List<UserActivityTopApplicationsDocument> userActivityTopApplicationsEntries = getUserActivityTopApplicationsEntries(id, timeRangeInDays);
        return filterBlacklistedTopApplications(userActivityTopApplicationsEntries);
    }

    @Override
    public List<UserActivityTopDirectoriesDocument> getUserActivityTopDirectoriesEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityTopDirectoriesEntries(id, timeRangeInDays);
    }

    @Override
    public List<UserActivityEmailRecipientDomainDocument> getUserActivityEmailRecipientDomainEntriesWithBlacklist(String id, int timeRangeInDays) {
        List<UserActivityEmailRecipientDomainDocument> userActivityEmailRecipientDomainEntries = getUserActivityEmailRecipientDomainEntries(id, timeRangeInDays);
        return filterBlacklistedEmailRecipientDomains(userActivityEmailRecipientDomainEntries);
    }

    @Override
    public List<UserActivityTopDirectoriesDocument> getUserActivityTopDirectoriesEntriesWithBlacklist(String id, int timeRangeInDays) {
        final List<UserActivityTopDirectoriesDocument> userActivityTopDirectoriesEntries = getUserActivityTopDirectoriesEntries(id, timeRangeInDays);
        return filterBlacklistedDirectories(userActivityTopDirectoriesEntries);
    }

    @Override
    public Set<String> getUserIdByUserLocation(List<String> userLocations) {
        return userActivityRepository.getUserIdByLocation(userLocations);
    }

    public List<UserActivityTargetDeviceDocument> getUserActivityTargetDeviceEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityTargetDeviceEntries(id, timeRangeInDays);
    }

    @Override
    public List<UserActivityEmailRecipientDomainDocument> getUserActivityEmailRecipientDomainEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityEmailRecipientDomainEntries(id, timeRangeInDays);
    }

    @Override
    public List<UserActivityClassificationExposureDocument> getUserActivityClassificationExposureEntries(String id, int timeRangeInDays) {
        return userActivityRepository.getUserActivityClassificationExposureEntries(id, timeRangeInDays);
    }

    protected List<UserActivityTopDirectoriesDocument> filterBlacklistedDirectories(List<UserActivityTopDirectoriesDocument> userActivityTopDirectoriesEntries) {
        final ApplicationConfiguration blacklistedDirectoriesConfiguration = applicationConfigurationService.getApplicationConfiguration(BLACKLISTED_DIRECTORIES_KEY);
        if (blacklistedDirectoriesConfiguration == null) {
            logger.info("Can't filter blacklisted directories because there's no configuration for blacklisted directories. key : {}", BLACKLISTED_DIRECTORIES_KEY);
        }
        else {
            for (UserActivityTopDirectoriesDocument userActivityTopDirectoriesEntry : userActivityTopDirectoriesEntries) {
                final Map<String, Double> filteredHistogram = filterByBlacklist(blacklistedDirectoriesConfiguration, userActivityTopDirectoriesEntry);
                userActivityTopDirectoriesEntry.getDirectories().setDirectoriesHistogram(filteredHistogram);
            }
        }

        return userActivityTopDirectoriesEntries;
    }

    private List<UserActivityTopApplicationsDocument> filterBlacklistedTopApplications(List<UserActivityTopApplicationsDocument> userActivityTopApplicationsEntries) {
        final ApplicationConfiguration blacklistedApplicationsConfiguration = applicationConfigurationService.getApplicationConfiguration(TOP_APPLICATIONS_BLACKLIST_DOMAINS);
        if (blacklistedApplicationsConfiguration == null) {
            logger.info("Can't filter blacklisted applications because there's no configuration. key : {}", TOP_APPLICATIONS_BLACKLIST_DOMAINS);
        }
        else {
            for (UserActivityTopApplicationsDocument userActivityTopApplicationsDocument : userActivityTopApplicationsEntries) {
                final Map<String, Double> filteredHistogram = filterByBlacklist(blacklistedApplicationsConfiguration, userActivityTopApplicationsDocument);
                userActivityTopApplicationsDocument.getApplications().setApplicationsHistogram(filteredHistogram);
            }
        }

        return userActivityTopApplicationsEntries;
    }

    private List<UserActivityEmailRecipientDomainDocument> filterBlacklistedEmailRecipientDomains(List<UserActivityEmailRecipientDomainDocument> userActivityEmailRecipientDomainEntries) {
        final ApplicationConfiguration blacklistedDomainsConfiguration = applicationConfigurationService.getApplicationConfiguration(EMAIL_RECIPIENT_BLACKLIST_DOMAINS);
        if (blacklistedDomainsConfiguration == null) {
            logger.info("Can't filter blacklisted domains because there's no configuration. key : {}", EMAIL_RECIPIENT_BLACKLIST_DOMAINS);
        }
        else {
            for (UserActivityEmailRecipientDomainDocument userActivityEmailRecipientDomainDocument : userActivityEmailRecipientDomainEntries) {
                final Map<String, Double> filteredHistogram = filterByBlacklist(blacklistedDomainsConfiguration, userActivityEmailRecipientDomainDocument);
                userActivityEmailRecipientDomainDocument.getRecipientDomains().setRecipientHistogram(filteredHistogram);
            }
        }

        return userActivityEmailRecipientDomainEntries;
    }

    private Map<String, Double> filterByBlacklist(ApplicationConfiguration blacklistedValuesConfiguration, UserActivityDocument userActivityDocument) {
        final Map<String, Double> histogram = userActivityDocument.getHistogram();
        final Set<String> allValues = histogram.keySet();
        final String blacklistValue = blacklistedValuesConfiguration.getValue();
        final Map<String, Double> filteredHistogram = new HashMap<>();
        final List<String> valueToBlacklist = Arrays.asList(blacklistValue.split(","));
        for (String value : allValues) {
            final Double count = histogram.get(value);
            if (!valueToBlacklist.contains(value)) {
                filteredHistogram.put(value, count);
            } else {
                logger.debug("Filtered {} with count {} ", value, count);
            }
        }
        return filteredHistogram;
    }
}