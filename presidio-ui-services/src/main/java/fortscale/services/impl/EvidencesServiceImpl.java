package fortscale.services.impl;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationData;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationGenericData;
import fortscale.common.dataentity.DataEntitiesConfig;
import fortscale.common.dataentity.DataEntity;
import fortscale.common.dataentity.DataEntityField;
import fortscale.domain.core.*;

import fortscale.domain.core.User;
import fortscale.domain.core.dao.rest.Events;
import fortscale.domain.dto.DateRange;
import fortscale.domain.historical.data.SupportingInformationDualKey;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.domain.historical.data.SupportingInformationSingleKey;
import fortscale.domain.historical.data.SupportingInformationTimestampKey;
import fortscale.remote.RemoteAlertClientService;
import fortscale.remote.RemoteClientServiceAbs;
import fortscale.services.EvidencesService;
import fortscale.services.UserService;
import fortscale.services.presidio.core.converters.IndicatorConverter;
import fortscale.temp.EvidenceMockBuilder;
import fortscale.temp.HardCodedMocks;
import fortscale.utils.logging.Logger;
import fortscale.utils.time.TimestampUtils;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import presidio.output.client.api.AlertsApi;
import presidio.output.client.client.ApiClient;
import presidio.output.client.client.ApiException;
import presidio.output.client.model.*;

import java.util.*;
import java.util.List;
import java.util.TreeMap;

/**
 * Services for managing the evidences
 *
 * Date: 6/23/2015.
 */
@Service("evidencesService")
public class EvidencesServiceImpl implements EvidencesService, InitializingBean {

	public static final int DEFAULT_EVENT_PAGE_SIZE = 50;
	public static final int DEFAULT_EVENT_PAGE_NUMBER = 0;
	final String TAG_ANOMALY_TYPE_FIELD_NAME = "tag";
	final String TAG_DATA_ENTITY ="active_directory";

	private static Logger logger = Logger.getLogger(UserServiceImpl.class);


	private DataEntitiesConfig dataEntitiesConfig;
	private UserService userService;
	private IndicatorConverter indicatorConverter;
	private RemoteAlertClientService remoteAlertClientService;


	// Severity thresholds for evidence
	@Value("${evidence.severity.medium:80}")
	protected int medium;
	@Value("${evidence.severity.high:90}")
	protected int high;
	@Value("${evidence.severity.critical:95}")
	protected int critical;
	@Value("${collection.evidence.tag.score:50}")
	protected double tagScore;



	/**
	 * Keeps mapping between score and severity
	 */
	private NavigableMap<Integer,Severity> scoreToSeverity = new TreeMap<>();

	public EvidencesServiceImpl(DataEntitiesConfig dataEntitiesConfig, UserService userService, IndicatorConverter indicatorConverter, RemoteAlertClientService remoteAlertClientService) {
		this.dataEntitiesConfig = dataEntitiesConfig;
		this.userService = userService;
		this.indicatorConverter = indicatorConverter;
		this.remoteAlertClientService = remoteAlertClientService;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// init scoring to severity map
		scoreToSeverity.put(0, Severity.Low);
		scoreToSeverity.put(medium, Severity.Medium);
		scoreToSeverity.put(high, Severity.High);
		scoreToSeverity.put(critical, Severity.Critical);
	}


	@Override
	public void saveEvidenceInRepository(Evidence evidence) {
		saveEvidence(evidence);
	}


