package fortscale.web.demoservices;

import fortscale.domain.core.*;
import org.apache.commons.csv.CSVRecord;

import java.util.*;


/**
 * Created by shays on 23/07/2017.
 */
public class DemoAlertFactory extends DemoFactoryAbstract<Alert>{
    private static final String [] FILE_HEADER_MAPPING = {"Alert Name","User Name","Start Time","# of Indicators","Severity","Daily/Hourly"};
    protected String getFileName(){
        return "demo_alerts.csv";
    }

    protected String[] headers(){
        return  FILE_HEADER_MAPPING;
    }
    protected Alert getRecord(CSVRecord csvRecord) throws RuntimeException {
        String username = csvRecord.get("User Name");
        String alertName = csvRecord.get("Alert Name");
        String startTimeString = csvRecord.get("Start Time");
        int totalIndicators= Integer.parseInt(csvRecord.get("# of Indicators"));
        Severity severity = Severity.valueOf(csvRecord.get("Severity"));
        String timeframe = csvRecord.get("Daily/Hourly");

        Alert a = new Alert();
        a.setSeverity(severity);
        a.setName(alertName);
        a.setEntityName(username);
        a.setEvidenceSize(totalIndicators);
        a.setTimeframe(AlertTimeframe.valueOf(timeframe));
        a.setEntityType(EntityType.User);
        a.setStatus(AlertStatus.Open);
        a.setFeedback(AlertFeedback.None);
        a.setMockId(UUID.randomUUID().toString());
        a.setUserScoreContribution(getContributionFromSeverity(severity));
        a.setUserScoreContributionFlag(true);

        switch (a.getSeverity()) {
            case Critical: a.setScore(99); break;
            case High: a.setScore(90); break;
            case Medium: a.setScore(80); break;
            case Low: a.setScore(51); break;

        }

        switch (a.getSeverity()) {
            case Critical: a.setSeverityCode(0); break;
            case High: a.setSeverityCode(1); break;
            case Medium: a.setSeverityCode(2); break;
            case Low: a.setSeverityCode(3); break;

        }

        boolean isDaily = a.getTimeframe().equals(AlertTimeframe.Daily);
        Date startTime = getStartDateFromString(startTimeString);
        a.setStartDate(startTime.getTime());

        long endTime = getEndTime(isDaily, startTime,EvidenceType.AnomalyAggregatedEvent);
        a.setEndDate(endTime);
        return a;
    }



    private double getContributionFromSeverity(Severity severity){
        switch(severity){
            case Critical:return 20;
            case High: return 15;
            case Medium: return 10;
            case Low: return 5;
            default: return 0;
        }
    }

    public List getAlerts() throws Exception {
        return getData(csvRecord -> this.getRecord(csvRecord),getFileName(),headers());
    }


}
