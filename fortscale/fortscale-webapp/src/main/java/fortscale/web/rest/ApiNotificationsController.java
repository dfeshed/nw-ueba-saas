package fortscale.web.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Optional;

import fortscale.domain.analyst.AnalystAuth;
import fortscale.domain.core.Notification;
import fortscale.domain.core.NotificationAggregate;
import fortscale.domain.core.NotificationComment;
import fortscale.domain.core.NotificationResource;
import fortscale.domain.core.dao.NotificationResourcesRepository;
import fortscale.domain.core.dao.NotificationsRepository;
import fortscale.services.analyst.AnalystService;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;

@Controller
@RequestMapping("/api/notifications")
public class ApiNotificationsController extends BaseController {

	private static final String TIME_STAMP = "ts";

	@Autowired
	private NotificationsRepository notificationsRepository;

	@Autowired
	private AnalystService analystService;
	
	@Autowired
	private NotificationResourcesRepository notificationResourcesRepository;

	private DataBean<List<Notification>> notificationsDataSingle(
			Iterable<Notification> userNotifications, Optional<Long> total) {
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
			if (total.isPresent())
				ret.setTotal(total.get().intValue());
			else
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
		Iterable<Notification> userNotifications = notificationsRepository.findByFsIdExcludeComments(fsid);
		return notificationsDataSingle(userNotifications, Optional.<Long>absent());
	}

	@RequestMapping(method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> list(
			@RequestParam(defaultValue="0", required=false) int page,
			@RequestParam(defaultValue="20", required=false) int size,
			@RequestParam(required=false) final List<String> includeUsers,
			@RequestParam(required=false) final List<String> excludeUsers,
			@RequestParam(required=false) final List<String> includeHostnames,
			@RequestParam(required=false) final List<String> excludeHostnames,
			@RequestParam(defaultValue="True", required=false) boolean includeDissmissed,
			@RequestParam(required=false) final List<String> includeGenerators,
			@RequestParam(required=false) final List<String> excludeGenerators,
			@RequestParam(required=false) Date after,
			@RequestParam(required=false) Date before,
			@RequestParam(defaultValue="True", required=false) boolean sortDesc) {
		
		// calculate the page request based on the parameters given
		PageRequest request = new PageRequest(page, size, 
				sortDesc ? Direction.DESC : Direction.ASC, TIME_STAMP);
		
		// calculate filter parameters 
		Set<String> includeFsID = new HashSet<String>();
		if (includeUsers!=null)
			includeFsID.addAll(includeUsers);
		if (includeHostnames!=null)
			includeFsID.addAll(includeHostnames);
		if (excludeUsers!=null)
			includeFsID.removeAll(excludeUsers);
		if (excludeHostnames!=null)
			includeFsID.removeAll(excludeHostnames);
		
		Set<String> excludeFsID = new HashSet<String>();
		if (excludeUsers!=null)
			excludeFsID.addAll(excludeUsers);
		if (excludeHostnames!=null)
			excludeFsID.addAll(excludeHostnames);
		
		Set<String> includeGeneratorsSet = new HashSet<String>();
		if (includeGenerators!=null)
			includeGeneratorsSet.addAll(includeGenerators);
		if (excludeGenerators!=null)
			includeGeneratorsSet.removeAll(excludeGenerators);
		
		Set<String> excludeGeneratorsSet = new HashSet<String>();
		if (excludeGenerators!=null)
			excludeGeneratorsSet.addAll(excludeGenerators);
			
		
		Page<Notification> notifications = notificationsRepository.findByPredicates(includeFsID, excludeFsID, includeDissmissed,
				includeGeneratorsSet, excludeGeneratorsSet, before, after, request);
		return notificationsDataSingle(notifications.getContent(), Optional.of(notifications.getTotalElements()));
	}

	
	/***
	 * Gets notification after a given time stamp
	 * @return list of matching notification, empty list if no notification found
	 */
	@RequestMapping(value = "/after/{ts}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> after(@PathVariable("ts") long ts) {
		
		Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, TIME_STAMP));
		
		// pass the time stamp and paging to the repository to perform the query
		Iterable<Notification> notifications = notificationsRepository.findByTsGreaterThanExcludeComments(ts, sort);
		return notificationsDataSingle(notifications,Optional.<Long>absent());
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


	/**
	 * Mark notification as dismissed
	 */
	@RequestMapping(value = "/dismiss/{id}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public void dismissNotification(@PathVariable("id") Long id) {
		Notification notification = notificationsRepository.findOne(id);
		if (notification!=null) {
			if (!notification.isDismissed()) {
				notification.setDismissed(true);
				notificationsRepository.save(notification);
			}
		}
	}
	
	/**
	 * Mark notification as un-dismissed
	 */
	@RequestMapping(value = "/undismiss/{id}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public void undismissNotification(@PathVariable("id") Long id) {
		Notification notification = notificationsRepository.findOne(id);
		if (notification!=null) {
			if (notification.isDismissed()) {
				notification.setDismissed(false);
				notificationsRepository.save(notification);
			}
		}
	}
	
	/**
	 * Adds a comment to a notification, setting the current user and time
	 */
	@RequestMapping(value = "/{id:.+}/comment")
	@ResponseBody
	@LogException
	public Notification commentOnNotification(@PathVariable("id") Long id, 
			@RequestParam(value="message", required=true) String message,
			@RequestParam(value="basedOn", required=false) Long basedOnID) {
		
		Notification notification = notificationsRepository.findOne(id);		
		if (notification!=null) {
			// get the current user
			AnalystAuth analyst = getThisAnalystAuth();
			String username = (analyst!=null)? analyst.getUsername() : null;
			String dispalyName = analystService.getAnalystDisplayName(username);
			
			// add new comment to notification
			notification.addComment(new NotificationComment(username, dispalyName, new Date(), message, basedOnID));
			notification = notificationsRepository.save(notification);
		}
		return notification;
	}
	
	
	@RequestMapping(value = "/clearAll", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public List<Notification> clearAll() {
		notificationsRepository.deleteAll();
		return new ArrayList<Notification>();
	}
}
