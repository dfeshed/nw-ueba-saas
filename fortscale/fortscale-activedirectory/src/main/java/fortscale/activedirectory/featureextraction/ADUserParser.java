package fortscale.activedirectory.featureextraction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ADUserParser {

	public static final String DATE_FORMAT = "yyyy/MM/dd'T'HH:mm:ss" ;
	public static final String ATTRIBUTE_OU_PREFIX = "OU=";
	
	
	public String parseOUFromDN(String dn) {
		int ouIndex = dn.indexOf(ATTRIBUTE_OU_PREFIX);

		return (ouIndex==-1) ? null : dn.substring(ouIndex);  
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
		return (null==memberOf || memberOf.isEmpty()) ? new String[]{} : memberOf.split(";");
	}
	
	
	public Double getAccountIsDisabledValue(String userAccountControl) {
		int flag = 0x00000002;
		return ((Integer.parseInt(userAccountControl) & flag) == flag) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getNoPasswordRequiresValue(String userAccountControl) {
		int flag = 0x00000020;
		return ((Integer.parseInt(userAccountControl) & flag) == flag) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getNormalUserAccountValue(String userAccountControl) {
		int flag = 0x00000200;
		return ((Integer.parseInt(userAccountControl) & flag) == flag) ? Feature.NEGATIVE_STATUS : Feature.POSITIVE_STATUS;
	} 
	
	
	public Double getInterdomainTrustAccountValue(String userAccountControl) {
		int flag = 0x00000800;
		return ((Integer.parseInt(userAccountControl) & flag) == flag) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getPasswordNeverExpiresValue(String userAccountControl) {
		int flag = 0x00010000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}
	
	
	public Double getDesKeyOnlyValue(String userAccountControl) {
		int flag = 0x00200000;
		return ((Integer.parseInt(userAccountControl) & flag) == flag) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getNumberOfSubordinates(String directReports) {
		return (null==directReports || directReports.isEmpty()) ? 0.0 : (directReports.split(";").length) ;
	}
		
}
