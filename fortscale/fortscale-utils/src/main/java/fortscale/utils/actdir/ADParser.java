package fortscale.utils.actdir;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class ADParser {
	public static final String[] DATE_FORMATS = { "yyyy/MM/dd'T'HH:mm:ss", "yyyyMMddHHmmss'.0Z'" };
	public static final String TIMESTAMP_FORMAT = "yyyy/MM/dd'T'HH:mm" ;
	public static final String ATTRIBUTE_OU_PREFIX = "OU=";
	public static final String ATTRIBUTE_CN_PREFIX = "CN=";
	public static final String ATTRIBUTE_DC_PREFIX = ",DC=";
	
	
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

	public String parseDCFromDN(String dn) {

		String [] splited = dn.split(ATTRIBUTE_DC_PREFIX);
		String ret = "";
		for (int i=0; i<splited.length-1; i++)
		{
			ret+=splited[i+1]+".";
		}

		if (ret.length()>0)
			ret = ret.substring(0,ret.length()-1);

		return ret;
	}
	
	public static String parseFirstCNFromDN(String dn) {
		int cnStartIndex = dn.indexOf(ATTRIBUTE_CN_PREFIX);
		if(cnStartIndex == -1){
			return null;
		} else{
			cnStartIndex = cnStartIndex + 3;
			if(cnStartIndex > dn.length()){
				return null;
			}
		}
		int cnLastIndex = dn.indexOf(",", cnStartIndex);
		if(cnLastIndex == -1){
			cnLastIndex = dn.length();
		}
		return dn.substring(cnStartIndex, cnLastIndex);
	}
	
	
	public Date parseTimestamp(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(TIMESTAMP_FORMAT);
		return pattern.parse(dateString);
	}
	
	
	public Date parseDate(String dateString) throws ParseException {
		for (String DATE_FORMAT: DATE_FORMATS) {
			SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
			pattern.setTimeZone(TimeZone.getTimeZone("UTC"));
			try {
				return pattern.parse(dateString);
			} catch (ParseException ex) {}
		}
		throw new ParseException("failed to parse date", 0);
	}

	
	public double getActivityDays(String whenCreated) throws ParseException {
		Date starting_date = this.parseDate(whenCreated);
		Date now = new Date();
		long timeDifInMilliSec2 = now.getTime() - starting_date.getTime() ;
		double activityDays = timeDifInMilliSec2 / (24 * 60 * 60 * 1000.0);
		return activityDays;
	}

	
	public static String[] getUserGroups(String memberOf) {
		return StringUtils.isEmpty(memberOf) ? new String[]{} : memberOf.split(";");
	}
	
	public static String[] getDirectReports(String directReports) {
		return StringUtils.isEmpty(directReports) ? new String[]{} : directReports.split(";");
	}
	
	public Double getNumberOfSubordinates(String directReports) {
		return (null==directReports || directReports.isEmpty()) ? 0.0 : (directReports.split(";").length) ;
	}
	
	
	public boolean isAccountIsDisabled(Integer userAccountControl) {
		int flag = 0x00000002;
		return ((userAccountControl!=null) && ((userAccountControl & flag) == flag));
	}
	
	public boolean isLockout(Integer userAccountControl) {
		int flag = 0x00000010;
		return ((userAccountControl & flag) == flag);
	}
	
	public boolean isWorkstationTrustAccount(Integer userAccountControl) {
		int flag = 0x00001000;
		return ((userAccountControl & flag) == flag);
	}
	
	
	public boolean isServerTrustAccount(Integer userAccountControl) {
		int flag = 0x00002000;
		return ((userAccountControl & flag) == flag);
	}
	
	public boolean isSmartcardRequired(Integer userAccountControl) {
		int flag = 0x00040000;
		return ((userAccountControl & flag) == flag);
	}
	
	
	public boolean isTrustedForDelegation(Integer userAccountControl) {
		int flag = 0x00080000;
		return ((userAccountControl & flag) == flag);
	}
	
	public boolean isNotDelegated(Integer userAccountControl) {
		int flag = 0x00100000;
		return ((userAccountControl & flag) == flag);
	}
	
	public boolean isPasswordExpired(Integer userAccountControl) {
		int flag = 0x00800000;
		return ((userAccountControl & flag) == flag);
	}
	
	public boolean isTrustedToAuthForDelegation(Integer userAccountControl) {
		int flag = 0x01000000;
		return ((userAccountControl & flag) == flag);
	}

	
	public boolean isNoPasswordRequiresValue(Integer userAccountControl) {
		int flag = 0x00000020;
		return ((userAccountControl & flag) == flag);
	}

	
	public boolean isNormalUserAccountValue(Integer userAccountControl) {
		int flag = 0x00000200;
		return ((userAccountControl & flag) == flag);
	} 
	
	
	public boolean isInterdomainTrustAccountValue(Integer userAccountControl) {
		int flag = 0x00000800;
		return ((userAccountControl & flag) == flag);
	}

	
	public boolean isPasswordNeverExpiresValue(Integer userAccountControl) {
		int flag = 0x00010000;
		return ((userAccountControl & flag) == flag);
	}
	
	
	public boolean isDesKeyOnlyValue(Integer userAccountControl) {
		int flag = 0x00200000;
		return ((userAccountControl & flag) == flag);
	}

	
	
		
}
