package fortscale.domain.fe.dao;

import java.util.Date;
import java.util.List;

import fortscale.domain.fe.AdUserFeaturesExtraction;

interface AdUsersFeaturesExtractionRepositoryCustom {
	public void saveMap(AdUserFeaturesExtraction adUsersFeaturesExtraction);
	public Double calculateAvgScore(String classifierId, Date timestamp);
//	public Double calculateUsersDailyMaxScores(String classifierId, String userId);
	public List<Threshold> calculateNumOfUsersWithScoresGTThresholdForLastRun(String classifierId,List<Threshold> thresholds);
	public Date getLatestTimeStamp();
	public AdUserFeaturesExtraction getClassifierIdAndByUserIdAndTimestamp(String classifierId, String userId, Date timestamp);
	public List<Date> getDistinctRuntime(String classifierId);
}
