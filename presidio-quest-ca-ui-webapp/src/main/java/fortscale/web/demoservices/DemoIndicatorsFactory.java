package fortscale.web.demoservices;

import fortscale.domain.core.*;
import fortscale.web.demoservices.services.DemoSupportingInformationUtils;
import fortscale.web.rest.entities.SupportingInformationEntry;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

import java.util.*;


/**
 * Created by shays on 23/07/2017.
 */
public class DemoIndicatorsFactory extends DemoFactoryAbstract<Evidence>{
    private static final String [] FILE_HEADER_MAPPING = {"user name","alert name","indicator name","indicator type", "score","value","hourly / daily","start time","chartType","description","historgram last 30 days","pie last 30 days","time","dataSource","id"};

    static private Set<IndicatorDetails> indicatorsMetaData;
    static {
        indicatorsMetaData = new HashSet<>();
        indicatorsMetaData.add(new IndicatorDetails("multiple_failed_authentications","Multiple Failed Authentications",EvidenceType.AnomalyAggregatedEvent));
        indicatorsMetaData.add(new IndicatorDetails("logged_onto_multiple_domains","Logged onto Multiple Domains",EvidenceType.AnomalyAggregatedEvent));
        indicatorsMetaData.add(new IndicatorDetails("abnormal_computer_accessed_remotely","Abnormal Computer Accessed Remotely",EvidenceType.AnomalySingleEvent));
        indicatorsMetaData.add(new IndicatorDetails("account_management_change_anomaly","Account Management Change Anomaly",EvidenceType.AnomalySingleEvent));
        indicatorsMetaData.add(new IndicatorDetails("multiple_account_management_changes","Multiple Account Management Changes",EvidenceType.AnomalyAggregatedEvent));
        indicatorsMetaData.add(new IndicatorDetails("multiple_privileged_group_membership_changes","Multiple privileged Group Membership Changes",EvidenceType.AnomalyAggregatedEvent));
        indicatorsMetaData.add(new IndicatorDetails("event_time","Abnormal Active Directory Change Time",EvidenceType.AnomalySingleEvent));
        indicatorsMetaData.add(new IndicatorDetails("event_time","Abnormal Logon Time",EvidenceType.AnomalySingleEvent));

        indicatorsMetaData.add(new IndicatorDetails("multiple_failed_file_access_events","Multiple Failed File Access Events",EvidenceType.AnomalyAggregatedEvent));
        indicatorsMetaData.add(new IndicatorDetails("multiple_folder_open_events","Multiple Folder Open Events",EvidenceType.AnomalyAggregatedEvent));
        indicatorsMetaData.add(new IndicatorDetails("multiple_file_delete_events","Multiple File Delete Events",EvidenceType.AnomalyAggregatedEvent));

    }

    private IndicatorDetails defaultIndicator = new IndicatorDetails("multiple_privileged_group_membership_changes","Multiple privileged Group Membership Changes",EvidenceType.AnomalyAggregatedEvent);

    public Map<String, List<SupportingInformationEntry>> suppotingInformationForIndicatorId=new HashMap<>();


    protected String getFileName(){
        return "demo_indicators.csv";
    }

