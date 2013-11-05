package fortscale.services.impl;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ImpalaScoreWriterFactory {

	@Value("${user.ad.group.membership.score.csv.file.full.path}")
	private String userAdScoreCsvFileFullPathString;
	
	
	
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
	
	
	public ImpalaGroupsScoreWriter createImpalaGroupsScoreWriter(){
		ImpalaGroupsScoreWriter writer = new ImpalaGroupsScoreWriter(getFile(userAdScoreCsvFileFullPathString));
		return writer;
	}
	
}
