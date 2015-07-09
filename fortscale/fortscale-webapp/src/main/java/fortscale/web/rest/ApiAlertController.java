package fortscale.web.rest;

import fortscale.domain.core.AlertStatus;
import fortscale.domain.core.EntityType;
import fortscale.domain.core.Severity;
import fortscale.domain.core.dao.AlertsRepository;
import fortscale.domain.core.Alert;
import fortscale.domain.core.dao.rest.Alerts;
import fortscale.utils.logging.Logger;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/api/alerts")
public class ApiAlertController extends BaseController {


	private static final int DEFAULT_PAGE_SIZE = 20;
	private static Logger logger = Logger.getLogger(ApiAlertController.class);

	private static final String TIME_STAMP_START = "ts_start";

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
	public @ResponseBody
	DataBean<List<Alert>> getAlerts(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
										  @RequestParam(required=false) String sortField,
										  @RequestParam(required=false) String sortDirection,
										  @RequestParam(required=false)  Integer size,
										  @RequestParam(required=false, value = "page") Integer pageFromRequest) {

		Sort sortByTSDesc;
		Sort.Direction sortDir = Sort.Direction.DESC;
		if (sortField != null) {
			if (sortDirection != null){
				sortDir = Sort.Direction.valueOf(sortDirection);
			}
			sortByTSDesc = new Sort(new Sort.Order(sortDir, sortField));
		} else {
			sortByTSDesc = new Sort(new Sort.Order(Sort.Direction.DESC, TIME_STAMP_START));
		}
		//if pageForMongo is not set, get first pageForMongo
		//Mongo pages start with 0. While on the API the first page is 1.
		int pageForMongo;
		if (pageFromRequest == null) {
			pageForMongo = 0;
		} else {
			pageForMongo = pageFromRequest -1;
		}
		if (size == null){
			size = DEFAULT_PAGE_SIZE;
		}


		PageRequest pageRequest = new PageRequest(pageForMongo, size, sortByTSDesc);
		Alerts alerts = alertsDao.findAll(pageRequest);

		DataBean<List<Alert>> entities = new DataBean<>();
		entities.setData(alerts.getAlerts());
		//total count of the total items in query.
		Long count = alertsDao.count(pageRequest);
		entities.setTotal(count.intValue());
		entities.setOffset(pageForMongo * size);
		return entities;
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
		throw new RuntimeException("NOT SUPPORTED");
//		alertsDao.add(alert);
//		return alert;
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

	/**
	 * //This API gets a single alert  GET: /api/alerts/{alertId}
	 * @param id
	 * @return
	 */
	@RequestMapping(value="{id}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<Alert> getAlertsById(@PathVariable String id)
	{
		Alert alert = alertsDao.getAlertById(id);
		DataBean<Alert> toReturn = new DataBean<Alert>();
		toReturn.setData(alert);

		return toReturn;
	}

}
