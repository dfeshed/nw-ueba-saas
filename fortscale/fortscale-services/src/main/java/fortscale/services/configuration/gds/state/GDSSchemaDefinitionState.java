package fortscale.services.configuration.gds.state;

/**
 * Schema definition state
 *
 * @author gils
 * 31/12/2015
 */
public class GDSSchemaDefinitionState implements GDSConfigurationState{
    private boolean sourceIp;
    private boolean targetIp;
    private String dataFields;
    private String enrichFields;
    private String enrichDelimiter;
    private String enrichTableName;
    private String scoreFields;
    private String scoreDelimiter;
    private String scoreTableName;
    private boolean topSchema;
    private String normalizedUserNameField;
    private String dataDelimiter;
    private String dataTableName;

    public boolean hasSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(boolean sourceIp) {
        this.sourceIp = sourceIp;
    }

    public boolean hasTargetIp() {
        return targetIp;
    }

    public void setTargetIp(boolean targetIp) {
        this.targetIp = targetIp;
    }

    public String getDataFields() {
        return dataFields;
    }

    public void setDataFields(String dataFields) {
        this.dataFields = dataFields;
    }

    public String getEnrichFields() {
        return enrichFields;
    }

    public void setEnrichFields(String enrichFields) {
        this.enrichFields = enrichFields;
    }

    public String getEnrichDelimiter() {
        return enrichDelimiter;
    }

    public void setEnrichDelimiter(String enrichDelimiter) {
        this.enrichDelimiter = enrichDelimiter;
    }

    public String getEnrichTableName() {
        return enrichTableName;
    }

    public void setEnrichTableName(String enrichTableName) {
        this.enrichTableName = enrichTableName;
    }

    public String getScoreFields() {
        return scoreFields;
    }

    public void setScoreFields(String scoreFields) {
        this.scoreFields = scoreFields;
    }

    public String getScoreDelimiter() {
        return scoreDelimiter;
    }

    public void setScoreDelimiter(String scoreDelimiter) {
        this.scoreDelimiter = scoreDelimiter;
    }

    public String getScoreTableName() {
        return scoreTableName;
    }

    public void setScoreTableName(String scoreTableName) {
        this.scoreTableName = scoreTableName;
    }

    public boolean hasTopSchema() {
        return topSchema;
    }

    public void setTopSchema(boolean topSchema) {
        this.topSchema = topSchema;
    }

    public String getNormalizedUserNameField() {
        return normalizedUserNameField;
    }

    public void setNormalizedUserNameField(String normalizedUserNameField) {
        this.normalizedUserNameField = normalizedUserNameField;
    }

    public String getDataDelimiter() {
        return dataDelimiter;
    }

    public void setDataDelimiter(String dataDelimiter) {
        this.dataDelimiter = dataDelimiter;
    }

    public String getDataTableName() {
        return dataTableName;
    }

    public void setDataTableName(String dataTableName) {
        this.dataTableName = dataTableName;
    }

    @Override
    public void reset() {
        sourceIp = false;
        targetIp = false;
        dataFields = null;
        enrichFields = null;
        enrichDelimiter = null;
        enrichTableName = null;
        scoreFields = null;
        scoreDelimiter = null;
        scoreTableName = null;
        topSchema = false;
        normalizedUserNameField = null;
        dataDelimiter = null;
        dataTableName = null;
    }
}
