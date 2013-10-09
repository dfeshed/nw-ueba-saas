package fortscale.domain.ad.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.RowMapper;

import fortscale.domain.ad.UserMachine;
import fortscale.domain.ad.dao.UserMachineDAO;
import fortscale.domain.impala.ImpalaDAO;



public class UserMachineDAOImpl extends ImpalaDAO<UserMachine> implements UserMachineDAO{
	
	@Autowired
	private JdbcOperations impalaJdbcTemplate;
	
	private String tableName = UserMachine.TABLE_NAME;
	
	
	@Override
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	
	@Override
	public String getInputFileHeaderDesc() {
		return UserMachine.implaValueTypeOrder;
	}
	
	
	@Override
	public List<UserMachine> findAll(){
		return findAll(null);
	}
	
	@Override
	public List<UserMachine> findAll(Pageable pageable){
		return super.findAll(pageable, new UserMachineMapper());
	}
		
	@Override
	public List<UserMachine> findByUsername(String username){
		List<UserMachine> ret = new ArrayList<UserMachine>();

		String query = String.format("select * from %s  where %s=\"%s\"", getTableName(), UserMachine.USERNAME_FIELD_NAME, username);
		ret.addAll(impalaJdbcTemplate.query(query, new UserMachineMapper()));
		
		return ret;
	}
	@Override
	public List<UserMachine> findByHostname(String hostname){
		List<UserMachine> ret = new ArrayList<UserMachine>();

		String query = String.format("select * from %s  where lower(%s)=\"%s\"", getTableName(), UserMachine.HOSTNAME_FIELD_NAME, hostname);
		ret.addAll(impalaJdbcTemplate.query(query, new UserMachineMapper()));
		
		return ret;
	}
	@Override
	public List<UserMachine> findByHostnameip(String hostnameip){
		List<UserMachine> ret = new ArrayList<UserMachine>();

		String query = String.format("select * from %s  where %s=\"%s\"", getTableName(), UserMachine.HOSTNAMEIP_FIELD_NAME, hostnameip);
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
		
		
	}


	public static String toCsvLine(UserMachine userMachine) {
		StringBuilder builder = new StringBuilder();
		appendValueToCsvLine(builder, userMachine.getUsername(), ",");
		appendValueToCsvLine(builder, userMachine.getHostname(), ",");
		appendValueToCsvLine(builder, Integer.toString(userMachine.getLogoncount()), ",");
		appendValueToCsvLine(builder, fromatDate(userMachine.getLastlogon()), ",");
		appendValueToCsvLine(builder, userMachine.getHostnameip(), "\n");
		return builder.toString();
	}
	
	public static String toCsvHeader() {
		StringBuilder builder = new StringBuilder();
		appendValueToCsvLine(builder, UserMachine.USERNAME_FIELD_NAME, ",");
		appendValueToCsvLine(builder, UserMachine.HOSTNAME_FIELD_NAME, ",");
		appendValueToCsvLine(builder, UserMachine.LOGONCOUNT_FIELD_NAME, ",");
		appendValueToCsvLine(builder, UserMachine.LASTLOGON_FIELD_NAME, ",");
		appendValueToCsvLine(builder, UserMachine.HOSTNAMEIP_FIELD_NAME, "\n");
		return builder.toString();
	}
	
	private static void appendValueToCsvLine(StringBuilder builder, String value, String deleimiter) {
		builder.append(value).append(deleimiter);
	}

	private static Date parseDate(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(UserMachine.DATE_FORMAT);
		return pattern.parse(dateString);
	}
	
	private static String fromatDate(Date date) {
		SimpleDateFormat pattern = new SimpleDateFormat(UserMachine.DATE_FORMAT);
		return pattern.format(date);
	}
}

