package fortscale.activedirectory.featureextraction;

import fortscale.utils.actdir.ADUserParser;


public class ADUserFeatureParser extends ADUserParser{
	
	public Double getAccountIsDisabledValue(Integer userAccountControl) {
		return isAccountIsDisabled(userAccountControl) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getNoPasswordRequiresValue(Integer userAccountControl) {
		return isNoPasswordRequiresValue(userAccountControl) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getNormalUserAccountValue(Integer userAccountControl) {
		return isNormalUserAccountValue(userAccountControl) ? Feature.NEGATIVE_STATUS : Feature.POSITIVE_STATUS;
	} 
	
	
	public Double getInterdomainTrustAccountValue(Integer userAccountControl) {
		return isInterdomainTrustAccountValue(userAccountControl) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getPasswordNeverExpiresValue(Integer userAccountControl) {
		return isPasswordNeverExpiresValue(userAccountControl) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}
	
	
	public Double getDesKeyOnlyValue(Integer userAccountControl) {
		return isDesKeyOnlyValue(userAccountControl) ? Feature.POSITIVE_STATUS : Feature.NEGATIVE_STATUS;
	}

	
	public Double getNumberOfSubordinates(String directReports) {
		return (null==directReports || directReports.isEmpty()) ? 0.0 : (directReports.split(";").length) ;
	}
		
}
