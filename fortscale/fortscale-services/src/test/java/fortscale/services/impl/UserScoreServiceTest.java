package fortscale.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.ClassifierService;

public class UserScoreServiceTest {	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Mock
	private ClassifierService classifierService;
				
	@Mock
	private ConfigurationService configurationService; 
		
	@InjectMocks
	private UserScoreServiceImpl userScoreService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void getUserScoresHistoryTest1(){
		//Testing the case where the current day is a new year.
		String uid = "5";
		String classifierId = Classifier.groups.getId();
		
		ScoreInfo scoreInfo = null;
		scoreInfo = createScoreInfo(2014, 1, 1, 12, 23, 50, 10, 10);
		ClassifierScore classifierScore = new ClassifierScore(classifierId, scoreInfo);
		List<ScoreInfo> infos = new ArrayList<>();
		infos.add(scoreInfo);
		infos.add(createScoreInfo(2013, 12, 31, 12, 23, 50, 10, 10));
		classifierScore.setPrevScores(infos);
		
		User user = new User();
		user.putClassifierScore(classifierScore);
		Mockito.when(userRepository.findOne(uid)).thenReturn(user);
		List<IUserScoreHistoryElement> elements = userScoreService.getUserScoresHistory(uid, classifierId, 0, 10);
		
		Assert.assertEquals(infos.size(),elements.size());
	}
	
	@Test
	public void getUserScoresHistoryTest2(){
		//Testing that the last seven days are returned with the correct data.
		String uid = "5";
		String classifierId = Classifier.groups.getId();
		int offset = 0;
		int limit = 7;
		
		ScoreInfo scoreInfo = null;
		scoreInfo = createScoreInfo(2014, 5, 15, 2, 5, 10, 11, 12);
		ClassifierScore classifierScore = new ClassifierScore(classifierId, scoreInfo);
		List<ScoreInfo> infos = new ArrayList<>();
		infos.add(scoreInfo);
		infos.add(createScoreInfo(2014, 5, 14, 4, 10, 20, 16, 22));
		infos.add(createScoreInfo(2014, 5, 13, 5, 15, 30, 21, 32));
		infos.add(createScoreInfo(2014, 5, 12, 6, 20, 40, 26, 42));
		infos.add(createScoreInfo(2014, 5, 11, 7, 25, 50, 31, 52));
		infos.add(createScoreInfo(2014, 5, 10, 8, 30, 60, 36, 62));
		infos.add(createScoreInfo(2014, 5, 9, 9, 35, 70, 41, 72));
		infos.add(createScoreInfo(2014, 5, 8, 10, 40, 80, 46, 82));
		infos.add(createScoreInfo(2014, 5, 7, 11, 45, 90, 51, 92));
		infos.add(createScoreInfo(2014, 5, 6, 12, 50, 91, 56, 93));
		classifierScore.setPrevScores(infos);
		
		User user = new User();
		user.putClassifierScore(classifierScore);
		Mockito.when(userRepository.findOne(uid)).thenReturn(user);
		List<IUserScoreHistoryElement> elements = userScoreService.getUserScoresHistory(uid, classifierId, offset, limit);
		
		Assert.assertEquals(limit,elements.size());
		for(int i = 0; i < limit; i++){
			IUserScoreHistoryElement historyElement =  elements.get(i);
			ScoreInfo info =  infos.get(i);
			
			Assert.assertEquals(info.getTimestamp(), historyElement.getDate());
			Assert.assertEquals(info.getAvgScore(), historyElement.getAvgScore(), 0);
			Assert.assertEquals(info.getScore(), historyElement.getScore(), 0);
		}
	}
	
	private ScoreInfo createScoreInfo(int year, int monthOfYear, int dayOfMonth, int hourOfDay, double avgScore, double score, double trend, double trendScore){
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
