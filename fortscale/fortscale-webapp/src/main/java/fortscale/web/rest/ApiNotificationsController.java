package fortscale.web.rest;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.common.base.Optional;

import fortscale.domain.analyst.Analyst;
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

	@RequestMapping(value = "/byUser/{fsid:.+}", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> userNotifications(@PathVariable("fsid") String fsid,
			@RequestParam(defaultValue="False") boolean includeDissmissed) {
		Iterable<Notification> userNotifications = notificationsRepository.findByFsIdExcludeComments(fsid, includeDissmissed);
		return notificationsDataSingle(userNotifications, Optional.<Long>absent());
	}

	@RequestMapping(value="/list", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public ResponseEntity<DataBean<List<Notification>>> list(
			@RequestParam(defaultValue="0", required=false) int page,
			@RequestParam(defaultValue="20", required=false) int size,
			@RequestParam(required=false) final List<String> includeFsIds,
			@RequestParam(required=false) final List<String> excludeFsIds,
			@RequestParam(defaultValue="True") boolean includeDissmissed,
			@RequestParam(required=false) final List<String> includeGenerators,
			@RequestParam(required=false) final List<String> excludeGenerators,
			@RequestParam(required=false, defaultValue="0") long after,
			@RequestParam(required=false, defaultValue="0") long before,
			@RequestParam(defaultValue="True") boolean sortDesc) {
		
		// calculate the page request based on the parameters given
		PageRequest request = new PageRequest(page, size, 
				sortDesc ? Direction.DESC : Direction.ASC, TIME_STAMP);
		
		// ensure we didn't get both include and exclude fsid lists
		if (includeFsIds!=null && !includeFsIds.isEmpty() &&
			excludeFsIds!=null && !excludeFsIds.isEmpty())
			return new ResponseEntity<DataBean<List<Notification>>>(HttpStatus.BAD_REQUEST);
		
		// ensure we didn't get both include and exclude generators lists
		if (includeGenerators!=null && !includeGenerators.isEmpty() &&
			excludeGenerators!=null && !excludeGenerators.isEmpty()) 
			return new ResponseEntity<DataBean<List<Notification>>>(HttpStatus.BAD_REQUEST);
		
		Page<Notification> notifications = notificationsRepository.findByPredicates(includeFsIds, excludeFsIds, includeDissmissed,
				includeGenerators, excludeGenerators, before, after, request);
		DataBean<List<Notification>> value = notificationsDataSingle(notifications.getContent(), Optional.of(notifications.getTotalElements()));
		return new ResponseEntity<DataBean<List<Notification>>>(value, HttpStatus.OK);
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
	public void dismissNotification(@PathVariable("id") String id) {
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
	public void undismissNotification(@PathVariable("id") String id) {
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
	@RequestMapping(value = "/comment/{id:.+}")
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> commentOnNotification(@PathVariable("id") String id, 
			@RequestParam(value="message", required=true) String message,
			@RequestParam(value="basedOn", required=false) Long basedOnID) {
		
		Notification notification = notificationsRepository.findOne(id);		
		if (notification!=null) {
			// get the current user
			AnalystAuth analystAuth = getThisAnalystAuth();
			Analyst analyst = (analystAuth!=null)? analystService.findByUsername(analystAuth.getUsername()) : null;
			String analystId = (analyst!=null)? analyst.getId() : null;
			String dispalyName = (analyst!=null)? analyst.getFirstName() + " " + analyst.getLastName() : null;
			
			// add new comment to notification
			notification.addComment(new NotificationComment(analystId, dispalyName, new Date(), message, basedOnID));
			notification = notificationsRepository.save(notification);
		}
		
		List<Notification> notifications = new ArrayList<Notification>(1);
		notifications.add(notification);
		return notificationsDataSingle(notifications, Optional.<Long>absent());
	}
	
	/**
	 * Get a specific notification with the comments
	 */
	@RequestMapping(value = "/get/{id:.+}")
	@ResponseBody
	@LogException
	public DataBean<List<Notification>> getNotification(@PathVariable("id") String id) {
		
		List<Notification> notifications = new ArrayList<Notification>(1);
		notifications.add(notificationsRepository.findOne(id));
		return notificationsDataSingle(notifications, Optional.<Long>absent());
	}
	
	
	@RequestMapping(value = "/clearAll", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public List<Notification> clearAll() {
		notificationsRepository.deleteAll();
		return new ArrayList<Notification>();
	}
}