    protected String[] headers(){
        return  FILE_HEADER_MAPPING;
    }
    protected Evidence getRecord(CSVRecord csvRecord){
        Evidence e = new Evidence();
//        try {
        e = new Evidence();
        e.setMockId(csvRecord.get("id"));
        e.setName(csvRecord.get("indicator name"));
        e.setAnomalyTypeFieldName(getAnomalyTypeFromIndicatorName(csvRecord.get("indicator name")));
        e.setAnomalyType(csvRecord.get("indicator name"));
        e.setAnomalyValue(csvRecord.get("value"));
        e.setTimeframe(EvidenceTimeframe.valueOf(csvRecord.get("hourly / daily")));
        e.setEntityType(EntityType.User);
        e.setEntityName(csvRecord.get("user name"));

        boolean isDaily = e.getTimeframe().equals(EvidenceTimeframe.Daily);
        Date startTime = getStartDateFromString(csvRecord.get("start time"));
        e.setStartDate(startTime.getTime());

        long endTime = getEndTime(isDaily, startTime,e.getEvidenceType());
        e.setEndDate(endTime);

        e.setScore(Integer.parseInt(csvRecord.get("score")));

        e.setEvidenceType(EvidenceType.valueOf(csvRecord.get("indicator type")));
        switch (e.getEvidenceType()) {
            case AnomalySingleEvent:
                e.setNumOfEvents(1);
                break;
            case AnomalyAggregatedEvent:
                e.setNumOfEvents(Integer.valueOf(csvRecord.get("value")));
                break;
            default:
                break;
        }


        e.setSeverity(scoreToSeverity(e.getScore()));
        e.setDataEntitiesIds(Arrays.asList(csvRecord.get("dataSource")));
        generateSupportingInformation(e, csvRecord);
        return e;
//        } catch (Exception e1){
//            System.out.print("exception when creating indicator:");
//            System.out.print(e1.toString());
//            return null;
//        }

    }

    private void generateSupportingInformation(Evidence e, CSVRecord csvRecord){
        if (EvidenceType.AnomalyAggregatedEvent.equals(e.getEvidenceType())){
            String[] historicalData = csvRecord.get("historgram last 30 days").split(",");

            List<SupportingInformationEntry> supportingInformation = DemoSupportingInformationUtils.getSupportingInformationForAggregatedIndicators
                                                                            (e.getStartDate(),e.getAnomalyValue(), e.getTimeframe(), historicalData);
            suppotingInformationForIndicatorId.put(e.getId(),supportingInformation);
        } else if(csvRecord.get("chartType").equals("Time")){
            List<SupportingInformationEntry> supportingInformation = DemoSupportingInformationUtils.getSupportingInformationForTime(e.getStartDate());
            suppotingInformationForIndicatorId.put(e.getId(),supportingInformation);
        } else {
            String rawData=csvRecord.get("pie last 30 days");
            if (StringUtils.isNotBlank(rawData)) {
                String[] historicalData = csvRecord.get("pie last 30 days").split(",");

                List<SupportingInformationEntry> supportingInformation = DemoSupportingInformationUtils.getSupportingInformationForPieAndSingleBar(historicalData,e.getAnomalyValue());
                suppotingInformationForIndicatorId.put(e.getId(), supportingInformation);
            }
        }




    }



    public String getAnomalyTypeFromIndicatorName(String prettyname) {
        return getIndicatorDetailsByPrettName(prettyname).name;
    }

    public EvidenceType getEvidenceTypeFromIndicatorName(String prettyname) {
        return getIndicatorDetailsByPrettName(prettyname).evidenceType;

    }
    private IndicatorDetails getIndicatorDetailsByPrettName(String prettyname){
        return indicatorsMetaData
                .stream()
                .filter(indicatorDetails -> indicatorDetails.prettyName.equals(prettyname))
                .findAny().
                        orElse(defaultIndicator);
    }

    static class IndicatorDetails{
        public String name;
        public String prettyName;
        public EvidenceType evidenceType;

        public IndicatorDetails(String name, String prettyName, EvidenceType evidenceType) {
            this.name = name;
            this.prettyName = prettyName;
            this.evidenceType = evidenceType;
        }
    }

    public List<Evidence> getEvidences() throws Exception {
        return getData(csvRecord -> this.getRecord(csvRecord),getFileName(),headers());
    }
//    public List<User> getList() throws IOException {
//        List<User> users = new ArrayList<>();
//        getRawRecorsIterator("demo-users.csv", FILE_HEADER_MAPPING).forEach((record)->{
//            String username = record.get("Username");
//            int riskScore = Integer.parseInt(record.get("Risk Score"));
//            int totalAlert= Integer.parseInt(record.get("Total Alerts"));
//
//            User user = new User();
//            user.setAlertsCount(totalAlert);
//            user.setScore(riskScore);
//            user.setUsername(username);
//            user.setMockId(username);
//            users.add(user);
//        });
//
//
//        return users;
//    }
}
