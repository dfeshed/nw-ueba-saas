package fortscale.web;

import java.util.Collections;
import java.util.List;

import org.springframework.data.domain.Sort.Direction;
import org.springframework.security.core.context.SecurityContextHolder;

import fortscale.domain.analyst.AnalystAuth;
import fortscale.web.beans.DataBean;

public class BaseController {
	private static final String ME = "me";
	protected static final DataBean<List<?>> emptyData = getEmptyData();
	
	private static DataBean<List<?>> getEmptyData(){
		DataBean<List<?>> ret = new DataBean<List<?>>();
		ret.setData(Collections.emptyList());
		ret.setOffset(0);
		ret.setTotal(0);
		
		return ret;
	}
	
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
