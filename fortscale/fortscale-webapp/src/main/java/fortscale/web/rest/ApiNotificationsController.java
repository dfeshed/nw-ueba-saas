package fortscale.web.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.core.Notification;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;

@Controller
@RequestMapping("/api/notifications")
public class ApiNotificationsController {

	private static final String TIME_STAMP = "ts";

	@Autowired
	private NotificationsRepository notificationRepository;

	@RequestMapping(value = "/{fsid:.+}/user", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> userNotifications(@PathVariable("fsid") String fsid) {
		DataBean<List<Notification>> ret = new DataBean<List<Notification>>();
		Iterable<Notification> userNotifications = notificationRepository.findByFsId(fsid);

		if (userNotifications != null) {
			ArrayList<Notification> array = new ArrayList<Notification>();
			for (Notification notification : userNotifications) {
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

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> list() {
		DataBean<List<Notification>> ret = new DataBean<List<Notification>>();

		// We return the list of notifications sorted by timestamp (descending)
		Sort sortByTSDesc = new Sort(new Sort.Order(Sort.Direction.DESC, TIME_STAMP));
		Iterable<Notification> findAll = notificationRepository.findAll(sortByTSDesc);
		if (notificationRepository.count() > 0) {
			// Return all documents
			ArrayList<Notification> array = new ArrayList<Notification>();
			for (Notification notification : findAll) {
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

	@RequestMapping(value = "/clearAll", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public List<Notification> clearAll() {
		notificationRepository.deleteAll();
		return new ArrayList<Notification>();
	}
}
