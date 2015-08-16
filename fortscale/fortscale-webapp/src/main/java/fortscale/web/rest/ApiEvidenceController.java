package fortscale.web.rest;

import fortscale.aggregation.feature.services.historicaldata.SupportingInformationAggrFunc;
import fortscale.aggregation.feature.services.historicaldata.SupportingInformationService;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.SupportingInformationData;
import fortscale.domain.core.dao.EvidencesRepository;
import fortscale.domain.histogram.HistogramEntry;
import fortscale.domain.histogram.HistogramKey;
import fortscale.domain.histogram.HistogramSingleKey;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.*;
import fortscale.services.exceptions.InvalidValueException;
import fortscale.utils.ConfigurationUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.utils.time.TimestampUtils;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * REST API for Evidences querying
 * Date: 7/2/2015.
 */
@Controller
@RequestMapping("/api/evidences")
public class ApiEvidenceController extends DataQueryController {

	private static final String DESC = "DESC";
	private static final String ASC = "ASC";
	private static final String OTHERS_COLUMN = "Others";

	private static Logger logger = Logger.getLogger(ApiEvidenceController.class);
	/**
	 * Mongo repository for fetching evidences
	 */
	@Autowired
	private EvidencesRepository evidencesDao;

	@Autowired
	private DataEntitiesConfig dataEntitiesConfig;

	@Value("${impala.data.table.fields.normalized_username:normalized_username}")
	private String normalizedUsernameField;

	@Value("${fortscale.evidence.name.text}")
	private String evidenceNameText;

	@Autowired
	private SupportingInformationService supportingInformationService;

	@Autowired
	DataQueryHelper dataQueryHelper;

	@Value("${fortscale.evidence.type.map}")
	private String evidenceTypeProperty;

	private Map evidenceTypeMap;

	@PostConstruct
	public void initEvidenceMap(){
		evidenceTypeMap = ConfigurationUtils.getStringMap(evidenceTypeProperty);
	}

	private void updateEvidenceFields(Evidence evidence) {
		if (evidence != null && evidence.getAnomalyTypeFieldName() != null) {
			//Each Evidence need to be configure  at the fortscale.evidence.type.map varibale (name:UI Title)
			String anomalyType = evidenceTypeMap.get(evidence.getAnomalyTypeFieldName()).toString();
			evidence.setAnomalyType(anomalyType);
			String evidenceName = String.format(evidenceNameText, evidence.getEntityType().toString().toLowerCase(), evidence.getEntityName(), anomalyType);
			evidence.setName(evidenceName);
		}
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
		Evidence evidence = evidencesDao.findById(id);
		updateEvidenceFields(evidence);
		ret.setData(evidence);
		return ret;
	}

