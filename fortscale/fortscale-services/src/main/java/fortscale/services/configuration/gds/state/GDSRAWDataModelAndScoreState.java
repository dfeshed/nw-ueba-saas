package fortscale.services.configuration.gds.state;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by idanp on 1/11/2016.
 */
public class GDSRAWDataModelAndScoreState extends GDSStreamingTaskState {

	private Map<String, String> scoresFieldMap = new LinkedHashMap<String, String>();
	private Map<String, String> additionalScoreFeldsMap = new LinkedHashMap<String, String>();
	private Map<String, String> additionalFieldsMap = new LinkedHashMap<String, String>();
	private Map<String, String> additionalFiledToScoreFieldMap = new LinkedHashMap<String, String>();

	private Boolean sourceMachienFlag;
	private Boolean destMachienFlag;
	private Boolean countryToScoreFlag;
	private Boolean actionTypeToScoreFlag;
	private Boolean dateTimeToScoreFlag;
	private String  dataSourcesConfigurationKey;


	public Map<String, String> getScoresFieldMap() {
		return scoresFieldMap;
	}

	public void setScoresFieldMap(Map<String, String> scoresFieldMap) {
		this.scoresFieldMap = scoresFieldMap;
	}

	public Map<String, String> getAdditionalScoreFeldsMap() {
		return additionalScoreFeldsMap;
	}

	public void setAdditionalScoreFeldsMap(Map<String, String> additionalScoreFeldsMap) {
		this.additionalScoreFeldsMap = additionalScoreFeldsMap;
	}

	public Map<String, String> getAdditionalFieldsMap() {
		return additionalFieldsMap;
	}

	public void setAdditionalFieldsMap(Map<String, String> additionalFieldsMap) {
		this.additionalFieldsMap = additionalFieldsMap;
	}

	public Map<String, String> getAdditionalFiledToScoreFieldMap() {
		return additionalFiledToScoreFieldMap;
	}

	public void setAdditionalFiledToScoreFieldMap(Map<String, String> additionalFiledToScoreFieldMap) {
		this.additionalFiledToScoreFieldMap = additionalFiledToScoreFieldMap;
	}

	public Boolean getSourceMachienFlag() {
		return sourceMachienFlag;
	}

	public void setSourceMachienFlag(Boolean sourceMachienFlag) {
		this.sourceMachienFlag = sourceMachienFlag;
	}

	public Boolean getDestMachienFlag() {
		return destMachienFlag;
	}

	public void setDestMachienFlag(Boolean destMachienFlag) {
		this.destMachienFlag = destMachienFlag;
	}

	public Boolean getDateTimeToScoreFlag() {
		return dateTimeToScoreFlag;
	}

	public void setDateTimeToScoreFlag(Boolean dateTimeToScoreFlag) {
		this.dateTimeToScoreFlag = dateTimeToScoreFlag;
	}

	public Boolean getCountryToScoreFlag() {
		return countryToScoreFlag;
	}

	public void setCountryToScoreFlag(Boolean countryToScoreFlag) {
		this.countryToScoreFlag = countryToScoreFlag;
	}

	public Boolean getActionTypeToScoreFlag() {
		return actionTypeToScoreFlag;
	}

	public void setActionTypeToScoreFlag(Boolean actionTypeToScoreFlag) {
		this.actionTypeToScoreFlag = actionTypeToScoreFlag;
	}

	public String getDataSourcesConfigurationKey() {
		return dataSourcesConfigurationKey;
	}

	public void setDataSourcesConfigurationKey(String dataSourcesConfigurationKey) {
		this.dataSourcesConfigurationKey = dataSourcesConfigurationKey;
	}

	@Override
	public void reset() {
		super.reset();
		scoresFieldMap = new LinkedHashMap<String, String>();
		additionalScoreFeldsMap = new LinkedHashMap<String, String>();
		additionalFieldsMap = new LinkedHashMap<String, String>();
		additionalFiledToScoreFieldMap = new LinkedHashMap<String, String>();

		sourceMachienFlag=null;
		destMachienFlag=null;
		countryToScoreFlag=null;
		actionTypeToScoreFlag=null;
		dateTimeToScoreFlag=null;
		dataSourcesConfigurationKey=null;
	}
}
