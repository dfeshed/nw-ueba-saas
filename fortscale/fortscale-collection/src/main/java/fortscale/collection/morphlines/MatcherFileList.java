package fortscale.collection.morphlines;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.Resource;

import fortscale.utils.logging.Logger;

public abstract class MatcherFileList {
	private static Logger logger = Logger.getLogger(MatcherFileList.class);
		
	public MatcherFileList(Resource resource){
		try{
			init(resource.getFile());
		} catch(Exception e){
			logger.error("Got an exception while calling get file of resource {}", resource.getFilename());
		}
	}

	public MatcherFileList(File f){
		init(f);
	}
	
	private void init(File f){
		try{
			if (f.exists() && f.isFile()) {
				ArrayList<String> matchVals = new ArrayList<String>(FileUtils.readLines(f));
				for (String matchVal : matchVals) {
					addToMatcher(matchVal);
				}
			}
		} catch(Exception e){
			logger.error("Got an exception while loading the regex file", e);
		}
	}
	
	protected abstract void addToMatcher(String matchVal);
	
	public abstract boolean isMatch(String val);
}
