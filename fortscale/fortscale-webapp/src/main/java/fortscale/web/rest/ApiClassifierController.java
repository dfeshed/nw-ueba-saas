package fortscale.web.rest;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.services.exceptions.InvalidValueException;
import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.IClassifierScoreDistribution;
import fortscale.services.fe.ILoginEventScoreInfo;
import fortscale.services.fe.IScoreDistribution;
import fortscale.services.fe.ISuspiciousUserInfo;
import fortscale.services.fe.IVpnEventScoreInfo;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;





@Controller
@RequestMapping("/api/classifier/**")
public class ApiClassifierController {
	
	private static final String SUSPICIOUS_USERS_BY_SCORE = "score";
	private static final String SUSPICIOUS_USERS_BY_TREND = "trend";

	@Autowired
	private ClassifierService classifierService;
	
	
	
	
	
	@RequestMapping(value="/loginsEvents", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<ILoginEventScoreInfo>> loginsEvents(@RequestParam(required=false) Long date,
			@RequestParam(required=false) String uid,
			@RequestParam(defaultValue="0") Integer offset,
			@RequestParam(defaultValue="10") Integer limit,
			Model model){
		DataBean<List<ILoginEventScoreInfo>> ret = new DataBean<List<ILoginEventScoreInfo>>();
		Date timestamp = null;
		if(date != null){
			timestamp = new Date(date);
		}
		List<ILoginEventScoreInfo> eventScoreInfos = Collections.emptyList();
		int total = 0;
		if(uid == null){
			eventScoreInfos = classifierService.getSuspiciousLoginEvents(timestamp, offset, limit);
			total = classifierService.countLoginEvents(timestamp);
		} else{
			eventScoreInfos = classifierService.getUserSuspiciousLoginEvents(uid, timestamp, offset, limit);
			total = classifierService.countLoginEvents(uid, timestamp);
		}
		ret.setData(eventScoreInfos);
		ret.setOffset(offset);
		ret.setTotal(total);
		return ret;
	}
	
	@RequestMapping(value="/vpnEvents", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IVpnEventScoreInfo>> vpnEvents(@RequestParam(required=false) Long date,
			@RequestParam(required=false) String uid,
			@RequestParam(required=false) String query,
			@RequestParam(defaultValue="0") Integer offset,
			@RequestParam(defaultValue="10") Integer limit,
			Model model){
		DataBean<List<IVpnEventScoreInfo>> ret = new DataBean<List<IVpnEventScoreInfo>>();
		Date timestamp = null;
		if(date != null){
			timestamp = new Date(date);
		}
		List<IVpnEventScoreInfo> eventScoreInfos = Collections.emptyList();
		int total = 0;
		if(uid == null){
			eventScoreInfos = classifierService.getSuspiciousVpnEvents(timestamp, offset, limit);
			total = classifierService.countVpnEvents(timestamp);
		} else{
			eventScoreInfos = classifierService.getUserSuspiciousVpnEvents(uid, timestamp, offset, limit);
			total = classifierService.countVpnEvents(uid, timestamp);
		}
		ret.setData(eventScoreInfos);
		ret.setOffset(offset);
		ret.setTotal(total);
		return ret;
	}
	
	@RequestMapping(value="/{id}/dist", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IScoreDistribution>> classifierDist(@PathVariable String id, Model model){
		DataBean<List<IScoreDistribution>> ret = new DataBean<List<IScoreDistribution>>();
		List<IScoreDistribution> dists = classifierService.getScoreDistribution(id);
		ret.setData(dists);
		ret.setTotal(dists.size());
		return ret;
	}
	
//	@RequestMapping(value="/{id}/severity/{severityId}/users", method=RequestMethod.GET)
//	@ResponseBody
//	@LogException
//	public DataBean<List<ISuspiciousUserInfo>> severityUsers(@PathVariable String id, @PathVariable String severityId, Model model){
//		DataBean<List<ISuspiciousUserInfo>> ret = new DataBean<List<ISuspiciousUserInfo>>();
//		List<ISuspiciousUserInfo> users = classifierService.getSuspiciousUsers(id, severityId);
//		ret.setData(users);
//		ret.setTotal(users.size());
//		return ret;
//	}
	
	@RequestMapping(value="/{id}/suspiciousUser", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<ISuspiciousUserInfo>> suspiciousUser(@PathVariable String id, 
			@RequestParam(defaultValue=SUSPICIOUS_USERS_BY_SCORE) String sortby,
			@RequestParam(defaultValue="0") Integer page,
			@RequestParam(defaultValue="10") Integer size,
			Model model){
		DataBean<List<ISuspiciousUserInfo>> ret = new DataBean<List<ISuspiciousUserInfo>>();
		List<ISuspiciousUserInfo> users;
		int total = classifierService.countUsers(id);
		if(SUSPICIOUS_USERS_BY_SCORE.equals(sortby)){
			users = classifierService.getSuspiciousUsersByScore(id, null,page,size);
		} else if(SUSPICIOUS_USERS_BY_TREND.equals(sortby)){
			users = classifierService.getSuspiciousUsersByTrend(id, null,page,size);
		} else{
			throw new InvalidValueException(String.format("no such sorting field [%s]", sortby));
		}

		ret.setData(users);
		ret.setOffset(page*size);
		ret.setTotal(total);
		return ret;
	}
	
	@RequestMapping(value="/all/dist", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IClassifierScoreDistribution>> dist(Model model){
		DataBean<List<IClassifierScoreDistribution>> ret = new DataBean<List<IClassifierScoreDistribution>>();
		List<IClassifierScoreDistribution> dists = classifierService.getScoreDistribution();
		ret.setData(dists);
		ret.setTotal(dists.size());
		return ret;
	}
	
}
