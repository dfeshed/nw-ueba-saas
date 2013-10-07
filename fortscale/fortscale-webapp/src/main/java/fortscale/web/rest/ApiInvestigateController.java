package fortscale.web.rest;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;






@Controller
@RequestMapping("/api/**")
public class ApiInvestigateController {

	@Autowired
	private JdbcOperations impalaJdbcTemplate;
	
	
	@RequestMapping(value="investigate", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Map<String, Object>>> loginsEvents(@RequestParam(required=true) String query,
			@RequestParam(defaultValue="0") Integer offset,
			@RequestParam(defaultValue="10") Integer limit,
			Model model){
		DataBean<List<Map<String, Object>>> retBean = new DataBean<>();
		List<Map<String, Object>> resultsMap = impalaJdbcTemplate.query(query, new ColumnMapRowMapper());
		retBean.setData(resultsMap);
		retBean.setTotal(resultsMap.size());
		return retBean;
	}
	
	
	
}
