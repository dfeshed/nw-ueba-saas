package fortscale.domain.schema;

import fortscale.utils.hdfs.partition.PartitionStrategy;

/**
 * Table schema general methods
 */
public interface TableSchema {

	String getTableName();
	PartitionStrategy getPartitionStrategy();
	String getPartitionFieldName();
	
}
