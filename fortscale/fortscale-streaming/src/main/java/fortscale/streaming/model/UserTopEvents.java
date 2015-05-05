package fortscale.streaming.model;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import fortscale.streaming.service.EventScore;

@JsonAutoDetect(fieldVisibility=Visibility.ANY, isGetterVisibility=Visibility.NONE, getterVisibility=Visibility.NONE, setterVisibility=Visibility.NONE)
public class UserTopEvents {
	
	private static final int MAX_NUM_OF_EVENTS_IN_LIST = 5;
	
	private static final double MILLIS_IN_DAY = 1000.0*60*60*24 ;
	
	private String eventType;
	private double lastUpdatedScore = 0;
	private long lastUpdateEpochTime = 0;
	private long lastUpdateScoreEpochTime = 0;
	private List<EventScore> eventScores = new ArrayList<EventScore>();
	private long latestRecievedEventEpochTime = 0;
	
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

	public long getLatestRecievedEventEpochTime() {
		return latestRecievedEventEpochTime;
	}

	public boolean updateEventScores(double score, long eventTimeInMillis) {
		if(eventTimeInMillis <= latestRecievedEventEpochTime){
			return false;
		}
		latestRecievedEventEpochTime = eventTimeInMillis;
		boolean isUpdated = false;
		if(eventScores.size() < MAX_NUM_OF_EVENTS_IN_LIST){
			eventScores.add(new EventScore(eventTimeInMillis, score));
			isUpdated = true;
		} else{
			double minScoreWithTimeDecay = timeDecayScore(eventScores.get(0).getScore(), eventScores.get(0).getEventTime(), eventTimeInMillis);
			int eventWithMinScoreIndex = 0;
			for(int i = 1; i < eventScores.size(); i++){
				double scoreWithTimeDecay = timeDecayScore(eventScores.get(i).getScore(), eventScores.get(i).getEventTime(), eventTimeInMillis);
				if(scoreWithTimeDecay < minScoreWithTimeDecay){
					minScoreWithTimeDecay = scoreWithTimeDecay;
					eventWithMinScoreIndex = i;
				}
			}
			if(minScoreWithTimeDecay < score){
				eventScores.set(eventWithMinScoreIndex, new EventScore(eventTimeInMillis, score));
				isUpdated = true;
			}
		}
		
		return isUpdated;
	}
	
	public double timeDecayScore(double score, long eventTimeInMillis, long currentTimeInMillis){
		// in order to maintain a fixed linear decay for all events, we reduce
		// 5 points from the score of an event for each passing day
		double decayVal = (currentTimeInMillis - eventTimeInMillis)*5.0 / (MILLIS_IN_DAY);
		return Math.max(0.0, score - decayVal);//Math.min( Math.exp( - ( System.currentTimeMillis() - eventTimeInMillis )/(MILLIS_IN_DAY*50.0) ), 1.0 );
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	
	public double calculateUserScore(long curTimeInMillis){
		if(eventScores.size() == 0){
			return 0;
		}
		double sum = 0;
		for(EventScore eventScore: eventScores){
			sum += timeDecayScore(eventScore.getScore(), eventScore.getEventTime(), curTimeInMillis);
		}
		
		return Math.round(sum / eventScores.size());
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
