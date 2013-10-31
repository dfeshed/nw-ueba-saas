package fortscale.utils.actdir;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

public class ADUserParser {
	public static final String DATE_FORMAT = "yyyy/MM/dd'T'HH:mm:ss" ;
	public static final String TIMESTAMP_FORMAT = "yyyy/MM/dd'T'HH:mm" ;
	public static final String ATTRIBUTE_OU_PREFIX = "OU=";
	
	
	public String parseOUFromDN(String dn) {
		int ouStartIndex = dn.indexOf(ATTRIBUTE_OU_PREFIX);
		if(ouStartIndex == -1){
			return null;
		}
		int ouLastIndex = ouStartIndex;
		int tmp = ouLastIndex;
		do{
			ouLastIndex = tmp;
			tmp = dn.indexOf(ATTRIBUTE_OU_PREFIX, ouLastIndex+1);
		}while(tmp != -1);

		ouLastIndex = dn.indexOf(",", ouLastIndex);
		if(ouLastIndex == -1){
			ouLastIndex = dn.length();
		}
		return dn.substring(ouStartIndex, ouLastIndex);  
	}
	
	
	public Date parseTimestamp(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(TIMESTAMP_FORMAT);
		return pattern.parse(dateString);
	}
	
	
	public Date parseDate(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
		return pattern.parse(dateString);
	}

	
	public double getActivityDays(String whenCreated) throws ParseException {
		Date starting_date = this.parseDate(whenCreated);
		Date now = new Date();
		long timeDifInMilliSec2 = now.getTime() - starting_date.getTime() ;
		double activityDays = timeDifInMilliSec2 / (24 * 60 * 60 * 1000.0);
		return activityDays;
	}

	
	public String[] getUserGroups(String memberOf) {
		return StringUtils.isEmpty(memberOf) ? new String[]{} : memberOf.split(";");
	}
	
	public String[] getDirectReports(String directReports) {
		return StringUtils.isEmpty(directReports) ? new String[]{} : directReports.split(";");
	}
	
	public Double getNumberOfSubordinates(String directReports) {
		return (null==directReports || directReports.isEmpty()) ? 0.0 : (directReports.split(";").length) ;
	}
	
	
	public boolean isAccountIsDisabled(String userAccountControl) {
		int flag = 0x00000002;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}
	
	public boolean isLockout(String userAccountControl) {
		int flag = 0x00000010;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}
	
	public boolean isWorkstationTrustAccount(String userAccountControl) {
		int flag = 0x00001000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}
	
	
	public boolean isServerTrustAccount(String userAccountControl) {
		int flag = 0x00002000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}
	
	public boolean isSmartcardRequired(String userAccountControl) {
		int flag = 0x00040000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}
	
	
	public boolean isTrustedForDelegation(String userAccountControl) {
		int flag = 0x00080000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}
	
	public boolean isNotDelegated(String userAccountControl) {
		int flag = 0x00100000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}
	
	public boolean isPasswordExpired(String userAccountControl) {
		int flag = 0x00800000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}
	
	public boolean isTrustedToAuthForDelegation(String userAccountControl) {
		int flag = 0x01000000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}

	
	public boolean isNoPasswordRequiresValue(String userAccountControl) {
		int flag = 0x00000020;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}

	
	public boolean isNormalUserAccountValue(String userAccountControl) {
		int flag = 0x00000200;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	} 
	
	
	public boolean isInterdomainTrustAccountValue(String userAccountControl) {
		int flag = 0x00000800;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}

	
	public boolean isPasswordNeverExpiresValue(String userAccountControl) {
		int flag = 0x00010000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}
	
	
	public boolean isDesKeyOnlyValue(String userAccountControl) {
		int flag = 0x00200000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag);
	}

	
	
		
}
