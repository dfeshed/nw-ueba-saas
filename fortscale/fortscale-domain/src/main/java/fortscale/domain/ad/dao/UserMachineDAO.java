package fortscale.domain.ad.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import fortscale.domain.ad.UserMachine;
import fortscale.utils.actdir.ADUserParser;



@Component
public class UserMachineDAO {
		
	// set the impalad host
		private static final String IMPALAD_HOST = "integ-cdh-01.Fortscale.dom";
		
		// port 21050 is the default impalad JDBC port 
		private static final String IMPALAD_JDBC_PORT = "21050";

		private static final String CONNECTION_URL = "jdbc:hive2://" + IMPALAD_HOST + ':' + IMPALAD_JDBC_PORT + "/;auth=noSasl";

		private static final String JDBC_DRIVER_NAME = "org.apache.hive.jdbc.HiveDriver";
	
	public List<UserMachine> findByUsername(String username){
		Connection con = null;
		List<UserMachine> ret = new ArrayList<UserMachine>();
		try {
			Class.forName(JDBC_DRIVER_NAME);
	
			con = DriverManager.getConnection(CONNECTION_URL);
	
			Statement stmt = con.createStatement();
	
			ResultSet rs = stmt.executeQuery(String.format("select * from %s  where %s=\"%s\"", UserMachine.TABLE_NAME, UserMachine.USERNAME_FIELD_NAME, username));
			
			while (rs.next()) {
				UserMachine userMachine = new UserMachine();
				userMachine.setHostname(rs.getString(UserMachine.HOSTNAME_FIELD_NAME));
				userMachine.setHostnameip(rs.getString(UserMachine.HOSTNAMEIP_FIELD_NAME));
				userMachine.setLastlogon(new ADUserParser().parseDate(rs.getString(UserMachine.LASTLOGON_FIELD_NAME)));
				userMachine.setLogoncount(Integer.parseInt(rs.getString(UserMachine.LOGONCOUNT_FIELD_NAME)));
				userMachine.setUsername(rs.getString(UserMachine.USERNAME_FIELD_NAME));
				ret.add(userMachine);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NumberFormatException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (ParseException e) {
			// TODO: handle exception
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				con.close();
			} catch (Exception e) {
				// swallow
			}
		}
		
		return ret;
	}
}