	public SupportingInformationData getSupportingInformationIndicatorId(String indicatorId){
		try {
			Indicator indicator = remoteAlertClientService.getConterollerApi().getIndicatorByAlert(indicatorId,"0",true);
			if (indicator==null || indicator.getHistoricalData()==null){
				return null;
			}
			if (indicator.getHistoricalData() instanceof CountAggregation){
				SupportingInformationKey anomalyValue = null;
				Map<SupportingInformationKey, Long> dataMap = new HashMap<>();
				CountAggregation data = (CountAggregation)indicator.getHistoricalData();
				if (org.apache.commons.collections.CollectionUtils.isNotEmpty(data.getBuckets())){
					for (CountBucket countBucket: data.getBuckets()){

						SupportingInformationSingleKey key = new SupportingInformationSingleKey(countBucket.getKey());
						long value = countBucket.getValue().longValue();
						dataMap.put(key,value);
						boolean isAnommaly = BooleanUtils.isTrue(countBucket.getAnomaly());
						if (isAnommaly){
							anomalyValue = key;
						}

					};
					SupportingInformationGenericData<Long> supportingInformationGenericData = new SupportingInformationGenericData(dataMap,anomalyValue);
					return supportingInformationGenericData;
				}



			} else if (indicator.getHistoricalData() instanceof TimeAggregation){
				SupportingInformationKey anomalyValue = null;
				Map<SupportingInformationKey, Long> dataMap = new HashMap<>();
				TimeAggregation data = (TimeAggregation)indicator.getHistoricalData();
				if (org.apache.commons.collections.CollectionUtils.isNotEmpty(data.getBuckets())){
					for (TimeBucket timeBucket: data.getBuckets()){

						SupportingInformationTimestampKey key = new SupportingInformationTimestampKey(TimestampUtils.convertToMilliSeconds(timeBucket.getKey().longValue())+"");
						long value = timeBucket.getValue().longValue();
						dataMap.put(key,value);
						boolean isAnommaly = BooleanUtils.isTrue(timeBucket.getAnomaly());
						if (isAnommaly){
							anomalyValue = key;
						}

					};
					SupportingInformationGenericData<Long> supportingInformationGenericData = new SupportingInformationGenericData(dataMap,anomalyValue);
					return supportingInformationGenericData;
				}

			}	else if (indicator.getHistoricalData() instanceof WeekdayAggregation){
				SupportingInformationKey anomalyValue = null;
				Map<SupportingInformationKey, Long> dataMap = new HashMap<>();
				WeekdayAggregation data = (WeekdayAggregation)indicator.getHistoricalData();
				if (org.apache.commons.collections.CollectionUtils.isNotEmpty(data.getBuckets())){
					List<DailyBucket> dailyBuckets =  data.getBuckets();
					for (DailyBucket dailyBucket:dailyBuckets){
						String dayKey = dailyBucket.getKey();
						for (HourlyBucket hourlyBucket:dailyBucket.getValue()){

							SupportingInformationDualKey supportingInformationDualKey = new SupportingInformationDualKey(dayKey,hourlyBucket.getKey());
							long value = hourlyBucket.getValue().longValue();
							dataMap.put(supportingInformationDualKey,value);
							boolean isAnommaly = BooleanUtils.isTrue(hourlyBucket.getAnomaly());
							if (isAnommaly){
								anomalyValue = supportingInformationDualKey;
							}
						}
					}

					SupportingInformationGenericData<Long> supportingInformationGenericData = new SupportingInformationGenericData(dataMap,anomalyValue);
					return supportingInformationGenericData;
				}

			} else {
				logger.error("Historical data of indicator id {}, of type {} is not match to any relevant type",indicatorId, indicator.getHistoricalData().getClass());
				return null;
			}

		} catch (ApiException e) {
			logger.debug("Cannot find evidence with id: {}",indicatorId);
			return  null;
		}

		return null;

	}
	private List<Evidence> getEvidencesMocks() {
		return Arrays.asList(getEvidenceMock());
	}

	@Override
	public Evidence findById(String id) {

		try {
			Indicator indicator = remoteAlertClientService.getConterollerApi().getIndicatorByAlert(id,"0",false);
			Evidence evidence = indicatorConverter.convertIndicator(indicator,AlertTimeframe.Hourly,"missing-username");
			return evidence;
		} catch (ApiException e) {
			logger.debug("Cannot find evidence with id: {}",id);
			return  null;
		}
	}

	private Evidence getEvidenceMock() {
		return new EvidenceMockBuilder(1).createInstance();
	}


	/**
	 * Saves evidence in Mongo
	 * @param evidence the evidence to save
	 * @return the saved evidence
	 */
	private Evidence saveEvidence(Evidence evidence){
		return getEvidenceMock();
	}

