package fortscale.domain.core;

import java.util.Collections;
import java.util.List;

public class ClassifierScore extends ScoreInfo{
	public static int MAX_NUM_OF_PREV_SCORES = 14;

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
		if(prevScores.size() > MAX_NUM_OF_PREV_SCORES){
			prevScores.subList(0, MAX_NUM_OF_PREV_SCORES);
		}
		this.prevScores = prevScores;
	}
	
	
}
