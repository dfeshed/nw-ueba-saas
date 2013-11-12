package fortscale.services.domain.jdbc;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.ConnectionCallback;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.ParameterizedPreparedStatementSetter;
import org.springframework.jdbc.core.PreparedStatementCallback;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.StatementCallback;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.jdbc.support.rowset.SqlRowSet;

public class JdbcOperationsTest implements JdbcOperations {

	@Override
	public int[] batchUpdate(String[] arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] batchUpdate(String arg0, BatchPreparedStatementSetter arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] batchUpdate(String arg0, List<Object[]> arg1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int[] batchUpdate(String arg0, List<Object[]> arg1, int[] arg2) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> int[][] batchUpdate(String arg0, Collection<T> arg1, int arg2,
			ParameterizedPreparedStatementSetter<T> arg3) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> call(CallableStatementCreator arg0,
			List<SqlParameter> arg1) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(ConnectionCallback<T> arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(StatementCallback<T> arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(String arg0) throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> T execute(PreparedStatementCreator arg0,
			PreparedStatementCallback<T> arg1) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(String arg0, PreparedStatementCallback<T> arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(CallableStatementCreator arg0,
			CallableStatementCallback<T> arg1) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T execute(String arg0, CallableStatementCallback<T> arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T query(String arg0, ResultSetExtractor<T> arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void query(String arg0, RowCallbackHandler arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> List<T> query(String arg0, RowMapper<T> arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T query(PreparedStatementCreator arg0, ResultSetExtractor<T> arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void query(PreparedStatementCreator arg0, RowCallbackHandler arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> List<T> query(PreparedStatementCreator arg0, RowMapper<T> arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T query(String arg0, PreparedStatementSetter arg1,
			ResultSetExtractor<T> arg2) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T query(String arg0, Object[] arg1, ResultSetExtractor<T> arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T query(String arg0, ResultSetExtractor<T> arg1, Object... arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void query(String arg0, PreparedStatementSetter arg1,
			RowCallbackHandler arg2) throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public void query(String arg0, Object[] arg1, RowCallbackHandler arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public void query(String arg0, RowCallbackHandler arg1, Object... arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> List<T> query(String arg0, PreparedStatementSetter arg1,
			RowMapper<T> arg2) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> query(String arg0, Object[] arg1, RowMapper<T> arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> query(String arg0, RowMapper<T> arg1, Object... arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T query(String arg0, Object[] arg1, int[] arg2,
			ResultSetExtractor<T> arg3) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void query(String arg0, Object[] arg1, int[] arg2,
			RowCallbackHandler arg3) throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public <T> List<T> query(String arg0, Object[] arg1, int[] arg2,
			RowMapper<T> arg3) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int queryForInt(String arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int queryForInt(String arg0, Object... arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int queryForInt(String arg0, Object[] arg1, int[] arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<Map<String, Object>> queryForList(String arg0)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> queryForList(String arg0, Class<T> arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> queryForList(String arg0, Object... arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> queryForList(String arg0, Object[] arg1, Class<T> arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> queryForList(String arg0, Class<T> arg1, Object... arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> queryForList(String arg0, Object[] arg1,
			int[] arg2) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> queryForList(String arg0, Object[] arg1, int[] arg2,
			Class<T> arg3) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long queryForLong(String arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long queryForLong(String arg0, Object... arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long queryForLong(String arg0, Object[] arg1, int[] arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Map<String, Object> queryForMap(String arg0)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> queryForMap(String arg0, Object... arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Object> queryForMap(String arg0, Object[] arg1,
			int[] arg2) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T queryForObject(String arg0, RowMapper<T> arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T queryForObject(String arg0, Class<T> arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T queryForObject(String arg0, Object[] arg1, RowMapper<T> arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T queryForObject(String arg0, RowMapper<T> arg1, Object... arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T queryForObject(String arg0, Object[] arg1, Class<T> arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T queryForObject(String arg0, Class<T> arg1, Object... arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T queryForObject(String arg0, Object[] arg1, int[] arg2,
			RowMapper<T> arg3) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T queryForObject(String arg0, Object[] arg1, int[] arg2,
			Class<T> arg3) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlRowSet queryForRowSet(String arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlRowSet queryForRowSet(String arg0, Object... arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SqlRowSet queryForRowSet(String arg0, Object[] arg1, int[] arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int update(String arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(PreparedStatementCreator arg0) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(PreparedStatementCreator arg0, KeyHolder arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(String arg0, PreparedStatementSetter arg1)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(String arg0, Object... arg1) throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int update(String arg0, Object[] arg1, int[] arg2)
			throws DataAccessException {
		// TODO Auto-generated method stub
		return 0;
	}

}
