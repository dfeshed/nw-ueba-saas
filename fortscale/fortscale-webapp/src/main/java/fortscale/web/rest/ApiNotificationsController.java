package fortscale.web.rest;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.core.Notification;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;

@Controller
@RequestMapping("/api/notifications/**")
public class ApiNotificationsController {

	@Autowired
	private NotificationsRepository notificationRepository;

	@RequestMapping(value = "/", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> index() {
		DataBean<List<Notification>> ret = new DataBean<List<Notification>>();

		Iterable<Notification> findAll = notificationRepository.findAll();
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
