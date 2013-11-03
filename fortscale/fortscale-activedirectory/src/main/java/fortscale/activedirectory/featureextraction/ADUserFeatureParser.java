package fortscale.activedirectory.featureextraction;

import fortscale.utils.actdir.ADUserParser;


public class ADUserFeatureParser extends ADUserParser{
	
	public Double getAccountIsDisabledValue(String userAccountControl) {
		return isAccountIsDisabled(userAccountControl) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getNoPasswordRequiresValue(String userAccountControl) {
		return isNoPasswordRequiresValue(userAccountControl) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getNormalUserAccountValue(String userAccountControl) {
		return isNormalUserAccountValue(userAccountControl) ? Feature.NEGATIVE_STATUS : Feature.POSITIVE_STATUS;
	} 
	
	
	public Double getInterdomainTrustAccountValue(String userAccountControl) {
		return isInterdomainTrustAccountValue(userAccountControl) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getPasswordNeverExpiresValue(String userAccountControl) {
		return isPasswordNeverExpiresValue(userAccountControl) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}
	
	
	public Double getDesKeyOnlyValue(String userAccountControl) {
		return isDesKeyOnlyValue(userAccountControl) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getNumberOfSubordinates(String directReports) {
		return (null==directReports || directReports.isEmpty()) ? 0.0 : (directReports.split(";").length) ;
	}
		
}
