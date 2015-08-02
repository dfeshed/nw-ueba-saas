package fortscale.streaming.alert;

/**
 * Created by danal on 02/08/2015.
 */
public class RuleUtils {

	public static long hourStartTimestamp(long timestamp){
		return Math.round(Math.floor(timestamp/3600000))*3600000;
	}

	public static long dayStartTimestamp(long timestamp){
		return Math.round(Math.floor(timestamp/86400000))*86400000;
	}

	public static long hourEndTimestamp(long startTimestamp){
		return startTimestamp+3599999;
	}
}
