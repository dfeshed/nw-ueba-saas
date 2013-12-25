package fortscale.domain.fe.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import fortscale.domain.fe.AdUserFeaturesExtraction;

interface AdUsersFeaturesExtractionRepositoryCustom {
	public void saveMap(AdUserFeaturesExtraction adUsersFeaturesExtraction);
	public Double calculateAvgScore(String classifierId, Date timestamp);
//	public Double calculateUsersDailyMaxScores(String classifierId, String userId);
	public List<Threshold> calculateNumOfUsersWithScoresGTThresholdForLastRun(String classifierId,List<Threshold> thresholds);
	public Date getLatestTimeStamp();
	public List<Date> getDistinctRuntime(String classifierId);
	public AdUserFeaturesExtraction findByClassifierIdAndUserIdAndTimestamp(String classifierId, String userId, Date timestamp);
	public List<AdUserFeaturesExtraction> findByClassifierIdAndTimestampAndUserIds(String classifierId, Date timestamp, Collection<String> userIds);
	public long countByClassifierIdAndTimestamp(String classifierId, Date timestamp);
}
