package presidio.ui.presidiouiapp.rest;



import fortscale.aggregation.feature.services.historicaldata.SupportingInformationAggrFunc;

import fortscale.domain.core.*;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationData;

import fortscale.domain.core.dao.rest.Events;
import fortscale.domain.historical.data.SupportingInformationKey;
import fortscale.domain.historical.data.SupportingInformationSingleKey;
import fortscale.services.EvidencesService;
import fortscale.services.LocalizationService;

import fortscale.utils.FilteringPropertiesConfigurationHandler;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;


import fortscale.domain.rest.HistoricalDataRestFilter;
import presidio.ui.presidiouiapp.demoservices.DemoBuilder;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import presidio.ui.presidiouiapp.beans.DataBean;
import presidio.ui.presidiouiapp.rest.Utils.ApiUtils;
import presidio.ui.presidiouiapp.rest.Utils.ResourceNotFoundException;
import presidio.ui.presidiouiapp.rest.entities.IndicatorStatisticsEntity;
import presidio.ui.presidiouiapp.rest.entities.SupportingInformationEntry;

import java.util.*;

/**
 * REST API for Evidences querying
 * Date: 7/2/2015.
 */
@Controller
@RequestMapping("/api/evidences")
public class ApiEvidenceController  {

	private static final String DESC = "DESC";
	private static final String ASC = "ASC";
	private static final String OTHERS_COLUMN = "Others";
	private static final String TIME_GRANULARITY_PROPERTY = "timeGranularity";
	private static final String TIME_STAMP = "ts";


	private static Logger logger = Logger.getLogger(ApiEvidenceController.class);


	@Value("${impala.data.table.fields.normalized_username:normalized_username}")
	private String normalizedUsernameField;
	private DemoBuilder demoBuilder;

	private EvidencesService evidencesService;
	private LocalizationService localizationService;


	public ApiEvidenceController(EvidencesService evidencesService, LocalizationService localizationService) {
		this.evidencesService = evidencesService;
		this.localizationService = localizationService;
	}

	private void updateEvidenceFields(Evidence evidence) {
		if (evidence != null && evidence.getAnomalyTypeFieldName() != null) {
			//Each Evidence need to be configure  at the fortscale.evidence.type.map varibale (name:UI Title)
			String name = localizationService.getIndicatorName(evidence);
			evidence.setAnomalyType(name);
			evidence.setName(name);
		}
	}




	@RequestMapping(value="/distinct-field/{fieldName}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public List getDisticntByFieldName (@PathVariable String fieldName) {
		return evidencesService.getDistinctByFieldName(fieldName);
	}

