package fortscale.collection.jobs.scoring;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

@Component
public class ImpalaWriterFactoryImpl extends ImpalaWriterFactory{

	@Autowired
	protected ImpalaClient impalaClient;

	@Value("${impala.total.scores.table.fields}")
	private String impalaTotalScoringTableFields;
	@Value("${impala.total.scores.table.delimiter}")
	private String impalaTotalScoringTableDelimiter;
    @Value(("${impala.total.scores.table.partition.type}"))
    private String impalaTotalScoringTablePartitionType;
	@Value("${hadoop.writer.buffer.size:10000}")
	protected int maxBufferSize;

	private BufferedHDFSWriter totalScoreAppender;

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
            PartitionStrategy partitionStrategy = PartitionsUtils.getPartitionStrategy(impalaTotalScoringTablePartitionType);
			HDFSPartitionsWriter writer = new HDFSPartitionsWriter(basePath, partitionStrategy, new DefaultFileSplitStrategy(),impalaTotalScoringTableDelimiter);
			totalScoreAppender = new BufferedHDFSWriter(writer, filename, maxBufferSize);
		}
//		totalScoreAppender.open(filename);
	}
	
	public void closeTotalScoreAppender() throws IOException{
		totalScoreAppender.close();
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
