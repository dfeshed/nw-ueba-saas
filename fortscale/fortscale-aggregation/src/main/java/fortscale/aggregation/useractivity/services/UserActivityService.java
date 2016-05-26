package fortscale.aggregation.useractivity.services;

import fortscale.aggregation.useractivity.data.LocationEntries;

import java.util.List;

public interface UserActivityService {
    List<LocationEntries> getLocationEntries();
}
