package fortscale.services.fe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.math.IntRange;
import org.apache.commons.lang.math.Range;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.jdbc.core.JdbcOperations;

import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.EventResultRepository;
import fortscale.domain.fe.dao.Threshold;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.services.UserService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.fe.impl.ClassifierServiceImpl;
import fortscale.services.fe.impl.ScoreDistribution;
import fortscale.services.impl.SeverityElement;
import fortscale.utils.impala.ImpalaParser;

public class ClassifierServiceTest{
	@Mock
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Mock
	private JdbcOperations impalaJdbcTemplate;
	
	@Mock
	private UserRepository userRepository;
	
	@Mock
	private EventResultRepository eventResultRepository;
	
	@Mock
	private AuthDAO loginDAO;
	
	@Mock
	private AuthDAO sshDAO;
	
	@Mock
	private VpnDAO vpnDAO;
	
	@Mock
	private UserMachineDAO userMachineDAO;
	
	@Mock
	private UserService userService;
	
	@Mock
	private ConfigurationService configurationService;
	
	@Mock
	private ImpalaParser impalaParser;
	
	@InjectMocks
	private ClassifierServiceImpl classifierService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	

	@Test
	public void testGetScoreDistributionForAuth() {
		testGetScoreDistribution(Classifier.auth.getId());
	}
	
	
	private void testGetScoreDistribution(String classifierId){
		int total = 17;
		Mockito.when(userRepository.countNumOfUsersAboveThreshold(classifierId, new Threshold("All", 0))).thenReturn(total);
		
		List<Threshold> thresholds = getThresholds();
		Map<String,IScoreDistribution> expectedScoreDistributions = new HashMap<>();
		
		int thresholdIndex = 0;
		int startCount = 17;
		int endCount = 9;
		addScoreDistribution(expectedScoreDistributions, classifierId, total, thresholds.get(thresholdIndex), thresholds.get(thresholdIndex+1), startCount, endCount);
		
		thresholdIndex++;
		startCount = endCount;
		endCount = 3;
		addScoreDistribution(expectedScoreDistributions, classifierId, total, thresholds.get(thresholdIndex), thresholds.get(thresholdIndex+1), startCount, endCount);
		
		thresholdIndex++;
		startCount = endCount;
		endCount = 1;
		addScoreDistribution(expectedScoreDistributions, classifierId, total, thresholds.get(thresholdIndex), thresholds.get(thresholdIndex+1), startCount, endCount);
		
		thresholdIndex++;
		startCount = endCount;
		endCount = 0;
		addScoreDistribution(expectedScoreDistributions, classifierId, total, thresholds.get(thresholdIndex), new Threshold("Last", 100), startCount, endCount);
		
		List<SeverityElement> severityElements = getSeverityElements(thresholds);
		Mockito.when(configurationService.getSeverityElements()).thenReturn(severityElements);
		
		List<IScoreDistribution> scoreDistributions = classifierService.getScoreDistribution(classifierId);
		
		Assert.assertEquals(expectedScoreDistributions.size(), scoreDistributions.size());
		
		for(IScoreDistribution scoreDistribution: scoreDistributions){
			IScoreDistribution expectedScoreDistribution = expectedScoreDistributions.get(scoreDistribution.getName());
			Assert.assertNotNull(expectedScoreDistribution);
			Assert.assertEquals(expectedScoreDistribution, scoreDistribution);
		}
		
	}
	
	private List<Threshold> getThresholds(){
		List<Threshold> ret = new ArrayList<>();
		ret.add(new Threshold("Low", 0));
		ret.add(new Threshold("Medium", 50));
		ret.add(new Threshold("High", 80));
		ret.add(new Threshold("Critical", 90));
		return ret;
	}
	
	private List<SeverityElement> getSeverityElements(List<Threshold> thresholds){
		List<SeverityElement> ret = new ArrayList<>();
		for(Threshold threshold: thresholds){
			ret.add(new SeverityElement(threshold.getName(), threshold.getValue()));
		}
		Collections.reverse(ret);
		return ret;
	}
	
