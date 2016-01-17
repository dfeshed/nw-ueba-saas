package fortscale.services.configuration.gds.state;

import fortscale.services.configuration.gds.state.field.FieldMetadataContainer;

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
    private boolean topSchema;
    private String normalizedUserNameField;
    private String dataDelimiter;
    private String dataTableName;
	private String scoreFieldsCSV;
	private String additionalScoreFieldsCSV;
	private String additionalFieldsCSV;
	private String additionalFiledToScoreFieldMapCSV;
    private FieldMetadataContainer fieldMetadataContainer;

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

	public String getScoreFieldsCSV() {
		return scoreFieldsCSV;
	}

	public void setScoreFieldsCSV(String scoreFieldsCSV) {
		this.scoreFieldsCSV = scoreFieldsCSV;
	}

	public String getAdditionalScoreFieldsCSV() {
		return additionalScoreFieldsCSV;
	}

	public void setAdditionalScoreFieldsCSV(String additionalScoreFieldsCSV) {
		this.additionalScoreFieldsCSV = additionalScoreFieldsCSV;
	}

	public String getAdditionalFieldsCSV() {
		return additionalFieldsCSV;
	}

	public void setAdditionalFieldsCSV(String additionalFieldsCSV) {
		this.additionalFieldsCSV = additionalFieldsCSV;
	}

	public String getAdditionalFiledToScoreFieldMapCSV() {
		return additionalFiledToScoreFieldMapCSV;
	}

	public void setAdditionalFiledToScoreFieldMapCSV(String additionalFiledToScoreFieldMapCSV) {
		this.additionalFiledToScoreFieldMapCSV = additionalFiledToScoreFieldMapCSV;
	}

    public FieldMetadataContainer getFieldMetadataContainer() {
        return fieldMetadataContainer;
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
        topSchema = false;
        normalizedUserNameField = null;
        dataDelimiter = null;
        dataTableName = null;
		additionalFieldsCSV = null;
		additionalScoreFieldsCSV = null;
		additionalFiledToScoreFieldMapCSV = null;

        fieldMetadataContainer.reset();
    }
}
