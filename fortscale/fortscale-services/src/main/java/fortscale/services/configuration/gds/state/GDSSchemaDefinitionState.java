package fortscale.services.configuration.gds.state;

import fortscale.services.configuration.gds.state.field.FieldMetadataDictionary;

/**
 * Schema definition state
 *
 * @author gils
 * 31/12/2015
 */
public class GDSSchemaDefinitionState implements GDSConfigurationState{
    private boolean hasSourceIp;
    private boolean hasTargetIp;
    private String dataTableFields;
    private String enrichTableFields;
    private String enrichDelimiter;
    private String enrichTableName;
    private String scoreTableFields;
    private String scoreDelimiter;
    private String scoreTableName;
    private boolean topSchema;
    private String normalizedUserNameField;
    private String dataDelimiter;
    private String dataTableName;
    private FieldMetadataDictionary fieldMetadataDictionary;

    public GDSSchemaDefinitionState() {
        fieldMetadataDictionary = new FieldMetadataDictionary();
    }

    public boolean hasSourceIp() {
        return hasSourceIp;
    }

    public void setHasSourceIp(boolean sourceIp) {
        this.hasSourceIp = sourceIp;
    }

    public boolean hasTargetIp() {
        return hasTargetIp;
    }

    public void setHasTargetIp(boolean targetIp) {
        this.hasTargetIp = targetIp;
    }

    public String getDataTableFields() {
        return dataTableFields;
    }

    public void setDataTableFields(String dataTableFields) {
        this.dataTableFields = dataTableFields;
    }

    public String getEnrichTableFields() {
        return enrichTableFields;
    }

    public void setEnrichTableFields(String enrichTableFields) {
        this.enrichTableFields = enrichTableFields;
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

    public String getScoreTableFields() {
        return scoreTableFields;
    }

    public void setScoreTableFields(String scoreTableFields) {
        this.scoreTableFields = scoreTableFields;
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

    public void setHasTopSchema(boolean topSchema) {
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

    public FieldMetadataDictionary getFieldMetadataDictionary() {
        return fieldMetadataDictionary;
    }

    @Override
    public void reset() {
        hasSourceIp = false;
        hasTargetIp = false;
        dataTableFields = null;
        enrichTableFields = null;
        enrichDelimiter = null;
        enrichTableName = null;
        scoreTableFields = null;
        scoreDelimiter = null;
        scoreTableName = null;
        topSchema = false;
        normalizedUserNameField = null;
        dataDelimiter = null;
        dataTableName = null;

        fieldMetadataDictionary.reset();
    }
}
