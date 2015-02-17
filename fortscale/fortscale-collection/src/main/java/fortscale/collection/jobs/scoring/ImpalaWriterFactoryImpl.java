package fortscale.collection.jobs.scoring;

import fortscale.services.impl.ImpalaGroupsScoreWriter;
import fortscale.services.impl.ImpalaTotalScoreWriter;
import fortscale.services.impl.ImpalaUseridToAppUsernameWriter;
import fortscale.services.impl.ImpalaWriterFactory;
import fortscale.utils.hdfs.BufferedHDFSWriter;
import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.PartitionStrategy;
import fortscale.utils.hdfs.partition.PartitionsUtils;
import fortscale.utils.hdfs.split.DefaultFileSplitStrategy;
import fortscale.utils.impala.ImpalaClient;
import fortscale.utils.impala.ImpalaParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@Component
public class ImpalaWriterFactoryImpl extends ImpalaWriterFactory{

	private static Logger logger = LoggerFactory.getLogger(ImpalaWriterFactoryImpl.class);
	@Autowired
	protected ImpalaClient impalaClient;
	
	@Value("${impala.ldap.group.membership.scores.table.fields}")
	private String impalaGroupMembershipScoringTableFields;
	@Value("${impala.ldap.group.membership.scores.table.delimiter}")
	private String impalaGroupMembershipScoringTableDelimiter;
	@Value("${impala.ldap.group.membership.scores.table.partition.type}")
	private String impalaGroupMembershipScoringTablePartitionType;
	
	@Value("${impala.total.scores.table.fields}")
	private String impalaTotalScoringTableFields;
	@Value("${impala.total.scores.table.delimiter}")
	private String impalaTotalScoringTableDelimiter;
    @Value(("${impala.total.scores.table.partition.type}"))
    private String impalaTotalScoringTablePartitionType;
	@Value("${hadoop.writer.buffer.size:10000}")
	protected int maxBufferSize;

	private HDFSPartitionsWriter groupsScoreAppender;
	private BufferedHDFSWriter totalScoreAppender;

	private String totalScoreAppenderFileName;
	
	public void createGroupsScoreAppender(String basePath, String filename) throws IOException{
		if(groupsScoreAppender == null){
			PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaGroupMembershipScoringTablePartitionType);
			groupsScoreAppender = new HDFSPartitionsWriter(basePath, partitionStrategy, new DefaultFileSplitStrategy());
		}
		groupsScoreAppender.open(filename);
	}
	
	public void closeGroupsScoreAppender() throws IOException{
		groupsScoreAppender.close();
	}
	
	public List<String> getGroupsScoreNewPartitions(){
		List<String> ret = null;
		if(groupsScoreAppender != null){
			ret = groupsScoreAppender.getNewPartitions();
		} else{
			ret = Collections.emptyList();
		}
		
		return ret;
	}
	
	public List<String> getTotalScoreNewPartitions(){
		List<String> ret = null;
		if(totalScoreAppender != null){
			ret = ((HDFSPartitionsWriter)totalScoreAppender.getWriter()).getNewPartitions();
		} else{
			ret = Collections.emptyList();
		}
		
		return ret;
	}
	
	public void createTotalScoreAppender(String basePath, String filename) throws IOException{
		if(totalScoreAppender == null){
			totalScoreAppenderFileName = filename;
            PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTotalScoringTablePartitionType);
			HDFSPartitionsWriter writer = new HDFSPartitionsWriter(basePath, partitionStrategy, new DefaultFileSplitStrategy());
			totalScoreAppender = new BufferedHDFSWriter(writer, totalScoreAppenderFileName, maxBufferSize);
		}
//		totalScoreAppender.open(filename);
	}
	
	public void closeTotalScoreAppender() throws IOException{
		totalScoreAppender.close();
	}

	@Override
	public ImpalaGroupsScoreWriter createImpalaGroupsScoreWriter() {
		ImpalaGroupsScoreWriter writer = null;
		if(groupsScoreAppender != null){
			writer = new ImpalaGroupsScoreWriter(groupsScoreAppender, impalaParser, ImpalaParser.getTableFieldNames(impalaGroupMembershipScoringTableFields), impalaGroupMembershipScoringTableDelimiter);
		} else{
			writer = new ImpalaGroupsScoreWriter(impalaParser, ImpalaParser.getTableFieldNames(impalaGroupMembershipScoringTableFields), impalaGroupMembershipScoringTableDelimiter);
		}
		return writer;
	}

	@Override
	public ImpalaTotalScoreWriter createImpalaTotalScoreWriter() {
		ImpalaTotalScoreWriter writer = null;
		if(totalScoreAppender != null){
			writer = new ImpalaTotalScoreWriter(totalScoreAppender, impalaParser, ImpalaParser.getTableFieldNames(impalaTotalScoringTableFields), impalaTotalScoringTableDelimiter);
		} else{
			writer = new ImpalaTotalScoreWriter(impalaParser, ImpalaParser.getTableFieldNames(impalaTotalScoringTableFields), impalaTotalScoringTableDelimiter);
		}
		return writer;
	}

	@Override
	public ImpalaUseridToAppUsernameWriter createImpalaUseridToAppUsernameWriter() {
		return null;
	}

}