	/**
	 * The API to get a single evidence. GET: /api/evidences/{evidenceId}
	 * @param id The ID of the requested evidence
	 */
	@RequestMapping(value="{id}",method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Evidence> getEvidence(@PathVariable String id) {
		DataBean<Evidence> ret = new DataBean<>();
		Evidence evidence = evidencesService.findById(id);
		updateEvidenceFields(evidence);
		ret.setData(evidence);
		return ret;
	}

	/**
	 *
	 * @param id - The evidence ID
	 * @param request_total - If we need to extract the total of rows that match to the filter (not only currey filter, and not limit to 200)
	 * @param use_cache - If we want to use 15 minutes cache
	 * @param page - page number
	 * @param size  - page size
	 * @param sort_field - sore field
	 * @param sort_direction - sort direction
	 * @return
	 */
	@RequestMapping(value = "/{id}/events", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Map<String, Object>>> getEvents(@PathVariable String id,
															 @RequestParam(defaultValue = "true") boolean request_total,
															 @RequestParam(defaultValue = "true") boolean use_cache,
															 @RequestParam(defaultValue = "1") Integer page, // starting from page 1
															 @RequestParam(defaultValue = "20") Integer size,
															 @RequestParam(required=false) String sort_field,
															 @RequestParam(required=false) String sort_direction) {


		DataBean<List<Map<String, Object>>> response = new DataBean<>();
		Events events = evidencesService.getListOfEvents(request_total, use_cache, page, size, sort_field, sort_direction, id);
		if (events!=null){
			response.setData(events.getData());
			response.setTotal(events.getTotal());
		}

		return response;


//		DataBean<List<Map<String, Object>>> answer = new DataBean<>();
//		Map<String, Object> fieldsMap = MockScenarioGenerator.getMockKerberosEvent();
//
//
//
//		answer.setData(Arrays.asList(fieldsMap));
//		answer.setTotal(1);
//		return answer;

	}





//        EntitySupportingInformation entitySupportingInformationFromEvidence = evidence.getSupportingInformation();
//		if ( entitySupportingInformationFromEvidence == null || entitySupportingInformationFromEvidence.generateResult().size() == 0) {
//			String entityName = evidence.getEntityName();
//			List<String> dataEntitiesIds = evidence.getDataEntitiesIds();
//			//TODO: add support to multiple dataEntitiies in a single query
//			//TODO: need to create a kind of union the same for base entity but also fo the case of multiple entities which are not under the same base entity
//			//TODO: currently the solution is using only the first entity
//			if (dataEntitiesIds != null && dataEntitiesIds.size() > 0) {
//				String dataEntity = dataEntitiesIds.get(0);
//
//				Long startDate = evidence.getStartDate();
//				Long endDate = evidence.getEndDate();
//				//The convention is to ask for the first page by index (1) but the real index is (0)
//				if (page != null) {
//					if (page < 1) {
//						throw new InvalidValueException("Page number must be greater than 0");
//					}
//					page -= 1;
//				}
//
//				//add conditions
//				List<Term> termsMap = new ArrayList<>();
//				//add condition to filter user
//				Term term = dataQueryHelper.createUserTerm(dataEntity, entityName);
//				termsMap.add(term);
//				// Add condition for custom filtering
//				if (eventsFilter != null) {
//					ObjectMapper objectMapper = new ObjectMapper();
//					Map evidenceMap = null;
//					try {
//						evidenceMap = objectMapper.convertValue(evidence, Map.class);
//					} catch (Exception ex) {
//						logger.error("failed to convert evidence object to map");
//					}
//					List<CustomedFilter> customFilter = eventsFilter.getFilter(evidence.getAnomalyTypeFieldName(), evidenceMap);
//					if (customFilter != null) {
//						termsMap.addAll(dataQueryHelper.createCustomTerms(dataEntity, customFilter));
//					}
//				}
//				//add condition about time range
//				Long currentTimestamp = System.currentTimeMillis();
//				term = dataQueryHelper.createDateRangeTerm(dataEntity, TimestampUtils.convertToSeconds(startDate), TimestampUtils.convertToSeconds(endDate));
//				termsMap.add(term);
//
//				//set sort order
//				SortDirection sortDir;
//				String sortFieldStr;
//
//				List<QuerySort> querySortList = new ArrayList<QuerySort>();
//
//				// Add custom sort if provided by request
//				// List<String, SortDirection> sortMap = new HashMap<>();
//				if (sortField != null) {
//					if (sortDirection != null) {
//						sortDir = SortDirection.valueOf(sortDirection);
//						sortFieldStr = sortField;
//						dataQueryHelper.addQuerySort(querySortList, sortFieldStr, sortDir);
//					}
//				}
//
//				// Sort according to event times for continues forwarding. sort is added so it's primary or secondary
//				// based on previous search params.
//				String timestampField = dataQueryHelper.getDateFieldName(dataEntity);
//				dataQueryHelper.addQuerySort(querySortList, timestampField, SortDirection.DESC);
//
//				DataQueryDTO dataQueryObject = dataQueryHelper.createDataQuery(dataEntity, "*", termsMap, querySortList, size, DataQueryDTOImpl.class);
//				//return dataQueryHandler(dataQueryObject, requestTotal, useCache, page, size);
//				return null;
//
//			} else {
//				return null;
//			}
        //return null;
//		}

//		// in case the Evidences contain the supporting information
//		int pageStart = size*(page-1);
//		int pageEnd = (size*(page-1)+size);
//
//		DataBean<List<Map<String, Object>>> result = new DataBean<>();
//		List<Map<String, Object>> resultMapList = new ArrayList<>();
//		resultMapList = entitySupportingInformationFromEvidence.generateResult();
//
//		//if the page end requier is higer then the actual result bring the actual result
//		pageEnd = pageEnd > resultMapList.size() ? resultMapList.size() : pageEnd;
//
//		result.setData(resultMapList.subList(pageStart,pageEnd));
//        result.setTotal(resultMapList.size());
//		return result;
//
//	}

	/**
	 * A URL for checking the controller
	 */
	@RequestMapping(value="/selfCheck", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public Date selfCheck(){
		return new Date();
	}

	/**
	 * get supporting information of evidence - show the regular behaviour of entity, to emphasize the anomaly in the evidence.
	 *
	 * URL example:
	 * ../../api/evidences/{evidenceId}/historical-data?entity_type=user&entityName=edward@snow.com&dataEntityId=kerberos&feature=dst_machine&startTime=1437480000
	 *
	 * @param evidenceId the evidence evidenceId

	 *
	 * @return list of supporting information entries
	 *
	 */
	@RequestMapping(value="/{id}/historical-data",method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<SupportingInformationEntry>> getEvidenceSupportingInformation(@PathVariable(value = "id") String evidenceId,
																		   HistoricalDataRestFilter historicalDataRestFilter) {
		DataBean<List<SupportingInformationEntry>> supportingInformationBean = new DataBean<>();

		//get the evidence from mongo according to ID
//		Evidence evidence = evidencesService.findById(evidenceId);

//		SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(evidence,
//				historicalDataRestFilter);

		SupportingInformationData evidenceSupportingInformationData = evidencesService.getSupportingInformationIndicatorId(evidenceId);
		if (evidenceSupportingInformationData == null){
			throw new ResourceNotFoundException("Can't get evidence of id: " + evidenceId);
		}

		boolean isSupportingInformationAnomalyValueExists = isSupportingInformationAnomalyValueExists(evidenceSupportingInformationData);

		List<SupportingInformationEntry> rawListOfEntries = createListOfEntries(evidenceSupportingInformationData, isSupportingInformationAnomalyValueExists);

		if(historicalDataRestFilter.getNumColumns() == null){
			historicalDataRestFilter.setNumColumns(rawListOfEntries.size());
		}

		List<SupportingInformationEntry> rearrangedEntries = rearrangeEntriesIfNeeded(rawListOfEntries, historicalDataRestFilter, isSupportingInformationAnomalyValueExists);

		if (evidenceSupportingInformationData.getTimeGranularity() != null) {
			addTimeGranularityInformation(supportingInformationBean, evidenceSupportingInformationData);
		}

//		//Add countries list for supported information if needed
//		Set<String> supportingInformationCountries = getSupportingInformationCountries(historicalDataRestFilter, evidence);
//		if (CollectionUtils.isNotEmpty(supportingInformationCountries)) {
//			supportingInformationBean.addInfo(COUNTRIES_INFO_ATTRIBUTE, supportingInformationCountries);
//		}
//		List<SupportingInformationEntry> rearrangedEntries = new ArrayList<>();
//		rearrangedEntries.add(new SupportingInformationEntry(Arrays.asList("0x12"),25));
//		rearrangedEntries.add(new SupportingInformationEntry(Arrays.asList("0x13"),100));
//		rearrangedEntries.add(new SupportingInformationEntry(Arrays.asList("0x14"),200));
		supportingInformationBean.setData(rearrangedEntries);




		supportingInformationBean.setData(rearrangedEntries);
		supportingInformationBean.setTotal(rearrangedEntries.size());
		return supportingInformationBean;





	}

	/**
	 * Check if the supporting information is VpnSession and return unique list of the countries
	 * @param historicalDataRestFilter
	 * @param evidence
	 * @return
	 */
	private Set<String> getSupportingInformationCountries(HistoricalDataRestFilter historicalDataRestFilter, Evidence evidence) {
		Set<String> supportingInformationCountries = new HashSet<>();
		EntitySupportingInformation supportingInformation = evidence.getSupportingInformation();
//		if (supportingInformation instanceof VpnGeoHoppingSupportingInformation &&
//				historicalDataRestFilter.getFeature().equals(COUNTRY_FEATURE)) {
//			supportingInformationCountries = ((VpnGeoHoppingSupportingInformation) supportingInformation).fetchCountriesNames();
//		}
		return supportingInformationCountries;
	}

	/**
	 * This api return statistics about the indicators in the system.
	 * @param  timeRange - from,to
	 * @return count the evidences from today-timeRange until now.
	 */
	@RequestMapping(value="/statistics", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<IndicatorStatisticsEntity> getStatistics(
			@RequestParam(required=true, value = "start_range") String timeRange)
	{

		IndicatorStatisticsEntity results = new IndicatorStatisticsEntity();
		List<Long> timeRangeList = ApiUtils.splitTimeRangeToFromAndToMiliseconds(timeRange);

		long indicatorsCount = evidencesService.count(timeRangeList.get(0), timeRangeList.get(1));
		results.setCount(indicatorsCount);


		DataBean<IndicatorStatisticsEntity> toReturn = new DataBean<IndicatorStatisticsEntity>();
		toReturn.setData(results);

		return toReturn;
	}

	/*
	 * Rearrange the entries for histograms (i.e. count aggregation based):
	 * 1. optionally limit the number of entries by setting 'Others' entry
	 * 2. Add additional anomaly value entry (if exist)
	 *
	 */
	private List<SupportingInformationEntry> rearrangeEntriesIfNeeded(List<SupportingInformationEntry> listOfEntries, HistoricalDataRestFilter historicalDataRestFilter, boolean isSupportingInformationAnomalyValueExists) {

		if(SupportingInformationAggrFunc.Count.name().equalsIgnoreCase(historicalDataRestFilter.getFunction())) {
			Collections.sort(listOfEntries); // the default sort is ascending

			// re -arrange list according to num columns, if necessary
			if(listOfEntries.size() >= historicalDataRestFilter.getNumColumns() + getNumOfAdditionalColumns(isSupportingInformationAnomalyValueExists)){
				//create new list divided into others, columns and anomaly
				listOfEntries = createListWithOthers(listOfEntries, historicalDataRestFilter.getNumColumns());
			}

			if (historicalDataRestFilter.getSortDirection().equals(DESC)) {
				Collections.reverse(listOfEntries);
			}
		}
		return listOfEntries;
	}

	private boolean isSupportingInformationAnomalyValueExists(SupportingInformationData evidenceSupportingInformationData) {
		return evidenceSupportingInformationData.getAnomalyValue() != null;
	}

	private void addTimeGranularityInformation(DataBean<List<SupportingInformationEntry>> supportingInformationBean,
											   SupportingInformationData evidenceSupportingInformationData) {
		Map<String, Object> infoMap = new HashMap<>(1);
		infoMap.put(TIME_GRANULARITY_PROPERTY, evidenceSupportingInformationData.getTimeGranularity().name().
				toLowerCase());
		supportingInformationBean.setInfo(infoMap);
	}

	private Integer getNumOfAdditionalColumns(boolean isEvidenceSupportAnomalyValue) {
		// num columns + 1 others +1 anomaly
		return (isEvidenceSupportAnomalyValue) ? 2 : 1;
	}

	/**
	 * gets list of supporting information entries, and return a new list divided into 'others' column and the rest of columns.
	 * @param oldList sorted list of SupportingInformationEntry, in ascending order
	 * @param numColumns the number of columns to keep. the rest will be inserted into 'others'
	 * @return list divided into 'others' column and the rest of columns.
	 */
	private List<SupportingInformationEntry> createListWithOthers(List<SupportingInformationEntry> oldList, int numColumns) {

		SupportingInformationEntry anomalyPair = new SupportingInformationEntry();
		boolean hasAnomaly = false;
		for(SupportingInformationEntry pair: oldList){
			if(pair.isAnomaly()){
				anomalyPair = oldList.remove(oldList.indexOf(pair));
				hasAnomaly = true;
				break;
			}
		}

		//get the last numColumns object, and sum their values into one. name this object 'other'
		Double othersValue = 0d;
		int i;
		assert(oldList.size() >= numColumns);
		for (i=0 ; i < oldList.size()- numColumns; i++) {
			SupportingInformationEntry<Double> pair=  oldList.get(i);
			othersValue += pair.getValue();
		}

		//create new list with others, and the remaining columns.
		List<SupportingInformationEntry> newListWithOthers = new ArrayList<>();
		newListWithOthers.add(new SupportingInformationEntry( new SupportingInformationSingleKey(OTHERS_COLUMN).generateKey(), othersValue));

		for(;i < oldList.size();i++){
			newListWithOthers.add(oldList.get(i));
		}

		if (hasAnomaly) {
			//insert the anomalyPair into the new list
			newListWithOthers.add(anomalyPair);
		}

		return newListWithOthers;
	}

	/**
	 * method that helps to serialize aggregated information into our API standarts.
	 * gets a map of supportingInformationKeys,value and return a list of supporting information entries.
	 * this function also marks the anomaly pair - essential for further data manipulation.
	 *
	 * @param supportingInformationData
	 * @param isEvidenceSupportAnomalyValue
	 * @return list of supporting information entries, with (0 or more) anomaly mark
	 */
	private List<SupportingInformationEntry> createListOfEntries(SupportingInformationData supportingInformationData,
													 boolean isEvidenceSupportAnomalyValue) {

		List<SupportingInformationEntry> supportingInformationEntries = new ArrayList<>();
		SupportingInformationKey anomalyValue = null;
		Map<SupportingInformationKey, Object> supportingInformationMapData = supportingInformationData.getData();
		Map<SupportingInformationKey, Map> additionalInformation = supportingInformationData.getAdditionalInformation();

		if (isEvidenceSupportAnomalyValue) {
			anomalyValue = supportingInformationData.getAnomalyValue();
		}

		for (Map.Entry<SupportingInformationKey, Object> supportingInformationEntry :
				supportingInformationMapData.entrySet()) {
			SupportingInformationKey key = supportingInformationEntry.getKey();
			Object value = supportingInformationEntry.getValue();
			SupportingInformationEntry supportingInformationEntry1 = new SupportingInformationEntry(key.generateKey(), (Comparable) value);
			if (anomalyValue != null && key.equals(anomalyValue)){
				supportingInformationEntry1.setIsAnomaly(true);
			}
			if (additionalInformation != null) {
				supportingInformationEntry1.setAdditionalInformation(additionalInformation.get(key));
			}
			supportingInformationEntries.add(supportingInformationEntry1);
		}
		return supportingInformationEntries;
	}


}


