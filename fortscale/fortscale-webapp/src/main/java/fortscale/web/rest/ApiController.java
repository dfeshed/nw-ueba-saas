package fortscale.web.rest;

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import fortscale.services.fe.ClassifierService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.ColumnMapRowMapper;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.services.exceptions.UnknownResourceException;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;






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
		StringBuilder builder = new StringBuilder("There is no resource for path ");
        builder.append(request.getRequestURI());
        @SuppressWarnings("unchecked")
		Map<String, String[]> map = request.getParameterMap();
        if(map.size() > 0){
        	builder.append(" parameters: ");
        
	        for(Map.Entry<String, String[]> entry: map.entrySet()){
	        	builder.append(entry.getKey()).append("=").append(StringUtils.join(entry.getValue(), ',')).append(" ");
	        }
        }
        throw new UnknownResourceException(builder.toString());
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

    @RequestMapping(value="/getLatestRuntime", method=RequestMethod.GET)
    @ResponseBody
    @LogException
    public Long getLatestRuntime(@RequestParam(required=true) String tableName,	Model model){
        return classifierService.getLatestRuntime(tableName);
    }
		
	@RequestMapping(value="/selfCheck", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public Date selfCheck(){
		return new Date();
	}
	
}
