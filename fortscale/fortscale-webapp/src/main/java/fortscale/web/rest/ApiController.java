package fortscale.web.rest;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.EBSResult;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;
import fortscale.web.exceptions.UnknownResourceException;






@Controller
@RequestMapping("/api/**")
public class ApiController {

	@Autowired
	private JdbcOperations impalaJdbcTemplate;
	
	@Autowired
	private ClassifierService classifierService;
	
	
	@RequestMapping("/**")
	@LogException
    public void unmappedRequest(HttpServletRequest request) {
        String uri = request.getRequestURI();
        throw new UnknownResourceException("There is no resource for path " + uri);
    }
	
	@RequestMapping(value="/investigate", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Map<String, Object>>> investigate(@RequestParam(required=true) String query,
			@RequestParam(required=false) String countQuery,
			Model model){
		DataBean<List<Map<String, Object>>> retBean = new DataBean<>();
		List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query, new ColumnMapRowMapper());
		int total = resultsMap.size();
		if(countQuery != null) {
			total = impalaJdbcTemplate.queryForInt(countQuery);
		}
		retBean.setData(resultsMap);
		retBean.setTotal(total);
		return retBean;
	}
	
	@RequestMapping(value="/investigateWithEBS", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Map<String, Object>>> investigateWithEBS(@RequestParam(required=true) String query,
			@RequestParam(defaultValue="0") Integer offset,
			@RequestParam(defaultValue="50") Integer limit,
			Model model){
		DataBean<List<Map<String, Object>>> retBean = new DataBean<>();
		EBSResult ebsResult = classifierService.getEBSAlgOnQuery(query, offset, limit);
		retBean.setData(ebsResult.getResultsList());
		retBean.setOffset(ebsResult.getOffset());
		retBean.setTotal(ebsResult.getTotal());
		return retBean;
	}
	
	@RequestMapping(value="/getLatestRuntime", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public Long getLatestRuntime(@RequestParam(required=true) String tableName,	Model model){
		return classifierService.getLatestRuntime(tableName);
	}
	
}
