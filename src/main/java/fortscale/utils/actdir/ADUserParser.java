package fortscale.utils.actdir;



public class ADUserParser {

	

	
	public String[] getUserGroups(String memberOf) {
		return memberOf.isEmpty() ? null : memberOf.split(";");
	}
	
	
		
}
