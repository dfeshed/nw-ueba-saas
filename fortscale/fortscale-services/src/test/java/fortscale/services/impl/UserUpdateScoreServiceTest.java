package fortscale.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verifyZeroInteractions;

import java.util.Date;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.services.UserScoreService;
import fortscale.services.UserService;
import fortscale.services.analyst.ConfigurationService;


@RunWith(JUnitParamsRunner.class)
public class UserUpdateScoreServiceTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private UserService userService;
	
	@Mock
	private UserScoreService userScoreService;
				
	@Mock
	private AdUsersFeaturesExtractionRepository adUsersFeaturesExtractionRepository;
	
	@Mock
	private AuthDAO loginDAO;
	
	@Mock
	private AuthDAO sshDAO;
	
	@Mock
	private VpnDAO vpnDAO;
				
	@Mock
	private ImpalaWriterFactory impalaWriterFactory;
	
	@Mock
	private ConfigurationService configurationService; 
		
	@InjectMocks
	private UserUpdateScoreServiceImpl userUpdateScoreService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}
	
//	@Test(expected=IllegalArgumentException.class)
//	@Parameters({"vpn", "active_directory", "active_directory_group_membership", "total"})
//	public void updateUserWithAuthScoreWrongClassifierTest(String classifierString){
//		Classifier classifier = Classifier.valueOf(classifierString);
//		userUpdateScoreService.updateUserWithAuthScore(classifier);
//	}
	
	@Test
	public void thisAlwaysPasses() {
	}

	@Test
	@Ignore
	public void thisIsIgnored() {
	}
	
	
	
	/*
	 * Tests for updateUserScore
	 */
	
	/*
	 * Test first update of user score.
	 */
	@Test
	@Parameters({ "aaa, 30.2, 55.1","bbb, 21.5, 10.4"})
	public void updateUserScoreTest(String classifierId, double score, double avgScore){
		User user = new NewUser("testId");
		Date timestamp = new Date(System.currentTimeMillis() - 1000);
//		ScoreInfo scoreInfo = UserScoreTestUtil.createScoreInfo(timestamp, avgScore, score, 10, 10);
//		ClassifierScore classifierScore = new ClassifierScore(classifierId, scoreInfo);
		User retUser = userUpdateScoreService.updateUserScore(user, timestamp, classifierId, score, avgScore, false, false);
		verifyZeroInteractions(userRepository);
		assertNotNull(retUser);
		assertEquals(user.getId(), retUser.getId());
		ClassifierScore classifierScore = retUser.getScore(classifierId);
		assertNotNull(classifierScore);
		assertEquals(classifierId, classifierScore.getClassifierId());
		ScoreInfo expected = UserScoreTestUtil.createScoreInfo(timestamp, avgScore, score, 0, 0);
		testScoreInfo(expected, classifierScore);
		List<ScoreInfo> infos = classifierScore.getPrevScores();
		assertNotNull(infos);
		assertEquals(1, infos.size());
		expected = infos.get(0);
		testScoreInfo(expected, classifierScore);
	}
	
	private void testScoreInfo(ScoreInfo expected, ScoreInfo actual){
		assertEquals(expected.getAvgScore(), actual.getAvgScore(), 0);
		assertEquals(expected.getScore(), actual.getScore(),0);
		assertEquals(expected.getTimestamp(), actual.getTimestamp());
		assertEquals(expected.getTimestampEpoc(), actual.getTimestampEpoc());
		assertEquals(expected.getTrend(), actual.getTrend(),0);
		assertEquals(expected.getTrendScore(), actual.getTrendScore(),0);
	}
	
	
	
	
	
	
	/*
	 * Tests for updateUserWithAuthScore
	 */
	
	
}
