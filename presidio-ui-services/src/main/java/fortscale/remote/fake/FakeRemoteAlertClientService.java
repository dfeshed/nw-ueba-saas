package fortscale.remote.fake;

import fortscale.remote.RemoteAlertClientService;
import fortscale.remote.RemoteClientServiceAbs;
import fortscale.remote.fake.creators.FakeAlertsCreator;
import fortscale.remote.fake.creators.FakeIndicatorCreators;
import presidio.output.client.api.AlertsApi;
import presidio.output.client.client.ApiClient;
import presidio.output.client.client.ApiException;
import presidio.output.client.model.*;

import java.math.BigDecimal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FakeRemoteAlertClientService extends RemoteAlertClientService {

    private  FakeAlertsCreator fakeAlertsCreator;
    private FakeIndicatorCreators fakeIndicatorCreators;

    public FakeRemoteAlertClientService(FakeAlertsCreator fakeAlertsCreator, FakeIndicatorCreators fakeIndicatorCreators) {
        this.fakeAlertsCreator = fakeAlertsCreator;
        this.fakeIndicatorCreators = fakeIndicatorCreators;
    }

    @Override
    protected AlertsApi getControllerInstance(ApiClient delegatorApiClient) {
        return new FakeAlertsApi();
    }

    private class FakeAlertsApi extends AlertsApi{
        @Override
        public Alert getAlert(String alertId, Boolean expand) throws ApiException {
            return fakeAlertsCreator.getAlerts(1).getAlerts().get(0);
        }

        @Override
        public AlertsWrapper getAlerts(AlertQuery body) throws ApiException {
            int size = 10;
            if (body!=null && body.getPageSize()!=null){
                size = body.getPageSize().intValue();
            }
            AlertsWrapper alertsWrapper = fakeAlertsCreator.getAlerts(10);
            alertsWrapper.setAggregationData(fakeAlertsCreator.getAggregationDate());
            return alertsWrapper;

        }

        @Override
        public Indicator getIndicatorByAlert(String indicatorId, String alertId, Boolean expand) throws ApiException {
            Indicator indicator = fakeIndicatorCreators.getIndicators(1, "2018-01-01 13:00","2018-01-01 14:00").getIndicators().get(0);
            indicator.setId(indicatorId);

            CountAggregation historicalData = new CountAggregation();

            for (int i=0; i<5;i++) {
                CountBucket countBucket = new CountBucket();
                countBucket.setAnomaly(i==4);
                countBucket.setKey("Demo Value"+i);
                countBucket.setValue(5);
                historicalData.addBucketsItem(countBucket);
            }

            indicator.setHistoricalData(historicalData);
            return indicator;

        }

        @Override
        public EventsWrapper getIndicatorEventsByAlert(String indicatorId, String alertId, EventQuery body) throws ApiException {
            EventsWrapper ew = new EventsWrapper();
            Map<String, Object> event = new HashMap<>();
            event.put("userId","user1");
            event.put("eventDate/epochSeconds",2222222222L);
            event.put("user_sid","user1");
            event.put("origin","Origin");
            event.put("description","This is description");
            ew.addEventsItem(event);

            Map<String, Object> event2 = new HashMap<>();
            event2.put("userId","user1");
            event2.put("eventDate/epochSeconds",2222222222L);
            event2.put("user_sid","user1");
            event2.put("origin","Origin");
            event2.put("description","This is description");
            ew.addEventsItem(event2);
            ew.setPage(1);
            ew.setTotal(2);
            return ew;

        }

        @Override
        public IndicatorsWrapper getIndicatorsByAlert(String alertId, IndicatorQuery body) throws ApiException {
            return fakeIndicatorCreators.getIndicators(1, "2018-01-01 13:00","2018-01-01 14:00");
        }

        @Override
        public Alert updateAlert(List<Patch> body) throws ApiException {
            return fakeAlertsCreator.getAlerts(1).getAlerts().get(0);
        }

        @Override
        public void updateAlertsFeedback(UpdateFeedbackRequest body) throws ApiException {

        }
    }
}
