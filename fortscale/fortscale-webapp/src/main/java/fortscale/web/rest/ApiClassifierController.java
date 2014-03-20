package fortscale.web.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.services.LogEventsEnum;
import fortscale.services.exceptions.InvalidValueException;
import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.IClassifierScoreDistribution;
import fortscale.services.fe.ILoginEventScoreInfo;
import fortscale.services.fe.IScoreDistribution;
import fortscale.services.fe.ISuspiciousUserInfo;
import fortscale.services.fe.IVpnEventScoreInfo;
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
	
	@RequestMapping(value="/loginsEvents", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<?>> loginsEvents(@RequestParam(required=false) Long date,
			@RequestParam(required=false) String uid,
			@RequestParam(defaultValue="0") Integer offset,
			@RequestParam(defaultValue="10") Integer limit,
			@RequestParam(required=false) String orderBy,
			@RequestParam(defaultValue="DESC") String orderByDirection,
			@RequestParam(defaultValue="0") Integer minScore,
			Model model){
		
		return events(LogEventsEnum.login, date, uid, offset, limit, orderBy, orderByDirection, minScore, false, model);
	}

	@RequestMapping(value = "/{id}/events", method = RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<?>> events(@PathVariable LogEventsEnum id, @RequestParam(required = false) Long date, @RequestParam(required = false) String uid, @RequestParam(defaultValue = "0") Integer offset,
			@RequestParam(defaultValue = "10") Integer limit, @RequestParam(required = false) String orderBy, @RequestParam(defaultValue = "DESC") String orderByDirection,
			@RequestParam(defaultValue = "0") Integer minScore, @RequestParam(defaultValue = "false") Boolean followedOnly, Model model) {
		Date timestamp = null;
		if (date != null) {
			timestamp = new Date(date);
		}

		Direction direction = convertStringToDirection(orderByDirection);

		DataBean<List<?>> ret = new DataBean<List<?>>();
		ret.setTotal(0);

		switch (id) {
		case ssh:
		case login:
			ret = authEvents(id, timestamp, uid, offset, limit, orderBy, direction, minScore, followedOnly);
			break;
		case vpn:
			ret = vpnEvents(id, timestamp, uid, offset, limit, orderBy, direction, minScore, followedOnly);
		default:
			break;
		}

		return ret;
	}

	private DataBean<List<?>> authEvents(LogEventsEnum id, Date timestamp, String uid, Integer offset, Integer limit, String orderBy, Direction direction, Integer minScore, boolean followedOnly) {
		DataBean<List<?>> ret = new DataBean<List<?>>();

		List<ILoginEventScoreInfo> eventScoreInfos = Collections.emptyList();
		int total = 0;
		if (uid == null) {
			eventScoreInfos = classifierService.getSuspiciousAuthEvents(id, timestamp, offset, limit, orderBy, direction, minScore, followedOnly);
			total = classifierService.countAuthEvents(id, timestamp);
		} else {
			eventScoreInfos = classifierService.getUserSuspiciousAuthEvents(id, uid, timestamp, offset, limit, orderBy, direction, minScore);
			total = classifierService.countAuthEvents(id, uid, timestamp);
		}
		
		List<Map<String, Object>> data = new ArrayList<>();
		for(ILoginEventScoreInfo eventScoreInfo: eventScoreInfos){
			data.add(eventScoreInfo.createMap());
		}
		ret.setData(data);
		ret.setOffset(offset);
		ret.setTotal(total);
		return ret;
	}

	private DataBean<List<?>> vpnEvents(LogEventsEnum id, Date timestamp, String uid, Integer offset, Integer limit, String orderBy, Direction direction, Integer minScore, boolean followedOnly) {
		DataBean<List<?>> ret = new DataBean<List<?>>();

		List<IVpnEventScoreInfo> eventScoreInfos = Collections.emptyList();
		int total = 0;
		if (uid == null) {
			eventScoreInfos = classifierService.getSuspiciousVpnEvents(timestamp, offset, limit, orderBy, direction, minScore, followedOnly);
			total = classifierService.countVpnEvents(timestamp);
		} else {
			eventScoreInfos = classifierService.getUserSuspiciousVpnEvents(uid, timestamp, offset, limit, orderBy, direction, minScore);
			total = classifierService.countVpnEvents(uid, timestamp);
		}
		List<Map<String, Object>> data = new ArrayList<>();
		for(IVpnEventScoreInfo eventScoreInfo: eventScoreInfos){
			data.add(eventScoreInfo.createMap());
		}
		ret.setData(data);
		ret.setOffset(offset);
		ret.setTotal(total);
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
			@RequestParam(defaultValue = "10") Integer size, @RequestParam(defaultValue = "false") Boolean followedOnly, Model model) {
		DataBean<List<ISuspiciousUserInfo>> ret = new DataBean<List<ISuspiciousUserInfo>>();
		List<ISuspiciousUserInfo> users;
		int total = classifierService.countUsers(id);
		if (SUSPICIOUS_USERS_BY_SCORE.equals(sortby)) {
			users = classifierService.getSuspiciousUsersByScore(id, null, page, size,followedOnly);
		} else if (SUSPICIOUS_USERS_BY_TREND.equals(sortby)) {
			users = classifierService.getSuspiciousUsersByTrend(id, null, page, size,followedOnly);
		} else {
			throw new InvalidValueException(String.format("no such sorting field [%s]", sortby));
		}

		ret.setData(users);
		ret.setOffset(page * size);
		ret.setTotal(total);
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
