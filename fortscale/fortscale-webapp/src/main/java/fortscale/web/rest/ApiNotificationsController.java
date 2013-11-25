package fortscale.web.rest;

import java.util.ArrayList;
import java.util.Iterator;
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

	private DataBean<List<Notification>> notificationsData(Iterable<Notification> userNotifications) {
		DataBean<List<Notification>> ret = new DataBean<List<Notification>>();

		if (userNotifications != null) {
			ArrayList<Notification> array = new ArrayList<Notification>();
			for (Notification notification : userNotifications) {
				NotificationResource res = notificationResourcesRepository
						.findByMsg_name(notification.getCause());

				String cause = "";

				if (notification.getType() == null || notification.getType().length() == 0
						|| notification.getType() != "agg") {
					cause = res.getSingle();
				} else {
					cause = res.getAgg();
				}

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
		return notificationsData(userNotifications);
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
		return notificationsData(overviewNotifications);
	}

	@RequestMapping(value = "/clearAll", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public List<Notification> clearAll() {
		notificationsRepository.deleteAll();
		return new ArrayList<Notification>();
	}
}
