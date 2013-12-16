package fortscale.services.domain.fe.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.Threshold;
import fortscale.domain.fe.dao.VpnDAO;

public class VpnDAOImpl implements VpnDAO {

	@Override
	public List<VpnScore> findAll(Pageable pageable) {
		
		return null;
	}

	@Override
	public VpnScore findCurrentByUsername(String username) {
		
		return null;
	}

	@Override
	public List<VpnScore> findEventsByUsername(String username,
			Pageable pageable) {
		
		return null;
	}

	@Override
	public List<VpnScore> findEventsByUsernameAndTimestamp(String username,
			Date timestamp, Pageable pageable) {
		
		return null;
	}

	@Override
	public List<VpnScore> findEventsByTimestamp(Date timestamp,
			Pageable pageable) {
		
		return null;
	}

	@Override
	public List<VpnScore> findEventsByTimestamp(Date timestamp,
			Pageable pageable, String additionalWhereQuery) {
		
		return null;
	}

	@Override
	public List<VpnScore> findGlobalScoreByUsername(String username, int limit) {
		
		return null;
	}

	@Override
	public List<VpnScore> findGlobalScoreByTimestamp(Date timestamp) {
		
		return null;
	}

	@Override
	public int countNumOfUsersAboveThreshold(Threshold threshold, Date timestamp) {
		
		return 0;
	}

	@Override
	public Date getLastRunDate() {
		
		return null;
	}

	@Override
	public double calculateAvgScoreOfGlobalScore(Date timestamp) {
		
		return 0;
	}

	@Override
	public List<VpnScore> getTopUsersAboveThreshold(Threshold threshold,
			Date timestamp, int limit) {
		
		return null;
	}

	@Override
	public List<VpnScore> findByTimestampAndGlobalScoreBetweenSortByEventScore(
			Date timestamp, int lowestVal, int upperVal, int limit) {
		
		return null;
	}

	@Override
	public List<VpnScore> getTopEventsAboveThreshold(Threshold threshold,
			Date timestamp, int limit) {
		
		return null;
	}

	@Override
	public Long getLastRuntime() {
		
		return null;
	}

	@Override
	public List<Long> getDistinctRuntime() {
		
		return null;
	}

	@Override
	public int countNumOfUsers(Date timestamp) {
		
		return 0;
	}

	@Override
	public int countNumOfEvents(Date timestamp) {
		
		return 0;
	}

	@Override
	public int countNumOfEventsByUser(Date timestamp, String userId) {
		
		return 0;
	}

	@Override
	public List<VpnScore> findEventsByUsernameAndTimestampGtEventScore(
			String username, Date timestamp, int minScore, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VpnScore> findEventsByTimestampGtEventScore(Date timestamp,
			Pageable pageable, int minScore) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countNumOfEventsByUserAndStatusRegex(Date timestamp, String username, String statusVal) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<VpnScore> findEventsByTimestampGtEventScoreInUsernameList(
			Date timestamp, Pageable pageable, Integer minScore,
			Collection<String> usernames) {
		// TODO Auto-generated method stub
		return null;
	}

}
