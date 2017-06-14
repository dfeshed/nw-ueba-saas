package presidio.collector.services.impl;

import com.opencsv.CSVReader;
import fortscale.common.general.DataSource;
import fortscale.utils.logging.Logger;
import presidio.collector.services.api.Fetcher;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class CsvFileFetcher implements Fetcher {

    private static final Logger logger = Logger.getLogger(CsvFileFetcher.class);

    private final String csvFilesFolderPath;
    private final Charset charset;
    private final char delimiter;

    public CsvFileFetcher(String csvFilesFolderPath, Charset charset, char delimiter) {
        this.csvFilesFolderPath = csvFilesFolderPath;
        this.charset = charset;
        this.delimiter = delimiter;
    }

    @Override
    public List<String[]> fetch(DataSource dataSource, Instant startTime, Instant endTime) throws Exception {
        final String csvFile = buildFileName(dataSource);
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), charset), delimiter));

        final List<String[]> records = new ArrayList<>();
        String[] line;
        boolean isDone = false;
        while (!isDone && (line = reader.readNext()) != null) {
            final Instant eventTime = Instant.parse(line[0]); //todo: ad-hoc. maybe we should pass the fetchers a map with parameters they need (like in this example, the date time unix fields index in each line). also format can come from config
            if (startTime.isBefore(eventTime)) {
                if (endTime.isAfter(eventTime)) {
                    records.add(line); //assumes file is sorted by date time unix
                } else {
                    isDone = true;
                }
            }
        }

        return filterFields(records);
    }

    private List<String[]> filterFields(List<String[]> records) {
        return records;
        //todo: do nothing for now, change this when relevant.
        //todo: i think a better way to do this is to have the "fetcher" and the "filter" uncoupled.
        //todo: so the fetcher will get the filter in the ctor.
        //todo: this way we can use the same CSVReader logic with different filters (i.e for dlpfile and dlpmail)
        //todo: also... - the name 'filter' can be better :-)
    }

    private String buildFileName(DataSource dataSource) {
        final String fileName = dataSource.name() + ".csv"; //todo: we should consider extracting to a service if someone else uses these files
        return Paths.get(csvFilesFolderPath, fileName).toString();
    }
}
