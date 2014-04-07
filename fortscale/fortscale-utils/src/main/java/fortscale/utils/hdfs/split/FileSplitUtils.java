package fortscale.utils.hdfs.split;


public class FileSplitUtils {

	public static FileSplitStrategy getFileSplitStrategy(String fileSplitStrategyType){
		switch(fileSplitStrategyType){
		case "daily" : return new DailyFileSplitStrategy();
		case "weekly" : return new WeeklyFileSplitStrategy();
		default : return new DefaultFileSplitStrategy();
		}
	}
}
