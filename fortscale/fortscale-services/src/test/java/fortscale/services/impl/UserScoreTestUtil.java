package fortscale.services.impl;


import java.util.Date;

import org.joda.time.DateTime;

import fortscale.domain.core.ScoreInfo;

public class UserScoreTestUtil {
	
	public static ScoreInfo createScoreInfo(int year, int monthOfYear, int dayOfMonth, int hourOfDay, double avgScore, double score, double trend, double trendScore){
		DateTime dateTime = new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, 0);
		return createScoreInfo(dateTime.toDate(), avgScore, score, trend, trendScore);
	}
	
	public static ScoreInfo createScoreInfo(Date date, double avgScore, double score, double trend, double trendScore){
		ScoreInfo ret = new ScoreInfo();
		ret.setAvgScore(avgScore);
		ret.setScore(score);
		ret.setTimestamp(date);
		ret.setTimestampEpoc(date.getTime());
		ret.setTrend(trend);
		ret.setTrendScore(trendScore);
		
		return ret;
	}
}
