package fortscale.web.rest;


import fortscale.aggregation.feature.services.SupportingInformationService;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.HistogramKey;
import fortscale.domain.core.HistogramPair;
import fortscale.domain.core.SupportingInformationData;
import fortscale.domain.core.dao.EvidencesRepository;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.*;
import fortscale.services.exceptions.InvalidValueException;
import fortscale.utils.TimestampUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * REST API for Evidences querying
 * Date: 7/2/2015.
 */
@Controller
@RequestMapping("/api/evidences")
public class ApiEvidenceController extends DataQueryController {

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

	@Value("${evidence.supporting.information.time.period.in.days:90}")
	private int defaultTimePeriodInDays;

	@Autowired
	private SupportingInformationService supportingInformationService;

	@Autowired
	DataQueryHelper dataQueryHelper;

	/**
	 * The API to get a single evidence. GET: /api/evidences/{evidenceId}
	 * @param id The ID of the requested evidence
	 */
	@RequestMapping(value="{id}",method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Evidence> getEvidence(@PathVariable String id) {
		DataBean<Evidence> ret = new DataBean<>();
		ret.setData(evidencesDao.findById(id));
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
	 * @return
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
	 * ../../api/evidences/{evidenceId}/histogram?entity_type=user&entityName=edward@snow.com&dataEntityId=kerberos&feature=dst_machine&startTime=1437480000
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
	@RequestMapping(value="/{id}/histogram",method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<HistogramPair>> getEvidenceHistogram( @PathVariable(value = "id") String evidenceId,
			@RequestParam(value = "context_type") String contextType,
			@RequestParam(value = "context_value") String contextValue,
			@RequestParam(value = "feature") String feature,
			@RequestParam(value = "function") String aggFunction,
			@RequestParam(required=false,value = "num_columns") Integer numColumns,
			@RequestParam(required=false,value = "sort_direction") String sortDirection,
			@RequestParam(required=false,value = "time_range") Integer timePeriodInDays){
		DataBean<List<HistogramPair>> histogramBean = new DataBean<>();

		//get the evidence from mongo according to ID
		Evidence evidence = evidencesDao.findById(evidenceId);
		if (evidence == null || evidence.getId() == null){
			throw new InvalidValueException("Can't get evidence ofr id: " + evidenceId);
		}

		if(timePeriodInDays == null){
			timePeriodInDays = defaultTimePeriodInDays;
		}
		//create list of histogram pairs divided to columns, anomaly, and Others according to numColumns
		String anomalyValue = evidence.getAnomalyValue();
		String anomalyType = evidence.getAnomalyType();
		SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(contextType, contextValue, evidence.getDataEntitiesIds(),
				feature,anomalyType ,anomalyValue,TimestampUtils.convertToMilliSeconds(evidence.getEndDate()),timePeriodInDays,aggFunction);

		Map<HistogramKey, Double> supportingInformationHistogram = evidenceSupportingInformationData.getHistogram();

		HistogramKey anomaly = evidenceSupportingInformationData.getAnomalyValue();


		//add the anomaly to the relevant fields

		List<HistogramPair> listOfHistogramPairs = createListOfHistogramPairs(supportingInformationHistogram, anomaly);

		//
		if(aggFunction.equalsIgnoreCase("count")) {
			Collections.sort(listOfHistogramPairs); // the default sort is ascending

			// re -arrange list according to num columns
			if(listOfHistogramPairs.size() < numColumns + 2 ){ // num columns + 1 others +1 anomaly
				//do nothing, no need to create 'others'
			}

			else {
				//create new list divided into others, columns and anomaly
				listOfHistogramPairs = createListWithOthers(listOfHistogramPairs, numColumns);
			}

			if (sortDirection != null && sortDirection.equals("DESC")) {
				Collections.reverse(listOfHistogramPairs);
			}
		}

		histogramBean.setData(listOfHistogramPairs);
		return histogramBean;
	}

	/**
	 * gets list of histogramPairs, and return a new list divided into 'others' column and the rest of columns.
	 * @param oldList sorted list of HistogramPair
	 * @param numColumns the number of columns to keep. the rest will be inserted into 'others'
	 * @return list divided into 'others' column and the rest of columns.
	 */
	private  List<HistogramPair> createListWithOthers(List<HistogramPair> oldList, int numColumns){

		HistogramPair anomalyPair = new HistogramPair();
		for(HistogramPair pair: oldList){
			if(pair.isAnomaly()){
				anomalyPair = oldList.remove(oldList.indexOf(pair));
				break;
			}
		}

		//get the last numColumns object, and sum their values into one. name this object 'other'
		double othersValue = 0;
		int i;
		for (i=0 ; i < numColumns; i++) {
			HistogramPair pair=  oldList.get(i);
			othersValue += pair.getValue();
		}

		//create new list with others, and the remaining columns.
		List<HistogramPair> newListWithOthers = new ArrayList<>();
		newListWithOthers.add(new HistogramPair("Others", othersValue));

		for(;i < oldList.size();i++){
			newListWithOthers.add(oldList.get(i));
		}
		//insert the anomalyPair into the new list
		oldList.add(anomalyPair);

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
	private List<HistogramPair> createListOfHistogramPairs(Map<HistogramKey, Double> supportingInformationHistogram, HistogramKey anomaly ) {

		List<HistogramPair> histogramPairs = new ArrayList<>();

		for (Map.Entry<HistogramKey, Double> supportingInformationHistogramEntry : supportingInformationHistogram.entrySet()) {
			HistogramKey key = supportingInformationHistogramEntry.getKey();
			Double value = supportingInformationHistogramEntry.getValue();

			HistogramPair histogramPair = new HistogramPair(key, value);

			if (key.equals(anomaly)){
				histogramPair.setIsAnomaly(true);
			}
			histogramPairs.add(histogramPair);
		}
		return histogramPairs;
	}
}
