package fortscale.services.configuration.gds.state.gds.entities.properties;

/**
 * additional field is a field not inherited from any super-scheme.
 * As such, it's properties should be configured.
 * (the rest of the fields have already configured in one of their super-scheme)
 * Created by galiar on 20/01/2016.
 */
public class GDSEntitiesPropertiesAdditionalField {

	private String fieldId;
	private String fieldName;
	private String fieldType;
	private String rank;
	private String scoreField;
	private String lov;
	private String enableByDefault;

	//------optional fields - might be null -----//
	private String searchable;
	private	String valueList;

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}

	public String getFieldName() {
		return fieldName;
	}

	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}

	public String getFieldType() {
		return fieldType;
	}

	public void setFieldType(String fieldType) {
		this.fieldType = fieldType;
	}

	public String getRank() {
		return rank;
	}

	public void setRank(String rank) {
		this.rank = rank;
	}

	public String getScoreField() {
		return scoreField;
	}

	public void setScoreField(String scoreField) {
		this.scoreField = scoreField;
	}

	public String getLov() {
		return lov;
	}

	public void setLov(String lov) {
		this.lov = lov;
	}

	public String getEnableByDefault() {
		return enableByDefault;
	}

	public void setEnableByDefault(String enableByDefault) {
		this.enableByDefault = enableByDefault;
	}

	public String getSearchable() {
		return searchable;
	}

	public void setSearchable(String searchable) {
		this.searchable = searchable;
	}

	public String getValueList() {
		return valueList;
	}

	public void setValueList(String valueList) {
		this.valueList = valueList;
	}

}
