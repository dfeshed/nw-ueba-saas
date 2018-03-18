package fortscale.web.demoservices;

import fortscale.domain.core.EvidenceType;
import fortscale.domain.core.Severity;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;

/**
 * Created by shays on 23/07/2017.
 */
public abstract class DemoFactoryAbstract<T> {

    final static String USERNAME ="user";
    final static String DATE = "date";
    final static String DATE_PATTERN = "yyyy-MM-dd'T'hh:mm:ss'Z'";

    protected abstract T getRecord(CSVRecord csvRecord) throws Exception;

    protected long getEndTime(boolean isDaily, Date startTime, EvidenceType evidenceType) {

        if (EvidenceType.AnomalySingleEvent.equals(evidenceType)){
            return startTime.getTime();
        }
        Calendar c = Calendar.getInstance();
        c.setTime(startTime);
        c.add(isDaily? Calendar.DAY_OF_YEAR:Calendar.HOUR,1);
        return c.getTime().getTime();
    }

    protected List<T> getData(Function<CSVRecord, T> processRow, String fileName, String[] headers) throws Exception {

        List<T> records = new ArrayList<>();

        //Reader in = new FileReader("classpath*:META-INF/demo/demo-users.csv");
        InputStream is = getClass().getResourceAsStream("/META-INF/demo/"+fileName);
        Reader in = new InputStreamReader(is);
        CSVFormat.EXCEL.withHeader(headers).withSkipHeaderRecord().parse(in).forEach(
                (csvRecord) -> {
                    records.add(processRow.apply(csvRecord));
                }
        );

        return records;
    }

    protected List<T> getData(Function<CSVRecord, T> processRow, String fileName, String[] headers,
                              String username, Long startDate, Long endDate, String indicatorIds) throws Exception {

        List<T> records = new ArrayList<>();

        //Reader in = new FileReader("classpath*:META-INF/demo/demo-users.csv");
        InputStream is = getClass().getResourceAsStream("/META-INF/demo/"+fileName);
        Reader in = new InputStreamReader(is);
        CSVFormat.EXCEL.withHeader(headers).withSkipHeaderRecord().parse(in).forEach(
                (csvRecord) -> {
                    if (isEventMatched(csvRecord, username, startDate, endDate,indicatorIds)) {
                        records.add(processRow.apply(csvRecord));
                    }
                }
        );

        return records;
    }

    private boolean isEventMatched(CSVRecord csvRecord, String username, Long startDate, Long endDate, String indicatorId) {

        String indicatorIds = csvRecord.get("indicator_ids");
        if (StringUtils.isNotBlank(indicatorIds)){
            String[] indicatorsIdsArray = indicatorIds.split(",");
            Set<String> indicatorIdsSet = new HashSet(Arrays.asList(indicatorsIdsArray));
            return (indicatorIdsSet.contains(indicatorId));
        } else {
            if (!csvRecord.get(USERNAME).equals(username)) {
                return false;
            }

            SimpleDateFormat format = new SimpleDateFormat(DATE_PATTERN);
            format.setTimeZone(TimeZone.getTimeZone("GMT"));

            try {
                Date eventDate = format.parse(csvRecord.get(DATE));
                Date rangeStart = new Date(startDate);
                Date rangeEnd = new Date(endDate);
                if ((eventDate.compareTo(rangeStart) == -1) ||
                        (eventDate.compareTo(rangeEnd) == 1)) {
                    return false;
                }
            } catch (ParseException e) {
                return false;
            }
        }
        return true;
    }

    protected Date getStartDateFromString(String startTimeString) {
        DateFormat format = new SimpleDateFormat("MMM dd yyyy HH:mm:ss", Locale.ENGLISH);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        Date startTime = null;
        try {
            startTime = format.parse(startTimeString.substring(0,20));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return startTime;
    }

    protected Date getStartDateFromEventString(String startTimeString) {
        DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
        format2.setTimeZone(TimeZone.getTimeZone("GMT"));

        Date startTime = null;
        try {
            startTime = format2.parse(startTimeString.substring(0,19).replace("T"," "));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        return startTime;
    }



    protected int severityToScore(Severity s){
        switch (s) {
            case Critical: return 97;
            case High: return 90;
            case Medium: return 80;
        }

        return 55;
    }

    protected Severity scoreToSeverity(int score){
        if (score<=75){
            return Severity.Low;
        } else if (score<=85){
            return Severity.Medium;
        } else if (score<=95) {
            return Severity.High;
        }

        return Severity.Critical;
    }

}
