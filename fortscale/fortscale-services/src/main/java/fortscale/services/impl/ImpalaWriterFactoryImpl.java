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
		}
		ImpalaGroupsScoreWriter writer = new ImpalaGroupsScoreWriter(getFile(userAdScoreCsvFileFullPathString), impalaParser, ImpalaParser.getTableFieldNames(impalaGroupMembershipScoringTableFields), impalaGroupMembershipScoringTableDelimiter);
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
