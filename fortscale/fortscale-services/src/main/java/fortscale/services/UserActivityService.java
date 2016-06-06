package fortscale.services;

import fortscale.domain.core.UserActivityLocation;

import java.util.List;

public interface UserActivityService {

    List<UserActivityLocation> getUserActivityLocationEntries(String id, int timeRangeInDays);

}
