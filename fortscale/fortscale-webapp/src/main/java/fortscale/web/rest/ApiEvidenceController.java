package fortscale.web.rest;


import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.Evidence;
import fortscale.domain.core.dao.EvidencesRepository;
import fortscale.services.dataentity.DataEntitiesConfig;
import fortscale.services.dataqueries.querydto.*;
import fortscale.utils.TimestampUtils;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.DataQueryController;
import fortscale.web.beans.DataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
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

	@RequestMapping(value = "{id}/top3events", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Map<String, Object>>> getTop3Events(@PathVariable String id,
															 @RequestParam(defaultValue = "false") boolean requestTotal,
															 @RequestParam(defaultValue = "true") boolean useCache,
															 @RequestParam(required = false) Integer page, // starting from 0
															 @RequestParam(defaultValue = "20") Integer size) {

		Evidence evidence = evidencesDao.findById(id);
		String entityType;
		String entityName = evidence.getEntityName();
		String dataEntityId = evidence.getDataEntityId();
		String dataEntityTimestampField = "event_time_utc";
		Long startDate = evidence.getStartDate();
		Long endDate = evidence.getEndDate();
		//The convention is to ask for the first page by index (1) but the real index is (0)
		Integer pageSize = size - 1;

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
		Term term = dataQueryHelper.createUserTerm(entityName);
		termsMap.add(term);
		//add condition about time range
		Long currentTimestamp = System.currentTimeMillis();
		term = dataQueryHelper.createDateRangeTerm(dataEntityTimestampField, TimestampUtils.convertToSeconds(startDate), TimestampUtils.convertToSeconds(endDate) );
		termsMap.add(term);
		//sort according to event times for continues forwarding
		List<QuerySort> querySortList = dataQueryHelper.createQuerySort(dataEntityTimestampField, SortDirection.DESC);


		DataQueryDTO dataQueryObject = dataQueryHelper.createDataQuery(dataEntityId, "*", termsMap, querySortList, pageSize);
		return dataQueryHandler(dataQueryObject, requestTotal, useCache, page, pageSize);
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

}
