package fortscale.web.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.aggregation.feature.services.SupportingInformationService;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.HistogramPair;
import fortscale.domain.core.SupportingInformationData;
import fortscale.domain.core.dao.EvidencesRepository;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.*;
import fortscale.services.exceptions.InvalidValueException;
import fortscale.utils.TimestampUtils;
import fortscale.utils.logging.Logger;
import fortscale.domain.core.Histogram;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Autowired
	private SupportingInformationService supportingInformationService;

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
		String dataEntityId = evidence.getDataEntityId();
		String dataEntityTimestampField = "event_time_utc";
		Long startDate = evidence.getStartDate();
		Long endDate = evidence.getEndDate();
		//The convention is to ask for the first page by index (1) but the real index is (0)
		if (page != null) {
			if (page < 1) {
				throw new InvalidValueException("Page number must be greater than 0");
			}
			page -=1;
		}

		//set sort order
		SortDirection sortDir = SortDirection.DESC;
		String sortFieldStr = dataEntityTimestampField;
		if (sort_field != null) {
			if (sort_direction != null){
				sortDir = SortDirection.valueOf(sort_direction);
				sortFieldStr = sort_field;
			}
		}

		switch (dataEntityId) {
			case "amt_session":
				dataEntityTimestampField = "end_time_utc";
				break;
			case "vpn_session":
				dataEntityTimestampField = "end_time_utc";
				break;
			default:
				dataEntityTimestampField = "event_time_utc";
		}

		ObjectMapper mapper = new ObjectMapper();
		DataQueryHelper dataQueryHelper = new DataQueryHelper(dataEntitiesConfig);
		//add conditions
		List<Term> termsMap = new ArrayList<>();
		//add condition to filter user
		Term term = dataQueryHelper.createUserTerm(entityName, normalizedUsernameField);
		termsMap.add(term);
		//add condition about time range
		Long currentTimestamp = System.currentTimeMillis();
		term = dataQueryHelper.createDateRangeTerm(dataEntityTimestampField, TimestampUtils.convertToSeconds(startDate), TimestampUtils.convertToSeconds(endDate) );
		termsMap.add(term);
		//sort according to event times for continues forwarding
		List<QuerySort> querySortList = dataQueryHelper.createQuerySort(sortFieldStr, sortDir);


		DataQueryDTO dataQueryObject = dataQueryHelper.createDataQuery(dataEntityId, "*", termsMap, querySortList, size);
		return dataQueryHandler(dataQueryObject, request_total, use_cache, page, size);
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
	 * @param entityType the entity type (user, machine etc.)
	 * @param entityName the entity name (e.g. mike@cnn.com)
	 * @param dataEntityId the data source (ssh, kerberos, etc.), or combination of some
	 * @param feature the related feature
	 * @param endTime the evidence end time in seconds
	 *
	 * @return list of histogramPair
	 *
	 */
	@RequestMapping(value="/{id}/histogram",method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<HistogramPair>> getEvidenceHistogram( @PathVariable(value = "id") String evidenceId,
															   @RequestParam(value = "entity_type") String entityType,
															   @RequestParam(value = "entity_name") String entityName,
															   @RequestParam(value = "data_entity_id") String dataEntityId,
															   @RequestParam(value = "feature") String feature,
															   @RequestParam(value = "end_time") Long endTime){
		DataBean<List<HistogramPair>> histogramBean = new DataBean<>();

		SupportingInformationData evidenceSupportingInformationData = supportingInformationService.getEvidenceSupportingInformationData(entityType, entityName, dataEntityId, feature, TimestampUtils.convertToMilliSeconds(endTime));

		Map<Object, Double> supportingInformationHistogram = evidenceSupportingInformationData.getHistogram();

		List<HistogramPair> listOfHistogramPairs = createListOfHistogramPairs(supportingInformationHistogram);

		histogramBean.setData(listOfHistogramPairs);

		return histogramBean;
	}

	private List<HistogramPair> createListOfHistogramPairs(Map<Object, Double> supportingInformationHistogram) {

		List<HistogramPair> histogramPairs = new ArrayList<>();

		for (Map.Entry<Object, Double> supportingInformationHistogramEntry : supportingInformationHistogram.entrySet()) {
			String key = (String) supportingInformationHistogramEntry.getKey();
			Double value = supportingInformationHistogramEntry.getValue();

			HistogramPair histogramPair = new HistogramPair(key, value);

			histogramPairs.add(histogramPair);
		}

		return histogramPairs;
	}


}
