package fortscale.collection.morphlines;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

import org.springframework.core.io.Resource;

public class RegexMatcherFileList extends MatcherFileList{	
	private ArrayList<Pattern> patternList;
	
	public RegexMatcherFileList(Resource resource){
		super(resource);
	}

	public RegexMatcherFileList(File f){
		super(f);
	}
	
	public boolean isMatch(String val){
		if (patternList == null || val == null){
			return false;
		}
		
		for (Pattern pattern : patternList) {
			if (pattern.matcher(val).matches()) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	protected void addToMatcher(String matchVal) {
		if(patternList == null){
			patternList = new ArrayList<Pattern>();
		}
		patternList.add(Pattern.compile(matchVal));
	}
}
