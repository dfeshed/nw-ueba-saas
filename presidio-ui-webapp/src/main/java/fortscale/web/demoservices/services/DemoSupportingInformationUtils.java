package fortscale.web.demoservices.services;


import fortscale.domain.core.EvidenceTimeframe;
import fortscale.web.rest.entities.SupportingInformationEntry;

import java.util.*;

/**
 * Created by shays on 02/08/2017.
 */
public class DemoSupportingInformationUtils {

    public static List<SupportingInformationEntry> getSupportingInformationForAggregatedIndicators(long startTime, String anomalyValue, EvidenceTimeframe timeframe, String[] historicalData) {
        List<SupportingInformationEntry> supportingInformation = new ArrayList<>();

        int numberOfHistoricHoursOrDays=historicalData.length;

        Calendar c = getCalendarXTimeUnitsBefore(timeframe, startTime,numberOfHistoricHoursOrDays);

        for (int i=0;i<numberOfHistoricHoursOrDays;i++){
            SupportingInformationEntry entry = getSupportingInformationEntry(historicalData[i], c,false);
            supportingInformation.add(entry);
            c.add(getTimeunitFromTimeFrame(timeframe),1);

        }

        SupportingInformationEntry entry  = getSupportingInformationEntry(anomalyValue, c,true);
        supportingInformation.add(entry);
        return supportingInformation;
    }

    public static List<SupportingInformationEntry> getSupportingInformationForPieAndSingleBar(String[] historicalData, String anomalyValue) {
        List<SupportingInformationEntry> supportingInformation = new ArrayList<>();

        for (int i=0;i<historicalData.length;i++){
            String count = historicalData[i].split("=")[0];
            String label = historicalData[i].split("=")[1].replaceAll("\"","");
            SupportingInformationEntry entry = getSupportingInformationEntry(count, label,label.equals(anomalyValue));
                    supportingInformation.add(entry);

        }

        return supportingInformation;
    }

    public static List<SupportingInformationEntry> getSupportingInformationForTime(long evidenceTime) {
        List<SupportingInformationEntry> supportingInformation = new ArrayList<>();

        String[] daysOfWeek=new String[]{"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};

        putRange(supportingInformation,daysOfWeek[1],10, 18);
        putRange(supportingInformation,daysOfWeek[2],10, 15);
        putRange(supportingInformation,daysOfWeek[3],11, 18);
        putRange(supportingInformation,daysOfWeek[4],12, 20);
        putRange(supportingInformation,daysOfWeek[5],13, 18);


        Calendar c = Calendar.getInstance();
        c.setTime(new Date(evidenceTime));
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK); //Start with 1
        String day=daysOfWeek[dayOfWeek-1];

        String hour = c.get(Calendar.HOUR)+"";
        supportingInformation.add(getSupportingInformationEntry("1", hour, day, true));
        return supportingInformation;

    }

    private static void putRange(List<SupportingInformationEntry> supportingInformation, String dayOfWeek,int from, int to){
        if (to<from){
            return;
        }
        for (int i=from; i<=to;i++) {

            supportingInformation.add(getSupportingInformationEntry("3", i+"", dayOfWeek, false));
        }
    }

    private static SupportingInformationEntry getSupportingInformationEntry(String value, Calendar c,boolean isAnomaly) {
        SupportingInformationEntry entry = new SupportingInformationEntry();
        long keyTime = c.getTime().getTime();
        entry.setKeys(Arrays.asList(Long.toString(keyTime)));
        entry.setValue(value);
        entry.setIsAnomaly(isAnomaly);
        return entry;
    }

    private static SupportingInformationEntry getSupportingInformationEntry(String value, String key,boolean isAnomaly) {
        SupportingInformationEntry entry = new SupportingInformationEntry();

        entry.setKeys(Arrays.asList(key));
        entry.setValue(value);
        entry.setIsAnomaly(isAnomaly);
        return entry;
    }

    private static SupportingInformationEntry getSupportingInformationEntry(String value, String hour, String weekDay,boolean isAnomaly) {
        SupportingInformationEntry entry = new SupportingInformationEntry();

        entry.setKeys(Arrays.asList(weekDay,hour));
        entry.setValue(value);
        entry.setIsAnomaly(isAnomaly);
        return entry;
    }

    private static Calendar getCalendarXTimeUnitsBefore(EvidenceTimeframe timeframe, long startTime, int timeUnits) {
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(startTime));

        c.add(getTimeunitFromTimeFrame(timeframe),-1*timeUnits);
        return c;
    }

    private static int getTimeunitFromTimeFrame(EvidenceTimeframe timeframe) {

        return EvidenceTimeframe.Daily.equals(timeframe)? Calendar.DATE:Calendar.HOUR;
    }
}
