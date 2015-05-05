package fortscale.services.impl;

import fortscale.domain.ad.AdUser;
import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.AdGroupRepository;
import fortscale.domain.ad.dao.AdUserRepository;
import fortscale.domain.ad.dao.AdUserThumbnailRepository;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.core.*;
import fortscale.domain.core.dao.ComputerRepository;
import fortscale.domain.core.dao.UserRepository;
import fortscale.domain.fe.dao.EventScoreDAO;
import fortscale.services.IUserScoreHistoryElement;
import fortscale.services.UserApplication;
import fortscale.services.fe.ClassifierServiceTest;
import fortscale.utils.actdir.ADParser;
import junitparams.JUnitParamsRunner;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(JUnitParamsRunner.class)
public class UserScoreServiceTest {

	@Mock
	private UserRepository userRepository;
	
	@Mock
	private User user;

	@Mock
	private ClassifierScore classifierScore;

	@InjectMocks
	private UserScoreServiceImpl userScoreService;
	
	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
	}

	@Test
	public void createNewApplicationUserDetailsTest(){
		//init
		String uid = "uid1";
		String classifierId = "total";
		List<ScoreInfo> scoreInfoList = new ArrayList<>();
		Date currentDate = new Date();
		ScoreInfo scoreInfo = new ScoreInfo();
		scoreInfo.setScore(99);
		scoreInfo.setTimestamp(currentDate);
		scoreInfo.setTimestampEpoc(DateTime.now().getMillis());
		scoreInfoList.add(scoreInfo);
		scoreInfo.setScore(80);
		scoreInfo.setTimestampEpoc(DateTime.now().minus(1).getMillis());
		scoreInfo.setScore(70);
		scoreInfoList.add(scoreInfo);
		DateTimeZone timeZone = DateTimeZone.forID("UTC");
		DateTime now = DateTime.now(timeZone);
		UserApplication userApplication = UserApplication.active_directory;
		String username = "usernameTest";

		//stub
		when(userRepository.findOne(uid)).thenReturn(user);
		when(user.getScore(classifierId)).thenReturn(classifierScore);
		when(classifierScore.getPrevScores()).thenReturn(scoreInfoList);
		when(classifierScore.getTimestamp()).thenReturn(currentDate);
		when(classifierScore.getScore()).thenReturn(90.0);

		//run the test
		List<IUserScoreHistoryElement> userScoreHistoryElements = userScoreService.getUserScoresHistory(uid, "total", now.minusDays(10), now);
		verify(userRepository, times(1)).findOne(uid);
		assertEquals(2, userScoreHistoryElements.size());
		assertEquals(90, userScoreHistoryElements.get(0).getScore());
		assertEquals(currentDate, userScoreHistoryElements.get(0).getDate());
	}
	
}
