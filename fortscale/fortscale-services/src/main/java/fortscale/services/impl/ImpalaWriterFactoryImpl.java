package fortscale.services.impl;


import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Value;

import fortscale.utils.impala.ImpalaParser;


public class ImpalaWriterFactoryImpl extends ImpalaWriterFactory{
	
	@Value("${impala.ldap.group.membership.scores.table.fields}")
	private String impalaGroupMembershipScoringTableFields;
	@Value("${impala.ldap.group.membership.scores.table.delimiter}")
	private String impalaGroupMembershipScoringTableDelimiter;
	
	@Value("${impala.total.scores.table.fields}")
	private String impalaTotalScoringTableFields;
	@Value("${impala.total.scores.table.delimiter}")
	private String impalaTotalScoringTableDelimiter;
	
	private static File getFile(String path) {
		String fileSeperator = File.separator;
		if(fileSeperator == null || fileSeperator.equals("\\")) {
			path = path.replace("/", "\\");
		}
		File file = new File(path);
		return file;
	}
	

	public ImpalaGroupsScoreWriter createImpalaGroupsScoreWriter(){
		if(StringUtils.isEmpty(userAdScoreCsvFileFullPathString)){
			return new ImpalaGroupsScoreWriter(impalaParser, ImpalaParser.getTableFieldNames(impalaGroupMembershipScoringTableFields), impalaGroupMembershipScoringTableDelimiter);
		} else{
			return new ImpalaGroupsScoreWriter(getFile(userAdScoreCsvFileFullPathString), impalaParser, ImpalaParser.getTableFieldNames(impalaGroupMembershipScoringTableFields), impalaGroupMembershipScoringTableDelimiter);
		}
	}
	
	public ImpalaTotalScoreWriter createImpalaTotalScoreWriter(){
		if(StringUtils.isEmpty(userTotalScoreCsvFileFullPathString)){
			return new ImpalaTotalScoreWriter(impalaParser, ImpalaParser.getTableFieldNames(impalaTotalScoringTableFields), impalaTotalScoringTableDelimiter);
		} else{
			return new ImpalaTotalScoreWriter(getFile(userTotalScoreCsvFileFullPathString), impalaParser, ImpalaParser.getTableFieldNames(impalaTotalScoringTableFields), impalaTotalScoringTableDelimiter);
		}
	}
	
	public ImpalaUseridToAppUsernameWriter createImpalaUseridToAppUsernameWriter(){
		if(StringUtils.isEmpty(useridToAppUsernameCsvFileFullPathString)){
			return new ImpalaUseridToAppUsernameWriter(impalaParser);
		}
		ImpalaUseridToAppUsernameWriter writer = new ImpalaUseridToAppUsernameWriter(getFile(useridToAppUsernameCsvFileFullPathString), impalaParser);
		return writer;
	}
}
