package fortscale.services;

import fortscale.domain.core.activities.UserActivityLocationDocument;

import java.util.List;

public interface UserActivityService {

    List<UserActivityLocationDocument> getUserActivityLocationEntries(String id, int timeRangeInDays);

}
