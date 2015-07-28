package fortscale.web.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.HistogramPair;
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

	@RequestMapping(value = "{id}/events", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Map<String, Object>>> getEvents(@PathVariable String id,
															 @RequestParam(defaultValue = "false") boolean requestTotal,
															 @RequestParam(defaultValue = "true") boolean useCache,
															 @RequestParam(defaultValue = "1") Integer page, // starting from page 1
															 @RequestParam(defaultValue = "20") Integer size,
															 @RequestParam(required=false) String sortField,
															 @RequestParam(required=false) String sortDirection) {

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
		if (sortField != null) {
			if (sortDirection != null){
				sortDir = SortDirection.valueOf(sortDirection);
				sortFieldStr = sortField;
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
		return dataQueryHandler(dataQueryObject, requestTotal, useCache, page, size);
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
	 * ../../api/evidences/{evidenceId}/histogram?entity_type=user&entity_name=edward@snow.com&data_entity_id=kerberos&feature=dst_machine&start_time=1437480000
	 *
	 * @param id the evidence id
	 * @param entity_type the entity type (user, machine etc.)
	 * @param entity_name the entity name (e.g. mike@cnn.com)
	 * @param data_entity_id the data source (ssh, kerberos, etc.), or combination of some
	 * @param feature the related feature
	 * @param start_time the evidence start time in seconds
	 *
	 * @return list of histogramPair
	 */
	@RequestMapping(value="/{id}/histogram",method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<HistogramPair>> getEvidenceHistogram( @PathVariable String id,
																@RequestParam String entity_type,
																@RequestParam String entity_name,
																@RequestParam String data_entity_id,
																@RequestParam String feature,
																@RequestParam long start_time){
		DataBean<List<HistogramPair>> toReturn = new DataBean<>();

		List<HistogramPair> histogram = new ArrayList<>();

		//stub histogram - just for now -- instead of the function call
		Histogram stub = new Histogram();
		String key1 = "comp1";
		Number count = 6;
		Map<String,Number> myMap = new HashMap<>();
		myMap.put(key1,count);
		stub.setMap(myMap);

		//convert histogram to ui format
		for (Map.Entry<String,Number> entry: stub.getMap().entrySet()){
			histogram.add(new HistogramPair(entry.getKey(),entry.getValue()));
		}

		//set data of the web bean
		toReturn.setData(histogram);

		return  toReturn;
	}

}
