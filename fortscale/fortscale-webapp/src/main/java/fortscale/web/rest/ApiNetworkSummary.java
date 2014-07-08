package fortscale.web.rest;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.services.networksummary.NetworkSummaryDTO;
import fortscale.services.networksummary.NetworkSummaryService;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;


@Controller
@RequestMapping("/api/networkSummary/**")
public class ApiNetworkSummary {
	@Autowired
	NetworkSummaryService networkSummaryService;

	@RequestMapping(value="/summary", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Map<String, NetworkSummaryDTO>> getNetworkSummary(){
		DataBean<Map<String, NetworkSummaryDTO>> res = new DataBean<Map<String, NetworkSummaryDTO>>();
		Map<String, NetworkSummaryDTO> data = networkSummaryService.getNetworkSummary();
		res.setData(data);
		res.setOffset(0);
		res.setTotal(data.size());
		return res;
	}
}
