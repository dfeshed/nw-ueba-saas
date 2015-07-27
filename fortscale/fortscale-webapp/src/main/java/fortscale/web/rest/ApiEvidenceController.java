package fortscale.web.rest;


import fortscale.domain.core.Evidence;
import fortscale.domain.core.HistogramPair;
import fortscale.domain.core.dao.EvidencesRepository;
import fortscale.domain.core.Histogram;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
public class ApiEvidenceController {

	/**
	 * Mongo repository for fetching evidences
	 */
	@Autowired
	private EvidencesRepository evidencesDao;



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

		Map<String,Object> info = new HashMap<>();
		info.put("test1","count1");

		//set data of the web bean
		toReturn.setData(histogram);

		return  toReturn;
	}

}
