package fortscale.web.rest;

import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.core.dao.rest.Alert;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@Controller
@RequestMapping("/api/alerts")
public class ApiAlertController extends BaseController {


	private static Logger logger = Logger.getLogger(ApiAlertController.class);

	private static final String TIME_STAMP = "ts_start";

	@Autowired
	private AlertsRepository alertsDao;


	/**
	 * the api to return all alerts. GET: /api/alerts
	 * @param httpRequest
	 * @param httpResponse
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET)
	@LogException
	public @ResponseBody Alerts getAlerts(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

		Sort sortByTSDesc = new Sort(new Sort.Order(Sort.Direction.DESC, TIME_STAMP));
		PageRequest pageRequest = new PageRequest(0, 10, sortByTSDesc);
		Alerts alerts = alertsDao.findAll(pageRequest, 20, httpRequest);
		return alerts;
	}

	/**
	 * The API to insert one alert. POST: /api/alerts
	 * @param alert
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(method = RequestMethod.POST)
	@LogException
	@ResponseBody
	public Alert addAlert(@Valid @RequestBody Alert alert) throws Exception{
		alertsDao.add(alert);
		return alert;
	}

	/**
	 * The API to update a single alert. PUT: /api/alerts/alertId
	 * @param id
	 * @param alert
	 */
	@RequestMapping(value="{id}",method = RequestMethod.PUT)
	@ResponseBody
	@LogException
	public void putAlert(@PathVariable String id, @RequestBody Alert alert) {
		alert.setUuid(id);
//		alertsDao.update(alert);
	}

	/**
	 * The API to delete a single alert. Delete: /api/alerts/alertId
	 * @param id
	 */
	@RequestMapping(value="{id}", method = RequestMethod.DELETE)
	@ResponseBody
	@LogException
	public void deleteAlert(@PathVariable String id) {
		alertsDao.delete(id);
	}

}
