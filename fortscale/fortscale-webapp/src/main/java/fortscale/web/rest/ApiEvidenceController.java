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
	 * @param id
	 * @param bucketConfig
	 * @return list of histogramPair
	 */
	@RequestMapping(value="/{id}/getHistogram",method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<HistogramPair>> getEvidenceHistogram(@PathVariable String id, String bucketConfig){
		DataBean<List<HistogramPair>> toReturn = new DataBean<>();

		List<HistogramPair> histogram = new ArrayList<>();

		//stub histogram - just for now -- instead of the function call
		Histogram stub = new Histogram();
		String key1 = "comp1";
		Number count = 6;
		Map<String,Number> myMap = new HashMap<>();
		myMap.put(key1,count);
		stub.setMap(myMap);

		//create web histogram from histogram
		for (Map.Entry<String,Number> entry: stub.getMap().entrySet()){
			histogram.add(new HistogramPair(entry.getKey(),entry.getValue()));
		}

		//set data of the web bean
		toReturn.setData(histogram);

		return  toReturn;
	}

}
