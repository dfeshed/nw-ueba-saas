package fortscale.collection.morphlines;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.springframework.core.io.Resource;

public class ExactMatcherFileList extends MatcherFileList{
	private Set<String> matchValSet;
	
	public ExactMatcherFileList(Resource resource){
		super(resource);
	}

	public ExactMatcherFileList(File f){
		super(f);
	}
	
	public boolean isMatch(String val){
		if (matchValSet == null || val == null){
			return false;
		}
		
		return matchValSet.contains(val);
	}

	@Override
	protected void addToMatcher(String matchVal) {
		if(matchValSet == null){
			matchValSet = new HashSet<>();
		}
		matchValSet.add(matchVal);
	}
}