	public long count(long fromTime, long toTime){
		return HardCodedMocks.ALERTS_COUNT;
	}

	@Override
	public List getDistinctByFieldName(String fieldName) {
		return getEvidencesMocks();
	}



	@Override
	public List<Evidence> getEvidencesById(List<String> evidenceIds) {
		return getEvidencesMocks();
	}


	/**
	 * Get the indicator and return list of fields.
	 * Each field is a key-value map
	 * @param requestTotal
	 * @param useCache
	 * @param page
	 * @param size
	 * @param sortField
	 * @param sortDirection
	 * @param evidenceId
	 * @return
	 */
	public Events getListOfEvents(boolean requestTotal, boolean useCache, Integer page,
								  Integer size, String sortField, String sortDirection, String evidenceId){

		EventQuery eventQuery= new EventQuery();

		if (page!=null) {
			eventQuery.setPageNumber(page-1);
		} else {
			eventQuery.setPageNumber(DEFAULT_EVENT_PAGE_NUMBER);
		}
		if (size!=null){
			eventQuery.setPageSize(size);
		} else {
			eventQuery.setPageSize(DEFAULT_EVENT_PAGE_SIZE);
		}

		try {
			Evidence indicator = findById(evidenceId);
			DataEntity dataEntity = dataEntitiesConfig.getAllLeafeEntities().get(indicator.getDataEntitiesIds().get(0));
			EventsWrapper eventsWrapper = remoteAlertClientService.getConterollerApi().getIndicatorEventsByAlert(evidenceId,"0",eventQuery);

			for (Map<String, Object> event:eventsWrapper.getEvents()){
				Map<String, Object> fieldsToAppned = convertEventFields(dataEntity, event);
				event.putAll(fieldsToAppned);
			}

			return new Events(eventsWrapper.getEvents(),eventsWrapper.getTotal());
		} catch (ApiException e) {
			throw new RuntimeException("Can't get evidence of id: " + evidenceId);
		} catch (Exception e) {
			logger.error("Cannot parse fields for indicators");
		}


		return  null;

	}

	/**
	 * Handle single field converstion, convert return list of fields which need to be added and convert values if needed
	 * @param dataEntity
	 * @param event
	 * @return
	 */
	private Map<String,Object> convertEventFields(DataEntity dataEntity, Map<String, Object> event) {

		Map<String,Object> additionalFields = new HashedMap();

		for (DataEntityField dataEntityField : dataEntity.getFields()){
			String uiKey = dataEntityField.getId();
			Object value = convertFieldValue(event, dataEntityField);

			if (value!=null){
				event.put(uiKey,value);
			}
		}
		return additionalFields;
	}

	/**
	 * Extract and convert specific field from the event
	 * @param event
	 * @param dataEntityField
	 * @return
	 */
	private Object convertFieldValue(Map<String, Object> event, DataEntityField dataEntityField) {
		Object value = null;

		String jsonPath[] = dataEntityField.getJsonPath();
		if (jsonPath!=null && jsonPath.length>0){
			Map<String,Object> subValue = event;
			//Iterate over the maps hierarchy to get the single value
			//The single value is always in the end of the hierarhcy
			for (int i=0; i<jsonPath.length-1;i++){
				subValue = (Map<String, Object>)subValue.get(jsonPath[0]);
				if (subValue == null){
					break;
				}
			}
			if (subValue==null){
				value = "";
			} else {

				value = subValue.get(jsonPath[jsonPath.length-1]);
				if (value!=null){
					switch (dataEntityField.getType()){
						case DATE_TIME:
							//Try to convert the value to long - miliSeconds. If fails - return the object without converting it
							if (value instanceof Integer){
								value = ((Integer)value).longValue();
							}
							if (value instanceof Long) {
								value = TimestampUtils.convertToMilliSeconds((long) value);
							}
							break;
						case CAPITALIZE:
							if (value instanceof String) {
								value = StringUtils.lowerCase((String)value);
								value = StringUtils.replace((String)value,"_"," ");
								value = WordUtils.capitalize((String)value);
							}
							break;
					}

				}
			}

		}
		return value;
	}
}