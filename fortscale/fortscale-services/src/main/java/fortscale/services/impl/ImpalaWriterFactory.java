package fortscale.services.impl;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fortscale.utils.impala.ImpalaParser;

@Component
public class ImpalaWriterFactory {
	
	@Autowired
	private ImpalaParser impalaParser;

	@Value("${user.ad.group.membership.score.csv.file.full.path:}")
	private String userAdScoreCsvFileFullPathString;
	@Value("${user.ad.total.score.csv.file.full.path:}")
	private String userTotalScoreCsvFileFullPathString;
	@Value("${user.id.to.app.username.csv.file.full.path:}")
	private String useridToAppUsernameCsvFileFullPathString;
	
	
	
	
	private static File getFile(String path) {
		String fileSeperator = File.separator;
		if(fileSeperator == null || fileSeperator.equals("\\")) {
			path = path.replace("/", "\\");
		}
		File file = new File(path);
		return file;
	}
	
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

	public ImpalaGroupsScoreWriter createImpalaGroupsScoreWriter(){
		if(StringUtils.isEmpty(userAdScoreCsvFileFullPathString)){
			return new ImpalaGroupsScoreWriter(impalaParser);
		}
		ImpalaGroupsScoreWriter writer = new ImpalaGroupsScoreWriter(getFile(userAdScoreCsvFileFullPathString), impalaParser);
		return writer;
	}
	
	public ImpalaTotalScoreWriter createImpalaTotalScoreWriter(){
		if(StringUtils.isEmpty(userTotalScoreCsvFileFullPathString)){
			return new ImpalaTotalScoreWriter(impalaParser);
		}
		ImpalaTotalScoreWriter writer = new ImpalaTotalScoreWriter(getFile(userTotalScoreCsvFileFullPathString), impalaParser);
		return writer;
	}
	
	public ImpalaUseridToAppUsernameWriter createImpalaUseridToAppUsernameWriter(){
		if(StringUtils.isEmpty(useridToAppUsernameCsvFileFullPathString)){
			return new ImpalaUseridToAppUsernameWriter(impalaParser);
		}
		ImpalaUseridToAppUsernameWriter writer = new ImpalaUseridToAppUsernameWriter(getFile(useridToAppUsernameCsvFileFullPathString), impalaParser);
		return writer;
	}
}
