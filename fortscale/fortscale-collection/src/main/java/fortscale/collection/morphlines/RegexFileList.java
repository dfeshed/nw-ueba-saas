package fortscale.collection.morphlines;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;

import fortscale.utils.logging.Logger;

public class RegexFileList {
	private static Logger logger = Logger.getLogger(RegexFileList.class);
	
	private ArrayList<Pattern> sshUsersRegList;
	
	public RegexFileList(Resource resource){
		try{
			init(resource.getFile());
		} catch(Exception e){
			logger.error("Got an exception while calling get file of resource {}", resource.getFilename());
		}
	}

	public RegexFileList(File f){
		init(f);
	}
	
	private void init(File f){
		try{
			if (f.exists() && f.isFile()) {
				sshUsersRegList = new ArrayList<Pattern>();
				ArrayList<String> usersRegex = new ArrayList<String>(FileUtils.readLines(f));
				for (String regex : usersRegex) {
					sshUsersRegList.add(Pattern.compile(regex));
				}
			}
		} catch(Exception e){
			logger.error("Got an exception while loading the regex file", e);
		}
	}
	
	public boolean isMatch(String val){
		if (sshUsersRegList == null){
			return false;
		}
		
		for (Pattern userPattern : sshUsersRegList) {
			if (userPattern.matcher(val).matches()) {
				return true;
			}
		}
		
		return false;
	}
}
