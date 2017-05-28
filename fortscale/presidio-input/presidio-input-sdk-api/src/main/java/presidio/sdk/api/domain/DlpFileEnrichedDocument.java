package presidio.sdk.api.domain;


import org.apache.commons.lang3.SerializationUtils;

public class DlpFileEnrichedDocument extends DlpFileDataDocument {

    private final String normalizedUsername;
    private final String normalizedMachineName;


    public DlpFileEnrichedDocument(DlpFileDataDocument record, String normalizedUsername, String normalizedMachineName) {
        super();
        SerializationUtils.clone(record);
        this.normalizedUsername = normalizedUsername;
        this.normalizedMachineName = normalizedMachineName;
    }
}
