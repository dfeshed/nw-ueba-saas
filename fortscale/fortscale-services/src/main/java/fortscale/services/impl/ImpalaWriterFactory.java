package fortscale.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import fortscale.utils.impala.ImpalaParser;

public abstract class ImpalaWriterFactory {
	
	@Autowired
	protected ImpalaParser impalaParser;

	@Value("${user.ad.group.membership.score.csv.file.full.path:}")
	protected String userAdScoreCsvFileFullPathString;
	@Value("${user.ad.total.score.csv.file.full.path:}")
	protected String userTotalScoreCsvFileFullPathString;
	@Value("${user.id.to.app.username.csv.file.full.path:}")
	protected String useridToAppUsernameCsvFileFullPathString;
		
	public void setUserAdScoreCsvFileFullPathString(String userAdScoreCsvFileFullPathString) {
		this.userAdScoreCsvFileFullPathString = userAdScoreCsvFileFullPathString;
	}
	
	public void setUserTotalScoreCsvFileFullPathString(String userTotalScoreCsvFileFullPathString) {
		this.userTotalScoreCsvFileFullPathString = userTotalScoreCsvFileFullPathString;
	}
	
	public void setUseridToAppUsernameCsvFileFullPathString(
			String useridToAppUsernameCsvFileFullPathString) {
		this.useridToAppUsernameCsvFileFullPathString = useridToAppUsernameCsvFileFullPathString;
	}
	public abstract ImpalaUseridToAppUsernameWriter createImpalaUseridToAppUsernameWriter();
}
