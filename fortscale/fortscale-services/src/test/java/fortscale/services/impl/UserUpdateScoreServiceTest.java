package fortscale.services.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
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
import org.springframework.data.mongodb.core.query.Update;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.dao.AdUsersFeaturesExtractionRepository;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.services.UserScoreService;
import fortscale.services.UserService;
import fortscale.services.analyst.ConfigurationService;
import fortscale.services.fe.Classifier;


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
	
	@SuppressWarnings("unchecked")
	@Test(expected=Exception.class)
	public void updateUserWithAuthScoreForLoginAvgExceptionWithEventsTest(){
		Date date = new Date(System.currentTimeMillis() - 1000);
		when(loginDAO.getLastRunDate()).thenReturn(date);
		when(loginDAO.calculateAvgScoreOfGlobalScore(date)).thenThrow(Exception.class);
		when(loginDAO.countNumOfEvents(date)).thenReturn(10);
		userUpdateScoreService.updateUserWithAuthScore(Classifier.auth);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void updateUserWithAuthScoreForLoginAvgExceptionWithNoEventsTest(){
		Date date = new Date(System.currentTimeMillis() - 1000);
		when(loginDAO.getLastRunDate()).thenReturn(date);
		when(loginDAO.calculateAvgScoreOfGlobalScore(date)).thenThrow(Exception.class);
		when(loginDAO.countNumOfEvents(date)).thenReturn(0);
		userUpdateScoreService.updateUserWithAuthScore(Classifier.auth);
		verify(loginDAO, never()).findGlobalScoreByTimestamp(date);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void updateUserWithAuthScoreForLoginAndUsernameNullTest(){
		Date date = new Date(System.currentTimeMillis() - 1000);
		double avgScore = 23;
		AuthScore authScore = new AuthScore();
		List<AuthScore> authScores = new ArrayList<>();
		authScores.add(authScore);
		when(loginDAO.findGlobalScoreByTimestamp(date)).thenReturn(authScores);
		when(loginDAO.getTableName()).thenReturn("loginstable");
		when(userRepository.findAllExcludeAdInfo()).thenReturn(Collections.<User>emptyList());
		when(loginDAO.getLastRunDate()).thenReturn(date);
		when(loginDAO.calculateAvgScoreOfGlobalScore(date)).thenReturn(avgScore);
		userUpdateScoreService.updateUserWithAuthScore(Classifier.auth);
		verify(userRepository, never()).save((Iterable<User>) any());
//		verify(loginDAO, atLeast(1)).findGlobalScoreByTimestamp((Date)any());
		verifyZeroInteractions(userService);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void updateUserWithAuthScoreForLoginAndUsernameEmptyStringTest(){
		Date date = new Date(System.currentTimeMillis() - 1000);
		double avgScore = 23;
		AuthScore authScore = new AuthScore();
		authScore.setUserName("");
		List<AuthScore> authScores = new ArrayList<>();
		authScores.add(authScore);
		when(loginDAO.findGlobalScoreByTimestamp(date)).thenReturn(authScores);
		when(loginDAO.getTableName()).thenReturn("loginstable");
		when(userRepository.findAllExcludeAdInfo()).thenReturn(Collections.<User>emptyList());
		when(loginDAO.getLastRunDate()).thenReturn(date);
		when(loginDAO.calculateAvgScoreOfGlobalScore(date)).thenReturn(avgScore);
		userUpdateScoreService.updateUserWithAuthScore(Classifier.auth);
		verify(userRepository, never()).save((Iterable<User>) any());
		verifyZeroInteractions(userService);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void updateUserWithAuthScoreForLoginAndNonCorrelationToAdUserUsernameAndEmptyLogUsernameTest(){
		//Testing the scenario where the login username is not correlated to any AdUser name and that the logUsername field of the User is empty.
		Date date = new Date(System.currentTimeMillis() - 1000);
		
		double avgScore = 23;
		AuthScore authScore = new AuthScore();
		String loginUserName = "testUser";
		authScore.setUserName(loginUserName);
		List<AuthScore> authScores = new ArrayList<>();
		authScores.add(authScore);
		when(loginDAO.findGlobalScoreByTimestamp(date)).thenReturn(authScores);
		when(loginDAO.getTableName()).thenReturn("loginstable");
		String adUserName = "testUser1";
		User user = new NewUser("testId");
		user.setUsername(adUserName);
		List<User> users = new ArrayList<>();
		users.add(user);
		when(userRepository.findAllExcludeAdInfo()).thenReturn(users);
		when(loginDAO.getLastRunDate()).thenReturn(date);
		when(loginDAO.calculateAvgScoreOfGlobalScore(date)).thenReturn(avgScore);
		userUpdateScoreService.updateUserWithAuthScore(Classifier.auth);
		verify(userRepository, never()).save((Iterable<User>) any());
		verifyZeroInteractions(userService);
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void updateUserWithAuthScoreForLoginAndCorrelationToAdUserUsernameAndEmptyLogUsernameTest(){
		//Testing the scenario where the login username is not correlated to any AdUser name and that the logUsername field of the User is empty.
		Classifier classifier = Classifier.auth;
		
		Date date = new Date(System.currentTimeMillis() - 1000);
		
		double avgScore = 23;
		AuthScore authScore = new AuthScore();
		String loginUserName = "testUser";
		authScore.setUserName(loginUserName);
		double globalScore = 12.3;
		authScore.setGlobalScore(globalScore);
		List<AuthScore> authScores = new ArrayList<>();
		authScores.add(authScore);
		when(loginDAO.findGlobalScoreByTimestamp(date)).thenReturn(authScores);
		String tableName = "loginstable";
		when(loginDAO.getTableName()).thenReturn(tableName);
		String adUserName = String.format("%s@abc.com",loginUserName);
		User user = new NewUser("testId");
		user.setUsername(adUserName);
//		ScoreInfo scoreInfo = UserScoreTestUtil.createScoreInfo(date, avgScore, globalScore, 10, 10);
//		ClassifierScore classifierScore = new ClassifierScore(classifier.getId(), scoreInfo);
//		user.putClassifierScore(classifierScore);
		List<User> users = new ArrayList<>();
		users.add(user);
		when(userRepository.findAllExcludeAdInfo()).thenReturn(users);
		when(loginDAO.getLastRunDate()).thenReturn(date);
		when(loginDAO.calculateAvgScoreOfGlobalScore(date)).thenReturn(avgScore);
		when(userService.createNewApplicationUserDetails(user, classifier.getUserApplication(), loginUserName, false)).thenReturn(false);
		userUpdateScoreService.updateUserWithAuthScore(classifier);
		ClassifierScore classifierScore = user.getScore(classifier.getId());
		assertNotNull(classifierScore);
		assertEquals(avgScore, classifierScore.getAvgScore(), 0);
		assertEquals(globalScore, classifierScore.getScore(),0);
		verify(userService,times(1)).updateLogUsername(eq(user), eq(tableName), eq(loginUserName), eq(false));
		verify(userRepository, never()).save((Iterable<User>) any());
		verify(userService, times(1)).fillUpdateUserScore((Update) any(), eq(user),  eq(classifier));
		verify(userService, times(1)).fillUpdateLogUsername((Update) any(), eq(loginUserName), eq(tableName));
		verify(userService, never()).fillUpdateAppUsername((Update) any(), (User) any(), (Classifier) any());
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void updateUserWithAuthScoreForLoginAndCorrelationToAdUserUsernameAndNonCorrelationToLogUsernameTest(){
		//Testing the scenario where the login username is correlated to AdUser name but not correlated to the logUsername field of the User.
		Date date = new Date(System.currentTimeMillis() - 1000);
		
		double avgScore = 23;
		AuthScore authScore = new AuthScore();
		String eventLoginUserName = "testUser";
		authScore.setUserName(eventLoginUserName);
		List<AuthScore> authScores = new ArrayList<>();
		authScores.add(authScore);
		when(loginDAO.findGlobalScoreByTimestamp(date)).thenReturn(authScores);
		String tableName = "loginstable";
		when(loginDAO.getTableName()).thenReturn(tableName);
		String adUserName = eventLoginUserName;
		User user = new NewUser("testId");
		user.setUsername(adUserName);
		String userLoginUsername = "testUser@abc.com";
		user.addLogUsername(tableName, userLoginUsername);
		List<User> users = new ArrayList<>();
		users.add(user);
		when(userRepository.findAllExcludeAdInfo()).thenReturn(users);
		when(loginDAO.getLastRunDate()).thenReturn(date);
		when(loginDAO.calculateAvgScoreOfGlobalScore(date)).thenReturn(avgScore);
		userUpdateScoreService.updateUserWithAuthScore(Classifier.auth);
		verify(userRepository, never()).save((Iterable<User>) any());
		verifyZeroInteractions(userService);
	}
	
	@Test
	public void updateUserWithAuthScoreLoginTest(){
//		Date date = new Date(System.currentTimeMillis() - 1000);
//		User user = new NewUser("test");
//		Classifier classifier = Classifier.auth;
//		double avgScore = 23;
//		AuthScore authScore = new AuthScore();
//		List<AuthScore> authScores = new ArrayList<>();
//		authScores.add(authScore);
//		ScoreInfo scoreInfo = UserScoreTestUtil.createScoreInfo(2014, 1, 1, 12, avgScore, 50, 10, 10);
//		ClassifierScore classifierScore = new ClassifierScore(classifier.getId(), scoreInfo);
//		when(loginDAO.getLastRunDate()).thenReturn(date);
//		when(loginDAO.calculateAvgScoreOfGlobalScore(date)).thenReturn(avgScore);
//		userUpdateScoreService.updateUserWithAuthScore(Classifier.auth);
	}
	
}
