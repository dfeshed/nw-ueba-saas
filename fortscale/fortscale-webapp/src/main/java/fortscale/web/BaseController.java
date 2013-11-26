package fortscale.web;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.context.SecurityContextHolder;

import fortscale.domain.analyst.AnalystAuth;

public class BaseController {
	private static final String ME = "me";
	
	protected boolean isThisAnalystAuth() {
		return getAnalystAuth(ME) != null;
	}
	
	protected AnalystAuth getThisAnalystAuth() {
		return getAnalystAuth(ME);
	}
	
	protected AnalystAuth getAnalystAuth(String id) {
		AnalystAuth ret = null;
		if(id.equalsIgnoreCase(ME)) {
			if(SecurityContextHolder.getContext().getAuthentication() != null) {
				if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof AnalystAuth) {
					ret = (AnalystAuth) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
				}
			}
		}
		return ret;
	}
	
	protected Direction convertStringToDirection(String direction){
		Direction ret = Direction.DESC;
		if(!"desc".equalsIgnoreCase(direction)){
			ret = Direction.ASC;
		}
		
		return ret;
	}
}
