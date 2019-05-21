package fortscale.presidio.output.client.mock;


import fortscale.presidio.output.client.api.UsersPresidioOutputClient;
import fortscale.presidio.output.client.mock.creators.FakeAlertsCreator;
import fortscale.presidio.output.client.mock.creators.FakeUsersCreator;

import presidio.output.client.api.EntitiesApi;
import presidio.output.client.api.UsersApi;

import presidio.output.client.client.ApiException;
import presidio.output.client.model.*;

import java.util.HashMap;
import java.util.Map;


public class FakeRemoteUserClientService implements UsersPresidioOutputClient {

    private FakeAlertsCreator alertsCreator;
    private FakeUsersCreator usersCreator;


    public FakeRemoteUserClientService(FakeAlertsCreator alertsCreator, FakeUsersCreator usersCreator) {
        this.alertsCreator = alertsCreator;
        this.usersCreator = usersCreator;
    }

    @Override
    public EntitiesApi getConterollerApi() {
        return new FakeEntitiesApi();
    }


    private class FakeEntitiesApi extends EntitiesApi {

        @Override
        public AlertsWrapper getAlertsByUser(String userId, EntityAlertsQuery body) throws ApiException {
            return alertsCreator.getAlerts(10);
        }

        @Override
        public Entity getEntity(String userId, Boolean expand) throws ApiException {
            return usersCreator.getUser(userId,"user"+userId);
        }

        @Override
        public UsersWrapper getEn(UserQuery body) throws ApiException {
            UsersWrapper usersWrapper = usersCreator.getUsers(10);

            Map<String,Long> indicatorsMap = new HashMap<>();
            indicatorsMap.put("Indicator Type 1",5L);
            indicatorsMap.put("Indicator Type 2",5L);

            Map<String,Long> alertsMap = new HashMap<>();
            alertsMap.put("Alert Type 1",5L);
            alertsMap.put("Alert Type 2",5L);

            Map<String,Long> severityMap = new HashMap<>();
            severityMap.put(UserQuery.SeverityEnum.CRITICAL.name(),5L);
            severityMap.put(UserQuery.SeverityEnum.HIGH.name(),5L);
            severityMap.put(UserQuery.SeverityEnum.MEDIUM.name(),5L);
            severityMap.put(UserQuery.SeverityEnum.LOW.name(),5L);
            severityMap.put("Alert Type 2",5L);

            Map<String,Long> tagMap = new HashMap<>();
            tagMap.put("admin",5L);






            Map<String,Map<String,Long>> aggregationData = new HashMap<>();
            aggregationData.put(UserQuery.AggregateByEnum.INDICATORS.name(),indicatorsMap);
            aggregationData.put(UserQuery.AggregateByEnum.ALERT_CLASSIFICATIONS.name(),alertsMap);
            aggregationData.put(UserQuery.AggregateByEnum.SEVERITY.name(),severityMap);
            aggregationData.put(UserQuery.AggregateByEnum.TAGS.name(),tagMap);
            usersWrapper.setAggregationData(aggregationData);
            return usersWrapper;

        }

        @Override
        public User updateUser(String userId, JsonPatch body) throws ApiException {
            return usersCreator.getUser(userId,userId+"name");

        }

        @Override
        public UsersWrapper updateUsers(UserPatchBody body) throws ApiException {
            return usersCreator.getUsers(10);
        }
    }
}
