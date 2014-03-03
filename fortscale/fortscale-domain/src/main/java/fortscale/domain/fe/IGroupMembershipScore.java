package fortscale.domain.fe;

import fortscale.utils.impala.ImpalaDateTime;

public interface IGroupMembershipScore {
	public Long getRuntime();
	
	public String getUid();
	
	public String getDn();
	
	public String getUsername();
	
	public Double getScore();
	
	public Double getAvgScore();
	
	public ImpalaDateTime getTime_stamp();
	
	public String getGroup_dn();
	
	public Double getFscore();
	
	public Double getFdist();
	
	public Integer getFcount();
	public String getFref();
	public String getFrefs();
}
