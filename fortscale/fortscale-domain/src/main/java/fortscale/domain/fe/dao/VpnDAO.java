package fortscale.domain.fe.dao;


public abstract class VpnDAO extends AccessDAO implements EventScoreDAO{
	
	public abstract String getStatusFieldName();
	
	public abstract String getCountryFieldName();
	
	public abstract String getRegionFieldName();
	
	public abstract String getCityFieldName();
	
	public abstract String getIspFieldName();
		
	public abstract String getIpusageFieldName();
	
	public abstract String getSourceIpFieldName();
	
	public abstract String getLocalIpFieldName();
	
	
}
