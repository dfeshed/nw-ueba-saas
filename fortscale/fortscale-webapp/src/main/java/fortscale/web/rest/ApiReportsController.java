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

import fortscale.services.actions.impl.ActionRateImpl;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;


@Controller
@RequestMapping("/api/reports/**")
public class ApiReportsController {

	
	@Autowired
	private JdbcOperations impalaJdbcTemplate;
	@Autowired
	private ActionRateImpl actionRateService;
	
	
	@RequestMapping(value="logongraph", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Map<String, Object>>> logongraph(@RequestParam(required=true) String query,
														  @RequestParam(required=true) String action,
														  @RequestParam(required=true) String rateField,
														  @RequestParam(defaultValue="1d") String bucket,
														  Model model) {

		DataBean<List<Map<String, Object>>> retBean = new DataBean<>();
		
		List<Map<String, Object>> sqlResultsMap = impalaJdbcTemplate.query(query, new ColumnMapRowMapper());
		List<Map<String, Object>> actionResultsMap = actionRateService.rateTable(sqlResultsMap, rateField, bucket);
		retBean.setData(actionResultsMap);
		retBean.setTotal(actionResultsMap.size());
		return retBean;
	}
	
	
}