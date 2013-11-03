package fortscale.domain.core;

import java.util.Collections;
import java.util.List;

public class ClassifierScore extends ScoreInfo{
	

	private String classifierId;
	private List<ScoreInfo> prevScores;
	
	
	
	public String getClassifierId() {
		return classifierId;
	}
	public void setClassifierId(String classifierId) {
		this.classifierId = classifierId;
	}
	public List<ScoreInfo> getPrevScores() {
		return (List<ScoreInfo>) (prevScores != null ? prevScores : Collections.emptyList());
	}
	public void setPrevScores(List<ScoreInfo> prevScores) {
		this.prevScores = prevScores;
	}
	
	
}
