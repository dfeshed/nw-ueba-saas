package fortscale.presidio.output.client.mock;


import fortscale.presidio.output.client.api.EntitiesPresidioOutputClient;
import fortscale.presidio.output.client.mock.creators.FakeAlertsCreator;
import fortscale.presidio.output.client.mock.creators.FakeEntitiesCreator;

import presidio.output.client.api.EntitiesApi;

import presidio.output.client.client.ApiException;
import presidio.output.client.model.*;

import java.util.HashMap;
import java.util.Map;


public class FakeRemoteUserClientService implements EntitiesPresidioOutputClient {

    private FakeAlertsCreator alertsCreator;
    private FakeEntitiesCreator entitiesCreator;


    public FakeRemoteUserClientService(FakeAlertsCreator alertsCreator, FakeEntitiesCreator entitiesCreator) {
        this.alertsCreator = alertsCreator;
        this.entitiesCreator = entitiesCreator;
    }

    @Override
    public EntitiesApi getConterollerApi() {
        return new FakeEntitiesApi();
    }


    private class FakeEntitiesApi extends EntitiesApi {

        @Override
        public AlertsWrapper getAlertsByEntity(String userId, EntityAlertsQuery body) throws ApiException {
            return alertsCreator.getAlerts(10);
        }

        @Override
        public Entity getEntity(String userId, Boolean expand) throws ApiException {
            return entitiesCreator.getEntity(userId,"user"+userId);
        }

        @Override
        public EntitiesWrapper getEntities(EntityQuery body) throws ApiException {
            EntitiesWrapper entitiesWrapper = entitiesCreator.getEntities(10);

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
            entitiesWrapper.setAggregationData(aggregationData);
            return entitiesWrapper;

        }

        @Override
        public Entity updateEntity(String userId, JsonPatch body) throws ApiException {
            return entitiesCreator.getEntity(userId,userId+"name");

        }

        @Override
        public EntitiesWrapper updateEntities(EntityPatchBody body) throws ApiException {
            return entitiesCreator.getEntities(10);
        }
    }
}