	private void addScoreDistribution(Map<String,IScoreDistribution> expectedScoreDistributions, String classifierId, int total, Threshold startThreshold, Threshold endThreshold, int startCount, int endCount){
		Mockito.when(userRepository.countNumOfUsersAboveThreshold(classifierId, startThreshold)).thenReturn(startCount);
		double percent =  Math.round(100 * (startCount - endCount) / ((double) total) );
		expectedScoreDistributions.put(startThreshold.getName(), new ScoreDistribution(startThreshold.getName(), (startCount - endCount), (int)percent, startThreshold.getValue(), endThreshold.getValue()));
	}
	
	@Test
	public void testgetGroupSuspiciousUsers() {
//		List<Threshold> thresholds = getThresholds();
//		List<SeverityElement> severityElements = getSeverityElements(thresholds);
//		Mockito.when(configurationService.getSeverityElements()).thenReturn(severityElements);
		
		List<User> users = new ArrayList<>();
		double avgScore = 30;
		double prevScore = 70;
		double prevAvgScore = 20;
		double score = 99.6;
		int limit = 4;
		int step = 5;
		for(int i = 0; i < limit; i++){
			users.add( createUser(String.format("test%d",i), score -i*step, avgScore, prevScore, prevAvgScore));
		}
		
		String severityId = "Critical";
		Range severityRange = new IntRange(90, 100);
		Mockito.when(configurationService.getRange(severityId)).thenReturn(severityRange);
		String classifierId = Classifier.groups.getId();
		Pageable pageable = new PageRequest(0, limit, Direction.DESC, User.getClassifierScoreCurrentScoreField(classifierId), User.getClassifierScoreCurrentTrendScoreField(classifierId));
		Mockito.when(userRepository.findByClassifierIdAndScoreBetween(classifierId, severityRange.getMinimumInteger(), severityRange.getMaximumInteger(), pageable)).thenReturn(users);
		List<ISuspiciousUserInfo> suspiciousUserInfos = classifierService.getSuspiciousUsersByScore(Classifier.groups.getId(), severityId, 0, limit, false);
		Assert.assertEquals(limit, suspiciousUserInfos.size());
		for(int i = 0; i < limit; i++){
			ISuspiciousUserInfo suspiciousUserInfo = suspiciousUserInfos.get(i);
			User user = users.get(i);
			Assert.assertEquals(user.getId(), suspiciousUserInfo.getUserId());
			Assert.assertEquals((int)Math.floor(user.getScore(classifierId).getScore()), suspiciousUserInfo.getScore());
		}
		
	}
	
	private TestUser createUser(String id) {
		TestUser retUser = new TestUser(id + "-dn");
		retUser.setId(id);
		return retUser;
	}
	
	private TestUser createUser(String id, double score, double avgScore, double prevScore, double prevAvgScore) {
		TestUser user = createUser(id);
		user.putClassifierScore(createClassifierScore(Classifier.groups.getId(), score, avgScore, createPrevScoreInfoList(prevScore, prevAvgScore)));
		user.putClassifierScore(createClassifierScore(Classifier.auth.getId(), score, avgScore, createPrevScoreInfoList(prevScore, prevAvgScore)));
		return user;
	}
	
	
	
	private ClassifierScore createClassifierScore(String classifierId, double score, double avgScore, List<ScoreInfo> prevScores) {
		ClassifierScore classifierScore = new ClassifierScore();
		classifierScore.setScore(score);
		classifierScore.setAvgScore(avgScore);
		classifierScore.setTimestamp(new Date());
		classifierScore.setPrevScores(prevScores);
		classifierScore.setClassifierId(classifierId);
		return classifierScore;
	}
	
	private List<ScoreInfo> createPrevScoreInfoList(double score, double avgScore){
		List<ScoreInfo> prevScores = new ArrayList<>();
		ScoreInfo prevScoreInfo = new ScoreInfo();
		prevScoreInfo.setScore(score);
		prevScoreInfo.setAvgScore(avgScore);
		prevScoreInfo.setTimestamp(new Date());
		prevScores.add(prevScoreInfo);
		
		return prevScores;
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
