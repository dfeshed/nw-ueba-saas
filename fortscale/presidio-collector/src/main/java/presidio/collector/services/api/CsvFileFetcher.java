package presidio.collector.services.api;

import com.opencsv.CSVReader;
import fortscale.utils.logging.Logger;
import presidio.collector.Datasource;
import presidio.sdk.api.domain.AbstractRecordDocument;
import presidio.sdk.api.domain.DlpFileRecordDocumentBuilder;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Paths;
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
    public List<AbstractRecordDocument> fetch(Datasource datasource, long startTime, long endTime) throws Exception {
        final String csvFile = createFileName(datasource, startTime, endTime);
        CSVReader reader = new CSVReader(new BufferedReader(new InputStreamReader(new FileInputStream(csvFile), charset), delimiter));
        final List<String[]> records = reader.readAll();

        List<String[]> filteredRecords = filterFields(records);

        return createDocuments(datasource, filteredRecords);
    }

    private List<String[]> filterFields(List<String[]> records) {
        return records;
        //todo: do nothing for now, change this when relevant.
        //todo: i think a better way to do this is to have the "fetcher" and the "filter" uncoupled.
        //todo: so the fetcher will get the filter in the ctor.
        //todo: this way we can use the same CSVReader logic with different filters (i.e for dlpfile and dlpmail)
        //todo: also... - the name 'filter' can be better :-)
    }

    private List<AbstractRecordDocument> createDocuments(Datasource datasource, List<String[]> records) throws Exception {
        List<AbstractRecordDocument> createdDocuments = new ArrayList<>();
        switch (datasource) { //todo: we can use a document factory instead of switch case
            case DLPFILE: {
                DlpFileRecordDocumentBuilder dlpFileRecordDocumentBuilder = new DlpFileRecordDocumentBuilder();
                for (String[] record : records) {
                    createdDocuments.add(dlpFileRecordDocumentBuilder.createDlpFileRecordDocument(record));
                }
                break;
            }
            case DLPMAIL: {
                throw new UnsupportedOperationException("DLPMAIL not supported yet");
            }
            case PRNLOG: {
                throw new UnsupportedOperationException("PRNLOG not supported yet");
            }
            default: {
                //should not happen
                throw new Exception("create documents failed. this is weird - should not happen. datasource=" + datasource.name());
            }
        }

        return createdDocuments;

    }

    private String createFileName(Datasource datasource, long startTime, long endTime) {
        final String fileName = datasource.name() + "_" + startTime + "_" + endTime + ".csv"; //todo: we should consider extracting to a service if someone else uses these files
        return Paths.get(csvFilesFolderPath, fileName).toString();
    }
}
