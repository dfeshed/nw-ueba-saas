package fortscale.services.impl;

import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import junitparams.JUnitParamsRunner;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.NoMoreInteractions;
import org.mockito.internal.verification.Times;
import org.mockito.verification.VerificationMode;

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
	
	@Test
	public void updateUserWithAuthScoreForLoginAndUsernameNullTest(){
		Date date = new Date(System.currentTimeMillis() - 1000);
		User user = new NewUser("test");
		Classifier classifier = Classifier.auth;
		double avgScore = 23;
		AuthScore authScore = new AuthScore();
		List<AuthScore> authScores = new ArrayList<>();
		authScores.add(authScore);
		when(loginDAO.findGlobalScoreByTimestamp(date)).thenReturn(authScores);
		when(loginDAO.getTableName()).thenReturn("loginstable");
		when(userRepository.findAllExcludeAdInfo()).thenReturn(Collections.<User>emptyList());
		ScoreInfo scoreInfo = UserScoreTestUtil.createScoreInfo(2014, 1, 1, 12, avgScore, 50, 10, 10);
		ClassifierScore classifierScore = new ClassifierScore(classifier.getId(), scoreInfo);
		when(loginDAO.getLastRunDate()).thenReturn(date);
		when(loginDAO.calculateAvgScoreOfGlobalScore(date)).thenReturn(avgScore);
		userUpdateScoreService.updateUserWithAuthScore(Classifier.auth);
		verify(userRepository, never()).save((List) any());
//		verify(loginDAO, atLeast(1)).findGlobalScoreByTimestamp((Date)any());
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
	
	
	class NewUser extends User{
		public NewUser(){
			super();
		}
		
		public NewUser(String id){
			setId(id);
		}
	}
	
}
