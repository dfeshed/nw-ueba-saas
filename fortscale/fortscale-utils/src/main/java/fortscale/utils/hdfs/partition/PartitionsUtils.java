package fortscale.utils.hdfs.partition;


public class PartitionsUtils {

	public static String normalizePath(String path) {
		String normalized = path.replace('\\', '/');
		if (normalized.endsWith("/"))
			return normalized;
		else
			return normalized + '/';
	}
	
	
	public static String getPartitionPartFromPath(String path) {
		if (path==null)
			return null;
		
		String normalized = normalizePath(path);
		normalized = normalized.substring(0, normalized.length()-1);
		
		if (normalized.contains("/"))
			return normalized.substring(normalized.lastIndexOf("/")+1);
		else
			return null;
	}
	
	public static PartitionStrategy getPartitionStrategy(String partitionType){
		switch(partitionType){
		case "monthly" : return new MonthlyPartitionStrategy();
		case "runtime" : return new RuntimePartitionStrategy();
        case "daily"   : return new DailyPartitionStrategy();
		default : return new DefaultPartitionStrategy();
		}
	}
}
