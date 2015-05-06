package fortscale.services.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import junitparams.JUnitParamsRunner;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import fortscale.domain.core.ClassifierScore;
import fortscale.domain.core.ScoreInfo;
import fortscale.domain.core.User;
import fortscale.domain.core.dao.UserRepository;
import fortscale.services.IUserScoreHistoryElement;

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
	public void getUserScoresHistoryWithCurrentScoreChosenTest(){
		//init
		String uid = "uid1";
		String classifierId = "total";
		List<ScoreInfo> scoreInfoList = new ArrayList<>();
		DateTime now = DateTime.now();
		Date currentDate = now.toDate();
		ScoreInfo scoreInfo = new ScoreInfo();
		scoreInfo.setScore(70);
		scoreInfo.setTimestamp(currentDate);
		scoreInfo.setTimestampEpoc(currentDate.getTime());
		scoreInfoList.add(scoreInfo);
		scoreInfo = new ScoreInfo();
		scoreInfo.setScore(80);
		DateTime scoreDate = now.minusDays(1);
		scoreInfo.setTimestamp(scoreDate.toDate());
		scoreInfo.setTimestampEpoc(scoreDate.getMillis());
		scoreInfoList.add(scoreInfo);
		scoreInfo = new ScoreInfo();
		scoreInfo.setScore(90);
		scoreDate = now.minusDays(2);
		scoreInfo.setTimestamp(scoreDate.toDate());
		scoreInfo.setTimestampEpoc(scoreDate.getMillis());
		scoreInfoList.add(scoreInfo);

		//stub
		when(userRepository.findOne(uid)).thenReturn(user);
		when(user.getScore(classifierId)).thenReturn(classifierScore);
		when(classifierScore.getPrevScores()).thenReturn(scoreInfoList);
		when(classifierScore.getTimestamp()).thenReturn(currentDate);
		when(classifierScore.getTimestampEpoc()).thenReturn(currentDate.getTime());
		when(classifierScore.getScore()).thenReturn(90.0);

		//run the test
		List<IUserScoreHistoryElement> userScoreHistoryElements = userScoreService.getUserScoresHistory(uid, "total", now.minusDays(10).getMillis(), now.getMillis(), 0);
		verify(userRepository, times(1)).findOne(uid);
		assertEquals(3, userScoreHistoryElements.size());
		assertEquals(90, userScoreHistoryElements.get(0).getScore());
		assertEquals(currentDate, userScoreHistoryElements.get(0).getDate());
	}
	
	@Test
	public void getUserScoresHistoryWithFirstPrevScoreChosenTest(){
		//init
				String uid = "uid1";
				String classifierId = "total";
				List<ScoreInfo> scoreInfoList = new ArrayList<>();
				DateTime now = DateTime.now();
				Date currentDate = now.toDate();
				ScoreInfo scoreInfo = new ScoreInfo();
				scoreInfo.setScore(95);
				scoreInfo.setTimestamp(currentDate);
				scoreInfo.setTimestampEpoc(currentDate.getTime());
				scoreInfoList.add(scoreInfo);
				scoreInfo = new ScoreInfo();
				scoreInfo.setScore(80);
				DateTime scoreDate = now.minusDays(1);
				scoreInfo.setTimestamp(scoreDate.toDate());
				scoreInfo.setTimestampEpoc(scoreDate.getMillis());
				scoreInfoList.add(scoreInfo);
				scoreInfo = new ScoreInfo();
				scoreInfo.setScore(90);
				scoreDate = now.minusDays(2);
				scoreInfo.setTimestamp(scoreDate.toDate());
				scoreInfo.setTimestampEpoc(scoreDate.getMillis());
				scoreInfoList.add(scoreInfo);

				//stub
				when(userRepository.findOne(uid)).thenReturn(user);
				when(user.getScore(classifierId)).thenReturn(classifierScore);
				when(classifierScore.getPrevScores()).thenReturn(scoreInfoList);
				when(classifierScore.getTimestamp()).thenReturn(currentDate);
				when(classifierScore.getTimestampEpoc()).thenReturn(currentDate.getTime());
				when(classifierScore.getScore()).thenReturn(90.0);

				//run the test
				List<IUserScoreHistoryElement> userScoreHistoryElements = userScoreService.getUserScoresHistory(uid, "total", now.minusDays(10).getMillis(), now.getMillis(), 0);
				verify(userRepository, times(1)).findOne(uid);
				assertEquals(3, userScoreHistoryElements.size());
				assertEquals(95, userScoreHistoryElements.get(0).getScore());
				assertEquals(currentDate, userScoreHistoryElements.get(0).getDate());
	}
	
	@Test
	public void getUserScoresHistoryWithRangeNotContainCurrentTest(){
		//init
				String uid = "uid1";
				String classifierId = "total";
				List<ScoreInfo> scoreInfoList = new ArrayList<>();
				DateTime now = DateTime.now();
				Date currentDate = now.toDate();
				ScoreInfo scoreInfo = new ScoreInfo();
				scoreInfo.setScore(95);
				scoreInfo.setTimestamp(currentDate);
				scoreInfo.setTimestampEpoc(currentDate.getTime());
				scoreInfoList.add(scoreInfo);
				scoreInfo = new ScoreInfo();
				scoreInfo.setScore(80);
				DateTime scoreDate = now.minusDays(1);
				scoreInfo.setTimestamp(scoreDate.toDate());
				scoreInfo.setTimestampEpoc(scoreDate.getMillis());
				scoreInfoList.add(scoreInfo);
				scoreInfo = new ScoreInfo();
				scoreInfo.setScore(90);
				scoreDate = now.minusDays(2);
				scoreInfo.setTimestamp(scoreDate.toDate());
				scoreInfo.setTimestampEpoc(scoreDate.getMillis());
				scoreInfoList.add(scoreInfo);

				//stub
				when(userRepository.findOne(uid)).thenReturn(user);
				when(user.getScore(classifierId)).thenReturn(classifierScore);
				when(classifierScore.getPrevScores()).thenReturn(scoreInfoList);
				when(classifierScore.getTimestamp()).thenReturn(currentDate);
				when(classifierScore.getTimestampEpoc()).thenReturn(currentDate.getTime());
				when(classifierScore.getScore()).thenReturn(90.0);

				//run the test
				List<IUserScoreHistoryElement> userScoreHistoryElements = userScoreService.getUserScoresHistory(uid, "total", now.minusDays(10).getMillis(), now.minusDays(1).getMillis(), 0);
				verify(userRepository, times(1)).findOne(uid);
				assertEquals(2, userScoreHistoryElements.size());
				assertEquals(80, userScoreHistoryElements.get(0).getScore());
				assertEquals(scoreInfoList.get(1).getTimestamp(), userScoreHistoryElements.get(0).getDate());
	}
	
	@Test
	public void getUserScoresHistoryWithEmptyPrevListTest(){
		//init
		String uid = "uid1";
		String classifierId = "total";
		List<ScoreInfo> scoreInfoList = new ArrayList<>();
		DateTime now = DateTime.now();
		Date currentDate = now.toDate();

		//stub
		when(userRepository.findOne(uid)).thenReturn(user);
		when(user.getScore(classifierId)).thenReturn(classifierScore);
		when(classifierScore.getPrevScores()).thenReturn(scoreInfoList);
		when(classifierScore.getTimestamp()).thenReturn(currentDate);
		when(classifierScore.getTimestampEpoc()).thenReturn(currentDate.getTime());
		when(classifierScore.getScore()).thenReturn(90.0);

		//run the test
		List<IUserScoreHistoryElement> userScoreHistoryElements = userScoreService.getUserScoresHistory(uid, "total", now.minusDays(10).getMillis(), now.getMillis(), 0);
		verify(userRepository, times(1)).findOne(uid);
		assertEquals(1, userScoreHistoryElements.size());
		assertEquals(90, userScoreHistoryElements.get(0).getScore());
		assertEquals(currentDate, userScoreHistoryElements.get(0).getDate());
	}
	
	@Test
	public void getUserScoresHistoryWithEmptyPrevListAndRangeNotContainCurrentTest(){
		//init
		String uid = "uid1";
		String classifierId = "total";
		List<ScoreInfo> scoreInfoList = new ArrayList<>();
		DateTime now = DateTime.now();
		Date currentDate = now.toDate();

		//stub
		when(userRepository.findOne(uid)).thenReturn(user);
		when(user.getScore(classifierId)).thenReturn(classifierScore);
		when(classifierScore.getPrevScores()).thenReturn(scoreInfoList);
		when(classifierScore.getTimestamp()).thenReturn(currentDate);
		when(classifierScore.getTimestampEpoc()).thenReturn(currentDate.getTime());
		when(classifierScore.getScore()).thenReturn(90.0);

		//run the test
		List<IUserScoreHistoryElement> userScoreHistoryElements = userScoreService.getUserScoresHistory(uid, "total", now.minusDays(10).getMillis(), now.minusDays(1).getMillis(), 0);
		verify(userRepository, times(1)).findOne(uid);
		assertEquals(0, userScoreHistoryElements.size());
	}
	
	@Test
	public void getUserScoresHistoryWithTimezoneShiftAndFirstPrevAndCurrentScoreOnTheSameDayTest(){
		//init
		String uid = "uid1";
		String classifierId = "total";
		List<ScoreInfo> scoreInfoList = new ArrayList<>();
		int tzshiftInMinutes = 150;
		int millisOffset = tzshiftInMinutes * 60 * 1000;
 		DateTimeZone dateTimeZone = DateTimeZone.forOffsetMillis(millisOffset);
		DateTime startOfDayPlusOne = DateTime.now(dateTimeZone).withTimeAtStartOfDay().plusHours(1);
		DateTime currentDate = startOfDayPlusOne.plusHours(3);
		ScoreInfo scoreInfo = new ScoreInfo();
		scoreInfo.setScore(70);
		scoreInfo.setTimestamp(startOfDayPlusOne.toDate());
		scoreInfo.setTimestampEpoc(startOfDayPlusOne.getMillis());
		scoreInfoList.add(scoreInfo);
		scoreInfo = new ScoreInfo();
		scoreInfo.setScore(80);
		DateTime scoreDate = startOfDayPlusOne.minusDays(1);
		scoreInfo.setTimestamp(scoreDate.toDate());
		scoreInfo.setTimestampEpoc(scoreDate.getMillis());
		scoreInfoList.add(scoreInfo);
		scoreInfo = new ScoreInfo();
		scoreInfo.setScore(90);
		scoreDate = startOfDayPlusOne.minusDays(2);
		scoreInfo.setTimestamp(scoreDate.toDate());
		scoreInfo.setTimestampEpoc(scoreDate.getMillis());
		scoreInfoList.add(scoreInfo);

		//stub
		when(userRepository.findOne(uid)).thenReturn(user);
		when(user.getScore(classifierId)).thenReturn(classifierScore);
		when(classifierScore.getPrevScores()).thenReturn(scoreInfoList);
		when(classifierScore.getTimestamp()).thenReturn(currentDate.toDate());
		when(classifierScore.getTimestampEpoc()).thenReturn(currentDate.getMillis());
		when(classifierScore.getScore()).thenReturn(90.0);

		//run the test
		List<IUserScoreHistoryElement> userScoreHistoryElements = userScoreService.getUserScoresHistory(uid, "total", currentDate.minusDays(10).getMillis(), currentDate.getMillis(), tzshiftInMinutes);
		verify(userRepository, times(1)).findOne(uid);
		assertEquals(3, userScoreHistoryElements.size());
		assertEquals(90, userScoreHistoryElements.get(0).getScore());
		assertEquals(currentDate.getMillis(), userScoreHistoryElements.get(0).getDate().getTime());
	}
	
	@Test
	public void getUserScoresHistoryWithTimezoneShiftAndFirstPrevAndCurrentScoreNotOnTheSameDayTest(){
		//init
		String uid = "uid1";
		String classifierId = "total";
		List<ScoreInfo> scoreInfoList = new ArrayList<>();
		int tzshiftInMinutes = 150;
		int millisOffset = tzshiftInMinutes * 60 * 1000;
 		DateTimeZone dateTimeZone = DateTimeZone.forOffsetMillis(millisOffset);
		DateTime startOfDayMinusOne = DateTime.now(dateTimeZone).withTimeAtStartOfDay().minusHours(1);
		DateTime currentDate = startOfDayMinusOne.plusHours(1);
		ScoreInfo scoreInfo = new ScoreInfo();
		scoreInfo.setScore(70);
		scoreInfo.setTimestamp(startOfDayMinusOne.toDate());
		scoreInfo.setTimestampEpoc(startOfDayMinusOne.getMillis());
		scoreInfoList.add(scoreInfo);
		scoreInfo = new ScoreInfo();
		scoreInfo.setScore(80);
		DateTime scoreDate = startOfDayMinusOne.minusDays(1);
		scoreInfo.setTimestamp(scoreDate.toDate());
		scoreInfo.setTimestampEpoc(scoreDate.getMillis());
		scoreInfoList.add(scoreInfo);
		scoreInfo = new ScoreInfo();
		scoreInfo.setScore(90);
		scoreDate = startOfDayMinusOne.minusDays(2);
		scoreInfo.setTimestamp(scoreDate.toDate());
		scoreInfo.setTimestampEpoc(scoreDate.getMillis());
		scoreInfoList.add(scoreInfo);

		//stub
		when(userRepository.findOne(uid)).thenReturn(user);
		when(user.getScore(classifierId)).thenReturn(classifierScore);
		when(classifierScore.getPrevScores()).thenReturn(scoreInfoList);
		when(classifierScore.getTimestamp()).thenReturn(currentDate.toDate());
		when(classifierScore.getTimestampEpoc()).thenReturn(currentDate.getMillis());
		when(classifierScore.getScore()).thenReturn(90.0);

		//run the test
		List<IUserScoreHistoryElement> userScoreHistoryElements = userScoreService.getUserScoresHistory(uid, "total", currentDate.minusDays(10).getMillis(), currentDate.getMillis(), tzshiftInMinutes);
		verify(userRepository, times(1)).findOne(uid);
		assertEquals(4, userScoreHistoryElements.size());
		assertEquals(90, userScoreHistoryElements.get(0).getScore());
		assertEquals(currentDate.getMillis(), userScoreHistoryElements.get(0).getDate().getTime());
	}
	
}
