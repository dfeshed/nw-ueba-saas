package fortscale.services.impl;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;

import fortscale.domain.core.User;
import fortscale.domain.fe.AdUserFeaturesExtraction;
import fortscale.domain.fe.IGroupMembershipScore;
import fortscale.utils.hdfs.HDFSWriter;
import fortscale.utils.impala.ImpalaParser;
import fortscale.utils.logging.Logger;

public class ImpalaGroupsScoreWriter extends ImpalaWriter{
	private static Logger logger = Logger.getLogger(ImpalaGroupsScoreWriter.class);
	
	
	private List<String> tableFieldsNames;
	private String tableFieldsDelimiter;
	
	public ImpalaGroupsScoreWriter(File file, ImpalaParser impalaParser, List<String> tableFieldsNames, String tableFieldsDelimiter){
		super(file, impalaParser);
		this.tableFieldsNames = tableFieldsNames;
		this.tableFieldsDelimiter = tableFieldsDelimiter;
	}
		
	public ImpalaGroupsScoreWriter(ImpalaParser impalaParser, List<String> tableFieldsNames, String tableFieldsDelimiter) {
		super(impalaParser);
		this.tableFieldsNames = tableFieldsNames;
		this.tableFieldsDelimiter = tableFieldsDelimiter;
	}
	
	public ImpalaGroupsScoreWriter(HDFSWriter writer, ImpalaParser impalaParser, List<String> tableFieldsNames, String tableFieldsDelimiter) {
		super(writer, impalaParser);
		this.tableFieldsNames = tableFieldsNames;
		this.tableFieldsDelimiter = tableFieldsDelimiter;
	}

	public void writeScore(User user, AdUserFeaturesExtraction extraction, double avgScore){
		GroupMembershipScoreIterator iterator = new GroupMembershipScoreIterator(impalaParser, user, extraction, avgScore);
		
		while(iterator.hasNext()){
			IGroupMembershipScore groupMembershipScore = iterator.next();
			List<String> values = new ArrayList<>();
			for(String fieldName: tableFieldsNames){
				try {
					values.add(BeanUtils.getProperty(groupMembershipScore, fieldName));
				} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
					logger.warn(String.format("got the following exception while trying to read the field %s", fieldName), e);
					values.add("NULL");
				}
			}
			writeLine(StringUtils.join(values, tableFieldsDelimiter), groupMembershipScore.getRuntime());
		}
	}	
}
