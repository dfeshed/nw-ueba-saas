package fortscale.web.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import fortscale.domain.tracer.Connection;
import fortscale.domain.tracer.FilterSettings;
import fortscale.domain.tracer.ListMode;
import fortscale.domain.tracer.ScoreRange;
import fortscale.domain.tracer.TracerRepository;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;

@Controller
@RequestMapping("/api/tracer")
public class ApiHoppingTracerController extends BaseController {

	@Autowired
	private TracerRepository tracerRepository;
	
	@RequestMapping("/{machine:.+}/expand")
	@ResponseBody
	@LogException
	public ResponseEntity<DataBean<List<Connection>>> expandConnections(
			@PathVariable(value="machine") String machine, 
			@RequestParam(value="treatAsSource", defaultValue="True") boolean treatAsSource, 
			@RequestParam(value="start", defaultValue="0") long start, 
			@RequestParam(value="end", defaultValue="0") long end, 
			@RequestParam(value="accounts", required=false) final List<String> accounts,
			@RequestParam(value="excludeAccounts", defaultValue="False") boolean excludeAccounts,
			@RequestParam(value="machines", required=false) final List<String> machines,
			@RequestParam(value="excludeMachines", defaultValue="False") boolean excludeMachines,
			@RequestParam(value="sources", required=false) final List<String> sources,
			@RequestParam(value="excludeSources", defaultValue="False") boolean excludeSources,
			@RequestParam(value="minScore", defaultValue="0.0d") double minScore,
			@RequestParam(value="maxScore", defaultValue="0.0d") double maxScore) {
		
		// validate parameters
		if (start > end)
			return new ResponseEntity<DataBean<List<Connection>>>(HttpStatus.BAD_REQUEST);
		
		FilterSettings filter = new FilterSettings();
		filter.setAccounts(accounts);
		filter.setAccountsListMode(excludeAccounts? ListMode.Exclude : ListMode.Include);
		filter.setMachines(machines);
		filter.setMachinesListMode(excludeMachines? ListMode.Exclude : ListMode.Include);
		filter.setSources(sources);
		filter.setSourcesListMode(excludeSources? ListMode.Exclude : ListMode.Include);
		filter.setScoreRange(new ScoreRange(minScore, maxScore));
		filter.setStart(start);
		filter.setEnd(end);
			
		
		List<Connection> connections = tracerRepository.expandConnections(machine, treatAsSource, filter);
		
		DataBean<List<Connection>> ret = new DataBean<List<Connection>>();
		ret.setData(connections);
		ret.setTotal(connections.size());
		
		return new ResponseEntity<DataBean<List<Connection>>>(ret, HttpStatus.OK);
	}
	
}
