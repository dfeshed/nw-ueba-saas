package fortscale.domain.ad.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import fortscale.domain.ad.UserMachine;



@Repository
public class UserMachineDAO {
	
	public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss.S" ;
	
	@Autowired
	private JdbcOperations impalaJdbcTemplate;
	
	public List<UserMachine> findByUsername(String username){
		List<UserMachine> ret = new ArrayList<UserMachine>();

		String query = String.format("select * from %s  where %s=\"%s\"", UserMachine.TABLE_NAME, UserMachine.USERNAME_FIELD_NAME, username);
		ret.addAll(impalaJdbcTemplate.query(query, new UserMachineMapper()));
		
		return ret;
	}
	
	
	
	
	class UserMachineMapper implements RowMapper<UserMachine>{

		@Override
		public UserMachine mapRow(ResultSet rs, int rowNum) throws SQLException {
			UserMachine userMachine = new UserMachine();
			try{
				userMachine.setHostname(rs.getString(UserMachine.HOSTNAME_FIELD_NAME));
				userMachine.setHostnameip(rs.getString(UserMachine.HOSTNAMEIP_FIELD_NAME));
				userMachine.setLastlogon(parseDate(rs.getString(UserMachine.LASTLOGON_FIELD_NAME)));
				userMachine.setLogoncount(Integer.parseInt(rs.getString(UserMachine.LOGONCOUNT_FIELD_NAME)));
				userMachine.setUsername(rs.getString(UserMachine.USERNAME_FIELD_NAME));
			} catch (NumberFormatException e) {
				throw new SQLException(e);
			} catch (ParseException e) {
				throw new SQLException(e);
			}
			
			return userMachine;
		}
		
		private Date parseDate(String dateString) throws ParseException {
			SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
			return pattern.parse(dateString);
		}
	}
}