	@RequestMapping(value = "/{id}/events", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Map<String, Object>>> getEvents(@PathVariable String id,
															 @RequestParam(defaultValue = "false") boolean request_total,
															 @RequestParam(defaultValue = "true") boolean use_cache,
															 @RequestParam(defaultValue = "1") Integer page, // starting from page 1
															 @RequestParam(defaultValue = "20") Integer size,
															 @RequestParam(required=false) String sort_field,
															 @RequestParam(required=false) String sort_direction) {

		Evidence evidence = evidencesDao.findById(id);
		if (evidence == null || evidence.getId() == null){
			throw new InvalidValueException("Can't get evidence ofr id: " + id);
		}

		String entityName = evidence.getEntityName();
		List<String> dataEntitiesIds = evidence.getDataEntitiesIds();
		//TODO: add support to multiple dataEntitiies in a single query
		//TODO: need to create a kind of union the same for base entity but also fo the case of multiple entities which are not under the same base entity
		//TODO: currently the solution is using only the first entity
		if (dataEntitiesIds != null && dataEntitiesIds.size() > 0) {
			String dataEntity = dataEntitiesIds.get(0);

			Long startDate = evidence.getStartDate();
			Long endDate = evidence.getEndDate();
			//The convention is to ask for the first page by index (1) but the real index is (0)
			if (page != null) {
				if (page < 1) {
					throw new InvalidValueException("Page number must be greater than 0");
				}
				page -= 1;
			}

			//add conditions
			List<Term> termsMap = new ArrayList<>();
			//add condition to filter user
			Term term = dataQueryHelper.createUserTerm(dataEntity, entityName);
			termsMap.add(term);
			//add condition about time range
			Long currentTimestamp = System.currentTimeMillis();
			term = dataQueryHelper.createDateRangeTerm(dataEntity, TimestampUtils.convertToSeconds(startDate), TimestampUtils.convertToSeconds(endDate));
			termsMap.add(term);

			String timestampField = dataQueryHelper.getDateFieldName(dataEntity);
			//set sort order
			SortDirection sortDir = SortDirection.DESC;
			String sortFieldStr = timestampField;
			if (sort_field != null) {
				if (sort_direction != null) {
					sortDir = SortDirection.valueOf(sort_direction);
					sortFieldStr = sort_field;
				}
			}
			//sort according to event times for continues forwarding
			List<QuerySort> querySortList = dataQueryHelper.createQuerySort(sortFieldStr, sortDir);

			DataQueryDTO dataQueryObject = dataQueryHelper.createDataQuery(dataEntity, "*", termsMap, querySortList, size);
			return dataQueryHandler(dataQueryObject, request_total, use_cache, page, size);
		} else
			return null;
	}

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
	 * get histogram of evidence - show the regular behaviour of entity, to emphasize the anomaly in the evidence.
	 *
	 * URL example:
	 * ../../api/evidences/{evidenceId}/historical-data?entity_type=user&entityName=edward@snow.com&dataEntityId=kerberos&feature=dst_machine&startTime=1437480000
	 *
	 * @param evidenceId the evidence evidenceId
	 * @param contextType the entity type (user, machine etc.)
	 * @param contextValue the entity name (e.g. mike@cnn.com)
	 * @param feature the related feature
	 * @param timePeriodInDays the evidence end time in seconds
	 *
	 * @return list of histogramPair
	 *
	 */
	@RequestMapping(value="/{id}/historical-data",method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<HistogramEntry>> getEvidenceHistogram( @PathVariable(value = "id") String evidenceId,
			@RequestParam(value = "context_type") String contextType,
			@RequestParam(value = "context_value") String contextValue,
			@RequestParam(value = "feature") String feature,
			@RequestParam(value = "function") String aggFunction,
			@RequestParam(required=false,value = "num_columns") Integer numColumns,
			@RequestParam(required=false,defaultValue = DESC,value = "sort_direction") String sortDirection,
			@RequestParam(required=false,defaultValue = "90",value = "time_range") Integer timePeriodInDays){
		DataBean<List<HistogramEntry>> histogramBean = new DataBean<>();

		//get the evidence from mongo according to ID
		Evidence evidence = evidencesDao.findById(evidenceId);
		if (evidence == null || evidence.getId() == null){
			throw new InvalidValueException("Can't get evidence of id: " + evidenceId);
		}

		String anomalyValue = extractAnomalyValue(evidence, feature);

		SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(contextType, contextValue, evidence.getDataEntitiesIds(),
				feature,anomalyValue,TimestampUtils.convertToMilliSeconds(evidence.getEndDate()),timePeriodInDays,aggFunction);

		//create list of histogram pairs divided to columns, anomaly, and Others according to numColumns
		Map<HistogramKey, Double> supportingInformationHistogram = evidenceSupportingInformationData.getHistogram();

		//add the anomaly to the relevant fields
		HistogramKey anomaly = evidenceSupportingInformationData.getAnomalyValue();
		List<HistogramEntry> listOfHistogramEntries = createListOfHistogramPairs(supportingInformationHistogram, anomaly);

		if(numColumns == null){
			numColumns = listOfHistogramEntries.size();
		}

		if(SupportingInformationAggrFunc.Count.name().equalsIgnoreCase(aggFunction)) {
			Collections.sort(listOfHistogramEntries); // the default sort is ascending

			// re -arrange list according to num columns, if necessary
			if(listOfHistogramEntries.size() >= numColumns + 2 ){ // num columns + 1 others +1 anomaly.
				//create new list divided into others, columns and anomaly
				listOfHistogramEntries = createListWithOthers(listOfHistogramEntries, numColumns);
			}

			if (sortDirection.equals(DESC)) {
				Collections.reverse(listOfHistogramEntries);
			}
		}

		histogramBean.setData(listOfHistogramEntries);
		return histogramBean;
	}

	private String extractAnomalyValue(Evidence evidence, String feature) {

		boolean contextAndFeatureMatch = isContextAndFeatureMatch(evidence, feature);

		if (contextAndFeatureMatch) { // in this case we want the inverse chart
			return evidence.getEntityName();
		}
		else {
			return evidence.getAnomalyValue();
		}
	}

	private boolean isContextAndFeatureMatch(Evidence evidence, String feature) {
		return feature.equalsIgnoreCase(evidence.getEntityTypeFieldName());
	}

	/**
	 * gets list of histogramEntries, and return a new list divided into 'others' column and the rest of columns.
	 * @param oldList sorted list of HistogramEntry, in ascending order
	 * @param numColumns the number of columns to keep. the rest will be inserted into 'others'
	 * @return list divided into 'others' column and the rest of columns.
	 */
	private  List<HistogramEntry> createListWithOthers(List<HistogramEntry> oldList, int numColumns){

		HistogramEntry anomalyPair = new HistogramEntry();
		for(HistogramEntry pair: oldList){
			if(pair.isAnomaly()){
				anomalyPair = oldList.remove(oldList.indexOf(pair));
				break;
			}
		}

		//get the last numColumns object, and sum their values into one. name this object 'other'
		double othersValue = 0;
		int i;
		assert(oldList.size() >= numColumns);
		for (i=0 ; i < oldList.size()- numColumns; i++) {
			HistogramEntry pair=  oldList.get(i);
			othersValue += pair.getValue();
		}

		//create new list with others, and the remaining columns.
		List<HistogramEntry> newListWithOthers = new ArrayList<>();
		newListWithOthers.add(new HistogramEntry( new HistogramSingleKey(OTHERS_COLUMN).generateKey(), othersValue));

		for(;i < oldList.size();i++){
			newListWithOthers.add(oldList.get(i));
		}
		//insert the anomalyPair into the new list
		newListWithOthers.add(anomalyPair);

		return newListWithOthers;
	}

	/**
	 * method that helps to serialize aggregated information into our API standarts.
	 * gets a map of histogramKey,value and return a list of HistogramPairs.
	 * this function also marks the anomaly pair - essential for further data manipulation.
	 *
	 * @param supportingInformationHistogram
	 * @param anomaly
	 * @return list of HistogramPairs, with (0 or more) anomaly mark
	 */
	private List<HistogramEntry> createListOfHistogramPairs(Map<HistogramKey, Double> supportingInformationHistogram, HistogramKey anomaly ) {

		List<HistogramEntry> histogramEntries = new ArrayList<>();

		for (Map.Entry<HistogramKey, Double> supportingInformationHistogramEntry : supportingInformationHistogram.entrySet()) {
			HistogramKey key = supportingInformationHistogramEntry.getKey();
			Double value = supportingInformationHistogramEntry.getValue();

			HistogramEntry histogramEntry = new HistogramEntry(key.generateKey(),value);

			if (key.equals(anomaly)){
				histogramEntry.setIsAnomaly(true);
			}
			histogramEntries.add(histogramEntry);
		}
		return histogramEntries;
	}

}
