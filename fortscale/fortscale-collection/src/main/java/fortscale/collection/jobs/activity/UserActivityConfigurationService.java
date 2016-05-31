package fortscale.collection.jobs.activity;

import java.util.*;

/**
 * @author gils
 * 24/05/2016
 */
public class UserActivityConfigurationService {

    private final static String USER_VPN_COLLECTION = "aggr_normalized_username_vpn_daily";
    private final static String USER_CRMSF_COLLECTION = "aggr_normalized_username_crmsf_daily";

    private static Set<String> activities;

    private static Map<String, String> dataSourceToCollection;

    private static Map<String, List<String>> activityToDataSources;

    static {
        activities = new HashSet<>();
        activities.add("locations");

        dataSourceToCollection = new HashMap<>();

        dataSourceToCollection.put("vpn", USER_VPN_COLLECTION);
        dataSourceToCollection.put("crmsf", USER_CRMSF_COLLECTION);

        activityToDataSources = new HashMap<>();

        activityToDataSources.put("locations", Arrays.asList("vpn", "crmsf"));
    }

    public Set<String> getActivities() {
        return activities;
    }

    public String getCollectionName(String dataSource) {
        return dataSourceToCollection.get(dataSource);
    }

    public List<String> getDataSources(String activityName) {
        return activityToDataSources.get(activityName);
    }
}
