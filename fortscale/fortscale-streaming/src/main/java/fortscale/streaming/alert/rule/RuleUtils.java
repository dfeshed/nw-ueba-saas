package fortscale.streaming.alert.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by danal on 02/08/2015.
 */
public class RuleUtils {

	public static final String ENTITY_TYPE_PREFIX_USER = "normalized_username_";

	private static Logger logger = LoggerFactory.getLogger(RuleUtils.class);

	public static Long hourStartTimestamp(Long timestamp){
		if (timestamp == null){
			// if timestamp is null, return zero as oldest day possible
			logger.warn("timestamp field is null");
			return 0L;
		}
		return Math.round(Math.floor(timestamp/(60*60*1000)))*(60*60*1000);
	}

	public static Long dayStartTimestamp(Long timestamp){
		if (timestamp == null){
			// if timestamp is null, return zero as oldest day possible
			logger.warn("timestamp field is null");
			return 0L;
		}
		return Math.round(Math.floor(timestamp/(60*60*24*1000)))*(60*60*24*1000);
	}

	public static Long hourEndTimestamp(Long startTimestamp){
		if (startTimestamp == null){
			// if timestamp is null, return max long value so it is an unreachable date
			logger.warn("startTimestamp field is null");
			return Long.MAX_VALUE;
		}
		return startTimestamp+(60*60*1000-1);
	}
	public static Long dayEndTimestamp(Long startTimestamp){
		if (startTimestamp == null){
			// if timestamp is null, return max long value so it is an unreachable date
			logger.warn("startTimestamp field is null");
			return Long.MAX_VALUE;
		}
		return startTimestamp+(60*60*24*1000-1);
	}

	/**
	 * context id comes in form of prefix+ entity name, e.g normalized_username_user123@fs.com
	 * in order to convert contexId to entity name, we should strip it from the prefix
	 * @param contextId
	 * @return
	 */
	public static String extractNormalizedUsernameFromContextId(String contextId){
		if(contextId == null){
			return null;
		}
		 return contextId.split(ENTITY_TYPE_PREFIX_USER)[1];
	}

	/**
	 * function to create an empty aggregatedFeatureEvents object
	 * @return
	 */
	public static java.util.List<net.minidev.json.JSONObject> aggEvent(){

		return new java.util.ArrayList<>();
	}

}
