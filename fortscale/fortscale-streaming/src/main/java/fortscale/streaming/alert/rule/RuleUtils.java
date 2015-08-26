package fortscale.streaming.alert.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by danal on 02/08/2015.
 */
public class RuleUtils {

	private static Logger logger = LoggerFactory.getLogger(RuleUtils.class);


	public static Long hourStartTimestamp(Long timestamp){
		if (timestamp == null){
			logger.warn("timestamp field is null");
			return 0L;
		}
		return Math.round(Math.floor(timestamp/3600000))*3600000;
	}

	public static Long dayStartTimestamp(Long timestamp){
		if (timestamp == null){
			logger.warn("timestamp field is null");
			return 0L;
		}
		return Math.round(Math.floor(timestamp/86400000))*86400000;
	}

	public static Long hourEndTimestamp(Long startTimestamp){
		if (startTimestamp == null){
			logger.warn("startTimestamp field is null");
			return 0L;
		}
		return startTimestamp+(60*60*1000-1);
	}
	public static Long dayEndTimestamp(Long startTimestamp){
		if (startTimestamp == null){
			logger.warn("startTimestamp field is null");
			return 0L;
		}
		return startTimestamp+(60*60*24*1000-1);
	}
}
