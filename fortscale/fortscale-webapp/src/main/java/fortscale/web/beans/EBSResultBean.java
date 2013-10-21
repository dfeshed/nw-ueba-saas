package fortscale.web.beans;

import java.util.List;
import java.util.Map;

import fortscale.services.fe.EBSResult;

public class EBSResultBean {
	private final EBSResult ebsResult;
	
	public EBSResultBean(EBSResult ebsResult) {
		this.ebsResult = ebsResult;
	}
	
	public List<Map<String, Object>> getResultsList() {
		return ebsResult.getResultsList();
	}

	public Double getGlobalScore() {
		return ebsResult.getGlobalScore();
	}
}
