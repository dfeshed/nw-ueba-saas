package fortscale.web.demoservices;

import org.apache.commons.csv.CSVRecord;

import java.text.DateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * Created by shays on 23/07/2017.
 */
public class DemoEventsFactory extends DemoFactoryAbstract<Map<String, Object>>{

    private static final String [] FILE_HEADER_MAPPING = {"date","time","user_sid","user","origin_ipv4","from","to","description","comment","server_dn","server_fqdn","computer","server_ou","os_version","domain_dn","domain_fqdn","domain","folder_path","file_name","event","result","indicator_ids"};
    private static final String [] LOGIN_HEADER_MAPPING = {"date","time_detected","user_sid","user","origin_ipv4","origin_ipv6","description","server_dn","computer","os_version","domain","event","action","subsystem","result","indicator_ids"};
    private static final String [] AD_HEADER_MAPPING = {"date","time_detected","user_sid","user","origin_ipv4","origin_ipv6","description","server_dn","computer","os_version","domain","event","action","subsystem","result","indicator_ids"};

    private String getFileEventsFileName(){
        return "demo_file_events.csv";
    }
    private String getLoginEventsFileName(){
        return "demo_logon_events.csv";
    }
    private String getADEventsFileName(){
        return "demo_ad_events.csv";
    }

    protected String[] headers(){
        return  FILE_HEADER_MAPPING;
    }

    public List<Map<String, Object>> getLogonEvents(String username, Long startDate, Long endDate, String indicatorIds) throws Exception {
        return getData(csvRecord -> this.getRecord(csvRecord), getLoginEventsFileName(), LOGIN_HEADER_MAPPING,
                username ,startDate ,endDate ,indicatorIds);
    }

    public List<Map<String, Object>> getFileEvents(String username, Long startDate, Long endDate, String indicatorIds) throws Exception {
        return getData(csvRecord -> this.getRecord(csvRecord), getFileEventsFileName(), FILE_HEADER_MAPPING,
                username ,startDate ,endDate,indicatorIds );
    }

    public List<Map<String, Object>> getActiveDirectoryEvents(String username, Long startDate, Long endDate, String indicatorIds) throws Exception {
        return getData(csvRecord -> this.getRecord(csvRecord), getADEventsFileName(), AD_HEADER_MAPPING,
                username ,startDate ,endDate,indicatorIds );
    }


    protected Map<String, Object> getRecord(CSVRecord csvRecord) throws RuntimeException {
        Map<String, Object> returnedMap = new HashMap<>();
        returnedMap.putAll(csvRecord.toMap());
        String dateAsString = (String)returnedMap.get("date");
        Date d = getStartDateFromEventString(dateAsString);
        if (d!=null) {
            returnedMap.put("date", Long.valueOf(d.getTime()));
        }



        return returnedMap;
    }


}
