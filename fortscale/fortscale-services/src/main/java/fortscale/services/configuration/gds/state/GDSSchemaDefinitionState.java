package fortscale.services.configuration.gds.state;

/**
 * Schema definition state
 *
 * @author gils
 * 31/12/2015
 */
public class GDSSchemaDefinitionState implements GDSConfigurationState{
    private boolean hasSourceIp;
    private boolean hasTargetIp;
    private String dataFields;
    private String enrichFields;
    private String enrichDelimiter;
    private String enrichTableName;
    private String scoreFields;
    private String scoreDelimiter;
    private String scoreTableName;
    private boolean hasTopSchema;
    private boolean hasNormalizedUserNameField;
    private String dataDelimiter;
    private String dataTableName;

    public boolean isHasSourceIp() {
        return hasSourceIp;
    }

    public void setHasSourceIp(boolean hasSourceIp) {
        this.hasSourceIp = hasSourceIp;
    }

    public boolean isHasTargetIp() {
        return hasTargetIp;
    }

    public void setHasTargetIp(boolean hasTargetIp) {
        this.hasTargetIp = hasTargetIp;
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

    public boolean isHasTopSchema() {
        return hasTopSchema;
    }

    public void setHasTopSchema(boolean hasTopSchema) {
        this.hasTopSchema = hasTopSchema;
    }

    public boolean isHasNormalizedUserNameField() {
        return hasNormalizedUserNameField;
    }

    public void setHasNormalizedUserNameField(boolean hasNormalizedUserNameField) {
        this.hasNormalizedUserNameField = hasNormalizedUserNameField;
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
        hasSourceIp = false;
        hasTargetIp = false;
        dataFields = null;
        enrichFields = null;
        enrichDelimiter = null;
        enrichTableName = null;
        scoreFields = null;
        scoreDelimiter = null;
        scoreTableName = null;
        hasTopSchema = false;
        hasNormalizedUserNameField = false;
        dataDelimiter = null;
        dataTableName = null;
    }
}
