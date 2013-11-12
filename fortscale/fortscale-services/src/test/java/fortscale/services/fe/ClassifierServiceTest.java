package fortscale.services.fe;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.Threshold;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.domain.AbstractTest;
import fortscale.services.fe.Classifier;
import fortscale.services.fe.ClassifierService;
import fortscale.services.fe.IScoreDistribution;

public class ClassifierServiceTest extends AbstractTest{
	
	@Autowired
	private ClassifierService service;
	
	@Autowired
	private AuthDAO authDAO;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Autowired
	private ConfigurationService configurationService;
	
	

	@Test
	public void testGetScoreDistributionForAuth() {
		testGetScoreDistribution(Classifier.auth.getId());
	}
	
	@Test
	public void testGetScoreDistributionForAd() {
		testGetScoreDistribution(Classifier.groups.getId());
	}
	
	private void testGetScoreDistribution(String classifierId){
		List<IScoreDistribution> scoreDistributions = service.getScoreDistribution(classifierId);
		
		Date tmpDate = new Date();
		int total = authDAO.countNumOfUsersAboveThreshold(new Threshold("", 0), tmpDate);
		for(IScoreDistribution scoreDistribution: scoreDistributions) {
			int aboveThreshold = authDAO.countNumOfUsersAboveThreshold(new Threshold("", scoreDistribution.getLowestScore()), tmpDate);
			int aboveThreshold1 = authDAO.countNumOfUsersAboveThreshold(new Threshold("", scoreDistribution.getHighestScore()), tmpDate);
			int count = (aboveThreshold - aboveThreshold1);
			double per = (count / (double)total)*100;
			Assert.assertTrue(Math.abs(scoreDistribution.getPercentage() - per) < 1);
			Assert.assertEquals(count, scoreDistribution.getCount());
		}
	}
	
	@Test
	public void testgetGroupSuspiciousUsers() {
		User user = createUser("test1", 90, 80, 90, 80);
		userRepository.save(user);
		adUsersFeaturesExtractionRepository.save(createAdUserFeaturesExtractionDummy(user.getId(), user.getAdDn()));
		user = createUser("test2", 90, 80, 0, 0);
		userRepository.save(user);
		adUsersFeaturesExtractionRepository.save(createAdUserFeaturesExtractionDummy(user.getId(), user.getAdDn()));
		user = createUser("test3", 90, 80, 100, 100);
		userRepository.save(user);
		adUsersFeaturesExtractionRepository.save(createAdUserFeaturesExtractionDummy(user.getId(), user.getAdDn()));
		service.getSuspiciousUsersByScore(Classifier.groups.getId(), configurationService.getSeverityElements().get(0).getName());
		
	}
	
	private TestUser createUser(String id) {
		TestUser retUser = new TestUser(id + "-dn");
		retUser.setId(id);
//		ClassifierScore classifierScoreAd = createClassifierScoreDummy(Classifier.groups.getId());
//		ClassifierScore classifierScoreAuth = createClassifierScoreDummy(Classifier.auth.getId());
		return retUser;
	}
	
	private TestUser createUser(String id, int score, int avgScore, int prevScore, int prevAvgScore) {
		TestUser user = createUser(id);
		user.putClassifierScore(createClassifierScore(Classifier.groups.getId(), score, avgScore, createPrevScoreInfoList(prevScore, prevAvgScore)));
		user.putClassifierScore(createClassifierScore(Classifier.auth.getId(), score, avgScore, createPrevScoreInfoList(prevScore, prevAvgScore)));
		return user;
	}
	
	private TestUser createUserWithClassifierScore(String id) {
		TestUser retUser = createUser(id);
		retUser.putClassifierScore(createClassifierScoreDummy(Classifier.groups.getId()));
		retUser.putClassifierScore(createClassifierScoreDummy(Classifier.auth.getId()));
		return retUser;
	}
	
	private ClassifierScore createClassifierScoreDummy(String classifierId) {
		return createClassifierScore(classifierId, 90, 80, createPrevScoreInfoList(100, 0));
	}
	
	private ClassifierScore createClassifierScore(String classifierId, int score, int avgScore, List<ScoreInfo> prevScores) {
		ClassifierScore classifierScore = new ClassifierScore();
		classifierScore.setScore(score);
		classifierScore.setAvgScore(avgScore);
		classifierScore.setTimestamp(new Date());
		classifierScore.setPrevScores(prevScores);
		classifierScore.setClassifierId(classifierId);
		return classifierScore;
	}
	
	private List<ScoreInfo> createPrevScoreInfoList(int score, int avgScore){
		List<ScoreInfo> prevScores = new ArrayList<>();
		ScoreInfo prevScoreInfo = new ScoreInfo();
		prevScoreInfo.setScore(score);
		prevScoreInfo.setAvgScore(avgScore);
		prevScoreInfo.setTimestamp(new Date());
		prevScores.add(prevScoreInfo);
		
		return prevScores;
	}
	
	
	private AdUserFeaturesExtraction createAdUserFeaturesExtractionDummy(String userId, String rawId){
		AdUserFeaturesExtraction adUserFeaturesExtraction = new AdUserFeaturesExtraction(Classifier.groups.getId(), userId, rawId);
		return adUserFeaturesExtraction;
	}
	
	public class TestUser extends User{

		public TestUser(String adDn) {
			super(adDn);
		}
		
		public void setId(String userIdString) {
			super.setId(userIdString);
		}
	}
}
