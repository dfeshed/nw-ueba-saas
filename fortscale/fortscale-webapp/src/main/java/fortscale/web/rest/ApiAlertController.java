package fortscale.web.rest;

import fortscale.domain.core.Alert;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/api/alerts")
public class ApiAlertController extends BaseController {


	private static Logger logger = Logger.getLogger(ApiAlertController.class);

	private static final String TIME_STAMP = "ts_start";

	@Autowired
	private AlertsRepository alertsDao;


	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public @ResponseBody List<Alert> getAlerts() {

		Sort sortByTSDesc = new Sort(new Sort.Order(Sort.Direction.DESC, TIME_STAMP));
		PageRequest request = new PageRequest(0, 10, sortByTSDesc);
		List<Alert> alerts = alertsDao.findAll(request, 20);
		return alerts;
	}

	@RequestMapping(method = RequestMethod.POST)
	@LogException
	@ResponseBody
	public String addAlert(@Valid @RequestBody Alert alert) throws Exception{
//		alertsDao.add(alert);
		return "OK";
	}

	@RequestMapping(value="{id}",method = RequestMethod.PUT)
	@ResponseBody
	@LogException
	public void putAlert(@PathVariable String id, @RequestBody Alert alert) {
		alert.setUuid(id);
//		alertsDao.update(alert);
	}

	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@LogException
	public void deleteAlert(@PathVariable String id) {
		alertsDao.delete(id);
	}

}
