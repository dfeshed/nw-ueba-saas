package fortscale.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.core.Notification;
import fortscale.domain.core.NotificationAggregate;
import fortscale.domain.core.NotificationResource;
import fortscale.domain.core.dao.NotificationResourcesRepository;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;

@Controller
@RequestMapping("/api/notifications")
public class ApiNotificationsController {

	private static final String TIME_STAMP = "ts";

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private NotificationResourcesRepository notificationResourcesRepository;

	private DataBean<List<Notification>> notificationsDataSingle(
			Iterable<Notification> userNotifications) {
		DataBean<List<Notification>> ret = new DataBean<List<Notification>>();

		if (userNotifications != null) {
			ArrayList<Notification> array = new ArrayList<Notification>();
			for (Notification notification : userNotifications) {
				NotificationResource res = notificationResourcesRepository
						.findByMsg_name(notification.getCause());

				String cause = res.getSingle();

				if (notification.getAttributes() != null) {
					cause = generateDynamicCause(cause, notification.getAttributes());
				}
				notification.setCause(cause);
				array.add(notification);
			}

			ret.setData(array);
			ret.setTotal(array.size());
			return ret;
		} else {
			// No documents found, return empty response
			return new DataBean<List<Notification>>();
		}
	}

	private DataBean<List<Object>> notificationDataAgg(Iterable<NotificationAggregate> overviewNotificationsAgg) {
		DataBean<List<Object>> ret = new DataBean<List<Object>>();
		ArrayList<Object> array = new ArrayList<Object>();
		if (overviewNotificationsAgg != null) {
			for (NotificationAggregate notification : overviewNotificationsAgg) {								
				NotificationResource res = notificationResourcesRepository.findByMsg_name(notification.getCause());

				String cause = (1 == notification.getAggregated().size() ) ? res.getSingle() : res.getAgg();
				notification.setCause(cause);
			
				array.add(notification);
			}

			ret.setData(array);
			ret.setTotal(array.size());

			return ret;
		}
		{
			return new DataBean<List<Object>>();
		}
	}

	private String generateDynamicCause(String cause, Map<String, String> attributes) {
		String dynamicCause = cause;

		for (String key : attributes.keySet()) {
			dynamicCause = dynamicCause.replace(String.format("{%s}", key), attributes.get(key));
		}

		return dynamicCause;
	}

	@RequestMapping(value = "/{fsid:.+}/user", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> userNotifications(@PathVariable("fsid") String fsid) {
		Iterable<Notification> userNotifications = notificationsRepository.findByFsId(fsid);
		return notificationsDataSingle(userNotifications);
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> list() {
		// We return the list of notifications sorted by timestamp (descending),
		// and limited to 10
		Sort sortByTSDesc = new Sort(new Sort.Order(Sort.Direction.DESC, TIME_STAMP));
		PageRequest request = new PageRequest(0, 10, sortByTSDesc);

		Iterable<Notification> overviewNotifications = notificationsRepository.findAll(request);
		return notificationsDataSingle(overviewNotifications);
	}

	
	/***
	 * Gets notification after a given time stamp, with optional paging offset
	 * @return list of matching notification, empty list if no notification found
	 */
	@RequestMapping(value = "/after/{ts}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> after(@PathVariable("ts") int ts) {
		
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, TIME_STAMP));
		
		// pass the time stamp and paging to the repository to perform the query
		Iterable<Notification> notifications = notificationsRepository.findByTsGreaterThan(ts, sort);
		return notificationsDataSingle(notifications);
	}
	
	@RequestMapping(value = "/aggregate", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Object>> agg() {
		Sort sortByTSDesc = new Sort(new Sort.Order(Sort.Direction.DESC, TIME_STAMP));
		PageRequest request = new PageRequest(0, 10, sortByTSDesc);

		Iterable<NotificationAggregate> overviewNotificationsAgg = notificationsRepository.findAllAndAggregate(request);
		return notificationDataAgg(overviewNotificationsAgg);
	}


	@RequestMapping(value = "/clearAll", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public List<Notification> clearAll() {
		notificationsRepository.deleteAll();
		return new ArrayList<Notification>();
	}
}
