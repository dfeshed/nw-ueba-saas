package fortscale.presidio.output.client.mock.creators;


import presidio.output.client.model.Alert;

import presidio.output.client.model.AlertQuery;
import presidio.output.client.model.AlertsWrapper;
import presidio.output.client.model.Indicator;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

public class FakeAlertsCreator {

    private FakeCreatorUtils fakeCreatorUtils;
    private FakeIndicatorCreators fakeIndicatorCreators;

    public FakeAlertsCreator(FakeCreatorUtils fakeCreatorUtils, FakeIndicatorCreators fakeIndicatorCreators) {
        this.fakeCreatorUtils = fakeCreatorUtils;
        this.fakeIndicatorCreators = fakeIndicatorCreators;
    }

    public Alert getAlert(String alertId, String classification,
                          String startTime,
                          String endTime,
                          Integer score, Alert.SeverityEnum severity, Boolean expand, int numberOfIndicators)  {
        Alert a = new Alert();
        a.setId(alertId);
        a.setFeedback(Alert.FeedbackEnum.NONE);

        a.setScore(score);

        a.setClassifiation(Arrays.asList(classification));
        a.setSeverity(severity);
        a.startDate(fakeCreatorUtils.timeStringToEpochBig(startTime));
        a.endDate(fakeCreatorUtils.timeStringToEpochBig(endTime));
        a.setIndicatorsNum(numberOfIndicators);
        a.setUserId("userId1");
        a.setUsername("User Id 1");
        a.setUserScoreContribution(new BigDecimal(20));

        //Set the indicators
        List<Indicator> indicators = fakeIndicatorCreators.getIndicators(10,startTime,endTime).getIndicators();
        if (expand) {
            a.setIndicators(indicators);
        }

        //Set the indicator names
        List<String> indicatorNames = indicators.stream().map(Indicator::getName).collect(Collectors.toList());
        a.setIndicatorsName(indicatorNames);





        return a;
    }

    public AlertsWrapper getAlerts(int amount) {
        List<Alert> alertsList=new ArrayList<>();
        for (int i=0; i<amount;i++){
            alertsList.add(getAlert("id"+i,"Brute Force","2018-01-01 13:00","2018-01-01 14:00",
                    50,Alert.SeverityEnum.CRITICAL,true, 3));
        }

        AlertsWrapper alertsWrapper = new AlertsWrapper();
        alertsWrapper.alerts(alertsList);
        alertsWrapper.setTotal(amount);
        return alertsWrapper;

    }


    public Map<String,Map<String,Long>> getAggregationDate() {
        Map<String,Long> feedback = new HashMap<>();
        feedback.put(Alert.FeedbackEnum.NONE.name(),100L);
        feedback.put(Alert.FeedbackEnum.NOT_RISK.name(),20L);
        feedback.put(Alert.FeedbackEnum.RISK.name(),20L);

        Map<String,Long> severity = new HashMap<>();
        severity.put(Alert.SeverityEnum.CRITICAL.name(),100L);
        severity.put(Alert.SeverityEnum.HIGH.name(),20L);
        severity.put(Alert.SeverityEnum.MEDIUM.name(),20L);
        severity.put(Alert.SeverityEnum.LOW.name(),20L);

        Map<String,Long> severityDaily = new HashMap<>();
        final int NUMBER_OF_DAYS = 60;

        LocalDate today = LocalDate.now();
        final  ZoneId zoneId = ZoneId.systemDefault();


        for (long i=0; i< NUMBER_OF_DAYS; i++) {
            LocalDate day = today.minusDays(i+1);

            long epoch = day.atStartOfDay(zoneId).toEpochSecond() *1000;

            severityDaily.put(epoch+":" + Alert.SeverityEnum.CRITICAL.name(), 15L);
            severityDaily.put(epoch+":" + Alert.SeverityEnum.HIGH.name(), 30L);
            severityDaily.put(epoch+":" + Alert.SeverityEnum.MEDIUM.name(), 60L);
            severityDaily.put(epoch+":" + Alert.SeverityEnum.LOW.name(), 100L);
        }

        Map<String,Long> classification = new HashMap<>();
        classification.put("Alert 1",100L);
        classification.put("Alert 3",10L);
        classification.put("Alert 2",1L);
        classification.put("Alert 4",50L);

        Map<String,Long> indicators = new HashMap<>();
        indicators.put("Indicator 1",100L);
        indicators.put("Indicator 3",10L);
        indicators.put("Indicator 2",1L);
        indicators.put("Indicator 4",50L);






        Map<String,Map<String,Long>> aggregatedData = new HashMap<>();
        aggregatedData.put(AlertQuery.AggregateByEnum.FEEDBACK.name(),feedback);
        aggregatedData.put(AlertQuery.AggregateByEnum.SEVERITY.name(),severity);
        aggregatedData.put(AlertQuery.AggregateByEnum.SEVERITY_DAILY.name(),severityDaily);
        aggregatedData.put(AlertQuery.AggregateByEnum.CLASSIFICATIONS.name(),classification);
        aggregatedData.put(AlertQuery.AggregateByEnum.INDICATOR_NAMES.name(),indicators);

        return aggregatedData;

    }
}
