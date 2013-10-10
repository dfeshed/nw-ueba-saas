package fortscale.service.domain.fe.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.VpnScore;
import fortscale.domain.fe.dao.Threshold;
import fortscale.domain.fe.dao.VpnDAO;

public class VpnDAOImpl implements VpnDAO {

	@Override
	public List<VpnScore> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public VpnScore findCurrentByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VpnScore> findEventsByUsername(String username,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VpnScore> findEventsByUsernameAndTimestamp(String username,
			Date timestamp, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VpnScore> findEventsByTimestamp(Date timestamp,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VpnScore> findEventsByTimestamp(Date timestamp,
			Pageable pageable, String additionalWhereQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VpnScore> findGlobalScoreByUsername(String username, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VpnScore> findGlobalScoreByTimestamp(Date timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countNumOfUsersAboveThreshold(Threshold threshold, Date timestamp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Date getLastRunDate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double calculateAvgScoreOfGlobalScore(Date timestamp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<VpnScore> getTopUsersAboveThreshold(Threshold threshold,
			Date timestamp, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VpnScore> findByTimestampAndGlobalScoreBetweenSortByEventScore(
			Date timestamp, int lowestVal, int upperVal, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VpnScore> getTopEventsAboveThreshold(Threshold threshold,
			Date timestamp, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

}
