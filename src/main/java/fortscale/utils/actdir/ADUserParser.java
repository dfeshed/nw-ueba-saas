package fortscale.utils.actdir;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;



public class ADUserParser {

	public static final String DATE_FORMAT = "yyyy/MM/dd'T'HH:mm:ss" ;
	
	
	
	public Date parseDate(String dateString) throws ParseException {
		SimpleDateFormat pattern = new SimpleDateFormat(DATE_FORMAT);
		return pattern.parse(dateString);
	}

	
	public String[] getUserGroups(String memberOf) {
		return memberOf.isEmpty() ? null : memberOf.split(";");
	}
	
	
		
}
