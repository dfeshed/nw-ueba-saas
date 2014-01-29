package fortscale.services.impl;

import org.joda.time.DateTime;

import fortscale.domain.core.ScoreInfo;

public class UserScoreTestUtil {
	
	public static ScoreInfo createScoreInfo(int year, int monthOfYear, int dayOfMonth, int hourOfDay, double avgScore, double score, double trend, double trendScore){
		ScoreInfo ret = new ScoreInfo();
		DateTime dateTime = new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, 0);
		ret.setAvgScore(avgScore);
		ret.setScore(score);
		ret.setTimestamp(dateTime.toDate());
		ret.setTimestampEpoc(dateTime.getMillis());
		ret.setTrend(trend);
		ret.setTrendScore(trendScore);
		
		return ret;
	}
}
