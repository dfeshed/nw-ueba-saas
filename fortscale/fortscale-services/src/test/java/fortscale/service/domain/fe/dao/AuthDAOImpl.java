package fortscale.service.domain.fe.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.Pageable;

import fortscale.domain.fe.AuthScore;
import fortscale.domain.fe.dao.AuthDAO;
import fortscale.domain.fe.dao.Threshold;

public class AuthDAOImpl implements AuthDAO {
	
	@Override
	public AuthScore findCurrentByUsername(String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuthScore> findEventsByUsername(String username,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuthScore> findEventsByUsernameAndTimestamp(String username,
			Date timestamp, Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuthScore> findEventsByTimestamp(Date timestamp,
			Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuthScore> findEventsByTimestamp(Date timestamp,
			Pageable pageable, String additionalWhereQuery) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuthScore> findGlobalScoreByUsername(String username, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuthScore> findGlobalScoreByTimestamp(Date timestamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countNumOfUsersAboveThreshold(Threshold threshold, Date timestamp) {
		if(threshold.getValue() == 100) {
			return 0;
		}
		int val = 100 - threshold.getValue();
		val = val * 10 + val / 10 + 1;
		if(threshold.getValue() == 0) {
			val = val * 4;
		}
		
		return val;
	}
	
	

	@Override
	public Date getLastRunDate() {
		return new Date();
	}

	@Override
	public double calculateAvgScoreOfGlobalScore(Date timestamp) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<AuthScore> getTopUsersAboveThreshold(Threshold threshold,
			Date timestamp, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuthScore> findByTimestampAndGlobalScoreBetweenSortByEventScore(
			Date timestamp, int lowestVal, int upperVal, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuthScore> getTopEventsAboveThreshold(Threshold threshold,
			Date timestamp, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<AuthScore> findAll(Pageable pageable) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Long getLastRuntime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Long> getDistinctRuntime() {
		// TODO Auto-generated method stub
		return null;
	}

}
