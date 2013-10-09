package fortscale.domain.fe;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

import fortscale.domain.AbstractTest;
import fortscale.domain.fe.dao.Threshold;
import fortscale.domain.fe.dao.VpnDAO;
import fortscale.utils.impala.ImpalaPageRequest;

public class VpnDAOTest extends AbstractTest {

	@Autowired
	private VpnDAO vpnDAO;
	
	@Test
	public void testGetLastRunDate() {
		vpnDAO.getLastRunDate();
	}
	
	@Test
	public void testGetTopEventsAboveThreshold() {
		Date lastRun = vpnDAO.getLastRunDate();
		List<VpnScore> vpnScores = vpnDAO.getTopEventsAboveThreshold(new Threshold("All", 0), lastRun, 2);
		Assert.assertTrue(vpnScores.size() > 0 && vpnScores.size() <= 2);
	}
	
	@Test
	public void testGetTopUsersAboveThreshold() {
		Date lastRun = vpnDAO.getLastRunDate();
		List<VpnScore> vpnScores = vpnDAO.getTopUsersAboveThreshold(new Threshold("All", 0), lastRun, 2);
		Assert.assertTrue(vpnScores.size() > 0 && vpnScores.size() <= 2);
	}
	
	@Test
	public void testCalculateAvgScoreOfGlobalScore() {
		Date lastRun = vpnDAO.getLastRunDate();
		vpnDAO.calculateAvgScoreOfGlobalScore(lastRun);
	}
	
	@Test
	public void testCountNumOfUsersAboveThreshold() {
		Date lastRun = vpnDAO.getLastRunDate();
		int count = vpnDAO.countNumOfUsersAboveThreshold(new Threshold("All", 0), lastRun);
		Assert.assertTrue(count > 0);
	}
	
	@Test
	public void testFindByTimestampAndGlobalScoreBetweenSortByEventScore() {
		Date lastRun = vpnDAO.getLastRunDate();
		List<VpnScore> vpnScores = vpnDAO.findByTimestampAndGlobalScoreBetweenSortByEventScore(lastRun, 0, 100, 2);
		Assert.assertTrue(vpnScores.size() > 0 && vpnScores.size() <= 2);
		if(vpnScores.size() > 1) {
			Assert.assertTrue(vpnScores.get(0).getEventScore() >= vpnScores.get(1).getEventScore());
		}
	}
	
	@Test
	public void testFindEventsByTimestamp() {
		Date lastRun = vpnDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(2, new Sort(Direction.DESC, VpnScore.EVENT_SCORE_FIELD_NAME));
		List<VpnScore> vpnScores = vpnDAO.findEventsByTimestamp(lastRun, pageable);
		Assert.assertTrue(vpnScores.size() > 0 && vpnScores.size() <= 2);
		Assert.assertTrue(vpnScores.get(0).getTimestamp().equals(lastRun));
	}
	
	@Test
	public void testFindCurrentVpnScoreByUsername() {
		Date lastRun = vpnDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(1, new Sort(Direction.DESC, VpnScore.EVENT_SCORE_FIELD_NAME));
		VpnScore vpnScore = vpnDAO.findEventsByTimestamp(lastRun, pageable).get(0);
		VpnScore vpnScore2 = vpnDAO.findCurrentByUsername(vpnScore.getUserName().toLowerCase());
		Assert.assertEquals(vpnScore.getUserName(), vpnScore2.getUserName());
		Assert.assertTrue( vpnScore.getTimestamp().equals(vpnScore2.getTimestamp()) || vpnScore.getTimestamp().before(vpnScore2.getTimestamp()) );
	}
	
	@Test
	public void testFindEventsByTimestampWithAdditionalWhereQuery() {
		Date lastRun = vpnDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(2, new Sort(Direction.DESC, VpnScore.EVENT_SCORE_FIELD_NAME));
		VpnScore vpnScore = vpnDAO.findEventsByTimestamp(lastRun, pageable).get(0);
		
		String additionalWhereQuery = String.format("%s  = \"%s\" and %s > %d", VpnScore.USERNAME_FIELD_NAME, vpnScore.getUserName(), VpnScore.EVENT_SCORE_FIELD_NAME, 0);
		List<VpnScore> vpnScores = vpnDAO.findEventsByTimestamp(lastRun, pageable, additionalWhereQuery);
		Assert.assertTrue(vpnScores.size() > 0 && vpnScores.size() <= 2);
		Assert.assertTrue(vpnScores.get(0).getTimestamp().equals(lastRun));
		Assert.assertTrue(vpnScores.get(0).getUserName().equals(vpnScore.getUserName()));
	}
	
	@Test
	public void testFindEventsByUsername() {
		Date lastRun = vpnDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(2, new Sort(Direction.DESC, VpnScore.EVENT_SCORE_FIELD_NAME));
		VpnScore vpnScore = vpnDAO.findEventsByTimestamp(lastRun, pageable).get(0);
		
		List<VpnScore> vpnScores = vpnDAO.findEventsByUsername(vpnScore.getUserName().toLowerCase(), pageable);
		Assert.assertTrue(vpnScores.size() > 0 && vpnScores.size() <= 2);
		Assert.assertTrue(vpnScores.get(0).getUserName().equals(vpnScore.getUserName()));
	}
	
	@Test
	public void testFindEventsByUsernameAndTimestamp() {
		Date lastRun = vpnDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(2, new Sort(Direction.DESC, VpnScore.EVENT_SCORE_FIELD_NAME));
		VpnScore vpnScore = vpnDAO.findEventsByTimestamp(lastRun, pageable).get(0);
		
		List<VpnScore> vpnScores = vpnDAO.findEventsByUsernameAndTimestamp(vpnScore.getUserName().toLowerCase(), lastRun, pageable);
		Assert.assertTrue(vpnScores.size() > 0 && vpnScores.size() <= 2);
		Assert.assertTrue(vpnScores.get(0).getTimestamp().equals(lastRun));
		Assert.assertTrue(vpnScores.get(0).getUserName().equals(vpnScore.getUserName()));
	}
	
	@Test
	public void testFindGlobalScoreByTimestamp() {
		Date lastRun = vpnDAO.getLastRunDate();
		List<VpnScore> vpnScores = vpnDAO.findGlobalScoreByTimestamp(lastRun);
		Assert.assertTrue(vpnScores.size() > 0);
		Assert.assertTrue(vpnScores.get(0).getTimestamp().equals(lastRun));
	}
	
	@Test
	public void testFindGlobalScoreByUsername() {
		Date lastRun = vpnDAO.getLastRunDate();
		Pageable pageable = new ImpalaPageRequest(2, new Sort(Direction.DESC, VpnScore.EVENT_SCORE_FIELD_NAME));
		VpnScore vpnScore = vpnDAO.findEventsByTimestamp(lastRun, pageable).get(0);
		
		List<VpnScore> vpnScores = vpnDAO.findGlobalScoreByUsername(vpnScore.getUserName().toLowerCase(), 2);
		Assert.assertTrue(vpnScores.size() > 0 && vpnScores.size() <= 2);
		Assert.assertTrue(vpnScores.get(0).getUserName().equals(vpnScore.getUserName()));
	}
}
