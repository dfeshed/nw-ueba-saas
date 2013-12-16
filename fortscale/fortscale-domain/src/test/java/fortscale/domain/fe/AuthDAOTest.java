package fortscale.domain.fe;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.AbstractTest;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.Threshold;
import fortscale.utils.impala.ImpalaPageRequest;

@Ignore
public class AuthDAOTest  extends AbstractTest{

	@Autowired
	private AuthDAO loginDAO;
	
	@Test
	public void testGetLastRunDate() {
		loginDAO.getLastRunDate();
	}
	
	@Test
	public void testGetTopEventsAboveThreshold() {
		Date lastRun = loginDAO.getLastRunDate();
		List<AuthScore> authScores = loginDAO.getTopEventsAboveThreshold(new Threshold("All", 0), lastRun, 2);
		Assert.assertTrue(authScores.size() > 0 && authScores.size() <= 2);
	}
	
	@Test
	public void testGetTopUsersAboveThreshold() {
		Date lastRun = loginDAO.getLastRunDate();
		List<AuthScore> authScores = loginDAO.getTopUsersAboveThreshold(new Threshold("All", 0), lastRun, 2);
		Assert.assertTrue(authScores.size() > 0 && authScores.size() <= 2);
	}
	
	@Test
	public void testCalculateAvgScoreOfGlobalScore() {
		Date lastRun = loginDAO.getLastRunDate();
		loginDAO.calculateAvgScoreOfGlobalScore(lastRun);
	}
	
	@Test
	public void testCountNumOfUsersAboveThreshold() {
		Date lastRun = loginDAO.getLastRunDate();
		int count = loginDAO.countNumOfUsersAboveThreshold(new Threshold("All", 0), lastRun);
		Assert.assertTrue(count > 0);
	}
	
	@Test
	public void testFindByTimestampAndGlobalScoreBetweenSortByEventScore() {
		Date lastRun = loginDAO.getLastRunDate();
		List<AuthScore> authScores = loginDAO.findByTimestampAndGlobalScoreBetweenSortByEventScore(lastRun, 0, 100, 2);
		Assert.assertTrue(authScores.size() > 0 && authScores.size() <= 2);
		if(authScores.size() > 1) {
			Assert.assertTrue(authScores.get(0).getEventScore() >= authScores.get(1).getEventScore());
		}
	}
	
	@Test
	public void testFindEventsByTimestamp() {
		Date lastRun = loginDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(2, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		List<AuthScore> authScores = loginDAO.findEventsByTimestamp(lastRun, pageable);
		Assert.assertTrue(authScores.size() > 0 && authScores.size() <= 2);
		Assert.assertTrue(authScores.get(0).getTimestamp().equals(lastRun));
	}
	
	@Test
	public void testFindCurrentAuthScoreByUsername() {
		Date lastRun = loginDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(1, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		AuthScore authScore = loginDAO.findEventsByTimestamp(lastRun, pageable).get(0);
		AuthScore authScore2 = loginDAO.findCurrentByUsername(authScore.getUserName().toLowerCase());
		Assert.assertEquals(authScore.getUserName(), authScore2.getUserName());
		Assert.assertTrue( authScore.getTimestamp().equals(authScore2.getTimestamp()) || authScore.getTimestamp().before(authScore2.getTimestamp()) );
	}
	
	@Test
	public void testFindEventsByTimestampWithAdditionalWhereQuery() {
		Date lastRun = loginDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(2, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		AuthScore authScore = loginDAO.findEventsByTimestamp(lastRun, pageable).get(0);
		
		String additionalWhereQuery = String.format("%s  = \"%s\" and %s > %d", AuthScore.USERNAME_FIELD_NAME, authScore.getUserName(), AuthScore.EVENT_SCORE_FIELD_NAME, 0);
		List<AuthScore> authScores = loginDAO.findEventsByTimestamp(lastRun, pageable, additionalWhereQuery);
		Assert.assertTrue(authScores.size() > 0 && authScores.size() <= 2);
		Assert.assertTrue(authScores.get(0).getTimestamp().equals(lastRun));
		Assert.assertTrue(authScores.get(0).getUserName().equals(authScore.getUserName()));
	}
	
	@Test
	public void testFindEventsByUsername() {
		Date lastRun = loginDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(2, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		AuthScore authScore = loginDAO.findEventsByTimestamp(lastRun, pageable).get(0);
		
		List<AuthScore> authScores = loginDAO.findEventsByUsername(authScore.getUserName().toLowerCase(), pageable);
		Assert.assertTrue(authScores.size() > 0 && authScores.size() <= 2);
		Assert.assertTrue(authScores.get(0).getUserName().equals(authScore.getUserName()));
	}
	
	@Test
	public void testFindEventsByUsernameAndTimestamp() {
		Date lastRun = loginDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(2, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		AuthScore authScore = loginDAO.findEventsByTimestamp(lastRun, pageable).get(0);
		
		List<AuthScore> authScores = loginDAO.findEventsByUsernameAndTimestamp(authScore.getUserName().toLowerCase(), lastRun, pageable);
		Assert.assertTrue(authScores.size() > 0 && authScores.size() <= 2);
		Assert.assertTrue(authScores.get(0).getTimestamp().equals(lastRun));
		Assert.assertTrue(authScores.get(0).getUserName().equals(authScore.getUserName()));
	}
	
	@Test
	public void testFindGlobalScoreByTimestamp() {
		Date lastRun = loginDAO.getLastRunDate();
		List<AuthScore> authScores = loginDAO.findGlobalScoreByTimestamp(lastRun);
		Assert.assertTrue(authScores.size() > 0);
		Assert.assertTrue(authScores.get(0).getTimestamp().equals(lastRun));
	}
	
	@Test
	public void testFindGlobalScoreByUsername() {
		Date lastRun = loginDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(2, new Sort(Direction.DESC, AuthScore.EVENT_SCORE_FIELD_NAME));
		AuthScore authScore = loginDAO.findEventsByTimestamp(lastRun, pageable).get(0);
		
		List<AuthScore> authScores = loginDAO.findGlobalScoreByUsername(authScore.getUserName().toLowerCase(), 2);
		Assert.assertTrue(authScores.size() > 0 && authScores.size() <= 2);
		Assert.assertTrue(authScores.get(0).getUserName().equals(authScore.getUserName()));
	}
}
