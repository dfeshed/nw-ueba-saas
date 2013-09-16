package fortscale.web.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.IClassifierScoreDistribution;
import fortscale.services.fe.IScoreDistribution;
import fortscale.services.fe.ISuspiciousUserInfo;
import fortscale.utils.logging.annotation.LogException;
import fortscale.web.beans.DataBean;





@Controller
@RequestMapping("/api/classifier/**")
public class ApiClassifierController {

	@Autowired
	private ClassifierService classifierService;
	
	
	
	@RequestMapping(value="dist", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IClassifierScoreDistribution>> dist(Model model){
		DataBean<List<IClassifierScoreDistribution>> ret = new DataBean<List<IClassifierScoreDistribution>>();
		List<IClassifierScoreDistribution> dists = classifierService.getScoreDistribution();
		ret.setData(dists);
		ret.setTotal(dists.size());
		return ret;
	}
	
	@RequestMapping(value="{id}/dist", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<IScoreDistribution>> classifierDist(@PathVariable String id, Model model){
		DataBean<List<IScoreDistribution>> ret = new DataBean<List<IScoreDistribution>>();
		List<IScoreDistribution> dists = classifierService.getScoreDistribution(id);
		ret.setData(dists);
		ret.setTotal(dists.size());
		return ret;
	}
	
	@RequestMapping(value="{id}/severity/{severityId}/users", method=RequestMethod.GET)
	@ResponseBody
	@LogException
	public DataBean<List<ISuspiciousUserInfo>> users(@PathVariable String id, @PathVariable String severityId, Model model){
		DataBean<List<ISuspiciousUserInfo>> ret = new DataBean<List<ISuspiciousUserInfo>>();
		List<ISuspiciousUserInfo> users = classifierService.getSuspiciousUsers(id, severityId);
		ret.setData(users);
		ret.setTotal(users.size());
		return ret;
	}
	
}
