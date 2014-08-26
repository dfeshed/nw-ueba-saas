package fortscale.services.impl;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;



public class ParsingUsersMachinesFiltering {
	
	static public ArrayList<Pair<String, UsersMachinesFilterEnum>> getFiltersList(String filtersStr){
		if(!filtersStr.startsWith("[") || !filtersStr.endsWith("]")){
			throw new IllegalArgumentException("Filters list must be enclosed with []");
		}
		filtersStr = filtersStr.substring(1, filtersStr.length()-1);
		String[] filtersList = filtersStr.split("\\s*;\\s*");
		ArrayList<Pair<String, UsersMachinesFilterEnum>> filtersPriorityList = new ArrayList<Pair<String, UsersMachinesFilterEnum>> ();
		for(String filter : filtersList){
			String regex = "\\s*\"(.*)\"\\s*:\\s*\"(.*)\"\\s*";
			Pattern pattern = Pattern.compile(regex);
			Matcher m = pattern.matcher(filter);
			if(m.matches() == false){
				throw new IllegalArgumentException("Bad filter list format");
			}
			String filterName = m.group(1);
			UsersMachinesFilterEnum filterKind;
			if(m.group(2).equals("group")){
				filterKind = UsersMachinesFilterEnum.GROUP;
			}else if(m.group(2).equals("ou")){
				filterKind = UsersMachinesFilterEnum.OU;
			}else{
				throw new IllegalArgumentException("Filter must be either a group or OU");
			}
			filtersPriorityList.add(new ImmutablePair<String, UsersMachinesFilterEnum> (filterName, filterKind));
		}
		return filtersPriorityList;
	}
}
