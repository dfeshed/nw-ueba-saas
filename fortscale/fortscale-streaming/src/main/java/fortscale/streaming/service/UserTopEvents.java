package fortscale.streaming.service;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, isGetterVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class UserTopEvents {
	
	private static final int MAX_NUM_OF_EVENTS_IN_LIST = 5;
	
	private static final double MILLIS_IN_DAY = 1000.0*60*60*24 ;
	
	private String eventType;
	private double lastUpdatedScore = 0;
	private long lastUpdateEpochTime = 0;
	private long lastUpdateScoreEpochTime = 0;
	private List<EventScore> eventScores = new ArrayList<EventScore>();
	
	@JsonCreator
	public UserTopEvents(@JsonProperty("eventType") String eventType) {
		this.eventType = eventType;
	}

	public List<EventScore> getEventScores() {
		return eventScores;
	}
	
	public boolean isFull(){
		return eventScores.size() == MAX_NUM_OF_EVENTS_IN_LIST;
	}

	public boolean updateEventScores(double score, long eventTimeInMillis) {
		boolean isUpdated = false;
		if(eventScores.size() < MAX_NUM_OF_EVENTS_IN_LIST){
			eventScores.add(new EventScore(eventTimeInMillis, score));
			isUpdated = true;
		} else{
			double newEventScoreWithTimeDecay = timeDecayScore(score, eventTimeInMillis);
			double minScoreWithTimeDecay = timeDecayScore(eventScores.get(0).getScore(), eventScores.get(0).getEventTime());
			int eventWithMinScoreIndex = 0;
			for(int i = 1; i < eventScores.size(); i++){
				double scoreWithTimeDecay = timeDecayScore(eventScores.get(i).getScore(), eventScores.get(i).getEventTime());
				if(scoreWithTimeDecay < minScoreWithTimeDecay){
					minScoreWithTimeDecay = scoreWithTimeDecay;
					eventWithMinScoreIndex = i;
				}
			}
			if(minScoreWithTimeDecay < newEventScoreWithTimeDecay){
				eventScores.set(eventWithMinScoreIndex, new EventScore(eventTimeInMillis, score));
				isUpdated = true;
			}
		}
		
		return isUpdated;
	}
	
	public double timeDecayScore(double score, long eventTimeInMillis){
		return score * (1 - (System.currentTimeMillis() - eventTimeInMillis )/(MILLIS_IN_DAY*14.0));//Math.min( Math.exp( - ( System.currentTimeMillis() - eventTimeInMillis )/(MILLIS_IN_DAY*50.0) ), 1.0 );
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public double calculateUserScore(){
		if(eventScores.size() == 0){
			return 0;
		}
		double sum = 0;
		for(EventScore eventScore: eventScores){
			sum += timeDecayScore(eventScore.getScore(), eventScore.getEventTime());
		}
		
		return sum / eventScores.size();
	}

	public double getLastUpdatedScore() {
		return lastUpdatedScore;
	}

	public void setLastUpdatedScore(double lastUpdatedScore) {
		this.lastUpdatedScore = lastUpdatedScore;
	}

	public long getLastUpdateEpochTime() {
		return lastUpdateEpochTime;
	}

	public void setLastUpdateEpochTime(long lastUpdateEpochTime) {
		this.lastUpdateEpochTime = lastUpdateEpochTime;
	}

	public long getLastUpdateScoreEpochTime() {
		return lastUpdateScoreEpochTime;
	}

	public void setLastUpdateScoreEpochTime(long lastUpdateScoreEpochTime) {
		this.lastUpdateScoreEpochTime = lastUpdateScoreEpochTime;
	}
}
