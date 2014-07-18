package fortscale.web.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.domain.events.LogEventsEnum;
import fortscale.domain.fe.EventScore;
import fortscale.domain.fe.dao.EmptyTableException;
import fortscale.domain.fe.dao.EventLoginDayCount;
import fortscale.services.exceptions.InvalidValueException;
import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.IClassifierScoreDistribution;
import fortscale.services.fe.IScoreDistribution;
import fortscale.services.fe.ISuspiciousUserInfo;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.BaseController;
import fortscale.web.beans.DataBean;

@Controller
@RequestMapping("/api/classifier/**")
public class ApiClassifierController extends BaseController {

	private static final String SUSPICIOUS_USERS_BY_SCORE = "score";
	private static final String SUSPICIOUS_USERS_BY_TREND = "trend";

	@Autowired
	private ClassifierService classifierService;
	
	
	@RequestMapping(value="/eventsTimeline", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<EventScore>> eventsTimeline(
			@RequestParam(required = true) String username, 
			@RequestParam(defaultValue="31") int daysBack,
			@RequestParam(defaultValue="200") int limit, 
			@RequestParam(defaultValue="vpn,login,ssh") List<LogEventsEnum> eventTypes) {
	
		List<EventScore> events = classifierService.getEventScores(eventTypes, username, daysBack, limit);
		
		DataBean<List<EventScore>> ret = new DataBean<>();
		ret.setData(events);
		ret.setTotal(events.size());
		return ret;
	}
	
	@RequestMapping(value = "/{id}/eventsLoginCount", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<EventLoginDayCount>> eventsLoginCount(@PathVariable LogEventsEnum id, @RequestParam(required = true) String username, @RequestParam(defaultValue = "7") Integer numberOfDays) {
		DataBean<List<EventLoginDayCount>> ret = new DataBean<>();
		List<EventLoginDayCount> eventLoginDayCounts = classifierService.getEventLoginDayCount(id, username, numberOfDays);
		
		ret.setData(eventLoginDayCounts);
		ret.setTotal(eventLoginDayCounts.size());
		
		return ret;
	}

	@RequestMapping(value = "/{id}/events", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<?>> events(@PathVariable LogEventsEnum id, @RequestParam(required = false) Long latestDate, @RequestParam(required = false) Long earliestDate, @RequestParam(required = false) String uid, @RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String orderBy, @RequestParam(defaultValue = "DESC") String orderByDirection,
			@RequestParam(defaultValue = "0") Integer minScore, @RequestParam(defaultValue = "false") Boolean followedOnly, Model model) {
		Direction direction = convertStringToDirection(orderByDirection);

		DataBean<List<?>> ret = null;
		
		if(latestDate == null){
			latestDate = System.currentTimeMillis();
		}
		
		if(earliestDate == null){
			earliestDate = (new DateTime(latestDate)).minusDays(14).getMillis();
		}

		try{
			ret = new DataBean<List<?>>();
			List<Map<String, Object>> eventScoreInfos = Collections.emptyList();
			int total = 0;
			if (uid == null) {
				eventScoreInfos = classifierService.getSuspiciousAuthEvents(id, latestDate, earliestDate, offset, limit, orderBy, direction, minScore, followedOnly);
				total = classifierService.countAuthEvents(id, latestDate, earliestDate, minScore, followedOnly);
			} else {
				eventScoreInfos = classifierService.getUserSuspiciousAuthEvents(id, latestDate, earliestDate, uid, offset, limit, orderBy, direction, minScore);
				total = classifierService.countAuthEvents(id, latestDate, earliestDate, uid, minScore);
			}
			
			ret.setData(eventScoreInfos);
			ret.setOffset(offset);
			ret.setTotal(total);
		} catch(EmptyTableException e){
			return emptyData;
		}

		return ret;
	}
	

	@RequestMapping(value = "/{id}/dist", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IScoreDistribution>> classifierDist(@PathVariable String id, Model model) {
		DataBean<List<IScoreDistribution>> ret = new DataBean<List<IScoreDistribution>>();
		List<IScoreDistribution> dists = classifierService.getScoreDistribution(id);
		ret.setData(dists);
		ret.setTotal(dists.size());
		return ret;
	}

	// @RequestMapping(value="/{id}/severity/{severityId}/users",
	// method=RequestMethod.GET)
	// @ResponseBody
	// @LogException
	// public DataBean<List<ISuspiciousUserInfo>> severityUsers(@PathVariable
	// String id, @PathVariable String severityId, Model model){
	// DataBean<List<ISuspiciousUserInfo>> ret = new
	// DataBean<List<ISuspiciousUserInfo>>();
	// List<ISuspiciousUserInfo> users =
	// classifierService.getSuspiciousUsers(id, severityId);
	// ret.setData(users);
	// ret.setTotal(users.size());
	// return ret;
	// }

	@RequestMapping(value = "/{id}/suspiciousUser", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<ISuspiciousUserInfo>> suspiciousUser(@PathVariable String id, @RequestParam(defaultValue = SUSPICIOUS_USERS_BY_SCORE) String sortby, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "80") Integer minScore, @RequestParam(defaultValue = "100") Integer maxScore, @RequestParam(defaultValue = "false") Boolean followedOnly, Model model) {
		DataBean<List<ISuspiciousUserInfo>> ret = new DataBean<List<ISuspiciousUserInfo>>();
		Page<ISuspiciousUserInfo> users;
		if (SUSPICIOUS_USERS_BY_SCORE.equals(sortby)) {
			users = classifierService.getSuspiciousUsersByScore(id, page, size, minScore, maxScore, followedOnly);
		} else if (SUSPICIOUS_USERS_BY_TREND.equals(sortby)) {
			users = classifierService.getSuspiciousUsersByTrend(id, page, size, minScore, maxScore, followedOnly);
		} else {
			throw new InvalidValueException(String.format("no such sorting field [%s]", sortby));
		}

		ret.setData(users.getContent());
		ret.setOffset(users.getNumber()*users.getSize());
		ret.setTotal((int) users.getTotalElements());
		return ret;
	}
	
	@RequestMapping(value = "/suspiciousUsers", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<ISuspiciousUserInfo>> suspiciousUsers(@RequestParam(required=true) List<String> classifierIds, @RequestParam(defaultValue = SUSPICIOUS_USERS_BY_SCORE) String sortby, @RequestParam(defaultValue = "0") Integer page,
			@RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "80") Integer minScore, @RequestParam(defaultValue = "100") Integer maxScore, @RequestParam(defaultValue = "false") Boolean followedOnly, Model model) {
		DataBean<List<ISuspiciousUserInfo>> ret = new DataBean<List<ISuspiciousUserInfo>>();
		
		int fromIndex = page*size;
		int toIndex = fromIndex + size;
		List<ISuspiciousUserInfo> content = new ArrayList<>();
		
		long total = 0;
		for(String id: classifierIds){
			Page<ISuspiciousUserInfo> users;
			if (SUSPICIOUS_USERS_BY_SCORE.equals(sortby)) {
				users = classifierService.getSuspiciousUsersByScore(id, 0, toIndex, minScore, maxScore, followedOnly);
			} else if (SUSPICIOUS_USERS_BY_TREND.equals(sortby)) {
				users = classifierService.getSuspiciousUsersByTrend(id, 0, toIndex, minScore, maxScore, followedOnly);
			} else {
				throw new InvalidValueException(String.format("no such sorting field [%s]", sortby));
			}
			content.addAll(users.getContent());
			total += users.getTotalElements();
		}

		Collections.sort(content, new ISuspiciousUserInfo.OrderByTrendDesc());
		
		if(toIndex > content.size()){
			ret.setData(content);
		} else{
			ret.setData(content.subList(fromIndex, toIndex));
		}
		
		ret.setOffset(fromIndex);
		ret.setTotal((int) total);
		return ret;
	}

	@RequestMapping(value = "/all/dist", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IClassifierScoreDistribution>> dist(Model model) {
		DataBean<List<IClassifierScoreDistribution>> ret = new DataBean<List<IClassifierScoreDistribution>>();
		List<IClassifierScoreDistribution> dists = classifierService.getScoreDistribution();
		ret.setData(dists);
		ret.setTotal(dists.size());
		return ret;
	}

}
