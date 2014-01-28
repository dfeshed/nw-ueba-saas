package fortscale.collection.jobs.scoring;

import java.io.IOException;

import org.springframework.stereotype.Component;

import fortscale.utils.hdfs.HDFSPartitionsWriter;
import fortscale.utils.hdfs.partition.MonthlyPartitionStrategy;
import fortscale.utils.hdfs.split.DefaultFileSplitStrategy;
import fortscale.services.impl.ImpalaGroupsScoreWriter;
import fortscale.services.impl.ImpalaTotalScoreWriter;
import fortscale.services.impl.ImpalaUseridToAppUsernameWriter;
import fortscale.services.impl.ImpalaWriterFactory;

@Component
public class ImpalaWriterFactoryImpl extends ImpalaWriterFactory{
	
	private HDFSPartitionsWriter groupsScoreAppender;
	private HDFSPartitionsWriter totalScoreAppender;
	
	public void createGroupsScoreAppender(String basePath, String filename) throws IOException{
		if(groupsScoreAppender == null){
			groupsScoreAppender = new HDFSPartitionsWriter(basePath, new MonthlyPartitionStrategy(), new DefaultFileSplitStrategy());
		}
		groupsScoreAppender.open(filename);
	}
	
	public void closeGroupsScoreAppender() throws IOException{
		groupsScoreAppender.close();
	}
	
	public void createTotalScoreAppender(String basePath, String filename) throws IOException{
		if(totalScoreAppender == null){
			totalScoreAppender = new HDFSPartitionsWriter(basePath, new MonthlyPartitionStrategy(), new DefaultFileSplitStrategy());
		}
		totalScoreAppender.open(filename);
	}
	
	public void closeTotalScoreAppender() throws IOException{
		totalScoreAppender.close();
	}

	@Override
	public ImpalaGroupsScoreWriter createImpalaGroupsScoreWriter() {
		ImpalaGroupsScoreWriter writer = null;
		if(groupsScoreAppender != null){
			writer = new ImpalaGroupsScoreWriter(groupsScoreAppender, impalaParser);
		} else{
			writer = new ImpalaGroupsScoreWriter(impalaParser);
		}
		return writer;
	}

	@Override
	public ImpalaTotalScoreWriter createImpalaTotalScoreWriter() {
		ImpalaTotalScoreWriter writer = null;
		if(totalScoreAppender != null){
			writer = new ImpalaTotalScoreWriter(totalScoreAppender, impalaParser);
		} else{
			writer = new ImpalaTotalScoreWriter(impalaParser);
		}
		return writer;
	}

	@Override
	public ImpalaUseridToAppUsernameWriter createImpalaUseridToAppUsernameWriter() {
		// TODO Auto-generated method stub
		return null;
	}

}
