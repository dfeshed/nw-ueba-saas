package fortscale.streaming.alert.rule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by danal on 02/08/2015.
 */
public class RuleUtils {

	private static final String ENTITY_TYPE_PREFIX_USER = "normalized_username_";

	private static final int MILLIS_IN_SECOND = 1000;
	private static final int MILLIS_IN_HOUR = 60 * 60 * MILLIS_IN_SECOND;
	private static final int MILLIS_IN_DAY = 24 * MILLIS_IN_HOUR;

	private static Logger logger = LoggerFactory.getLogger(RuleUtils.class);

	public static Long hourStartTimestamp(Long timestamp){
		if (timestamp == null){
			// if timestamp is null, return zero as oldest day possible
			logger.error("timestamp field is null");
			return 0L;
		}
		long hoursSinceEpoch = timestamp / MILLIS_IN_HOUR;

		return hoursSinceEpoch * MILLIS_IN_HOUR;
	}

	public static Long dayStartTimestamp(Long timestamp){
		if (timestamp == null){
			// if timestamp is null, return zero as oldest day possible
			logger.error("timestamp field is null");
			return 0L;
		}

		long daysSinceEpoch = timestamp / MILLIS_IN_DAY;

		return daysSinceEpoch * MILLIS_IN_DAY;
	}

	public static Long hourEndTimestamp(Long startTimestamp){
		if (startTimestamp == null){
			// if timestamp is null, return max long value so it is an unreachable date
			logger.error("startTimestamp field is null");
			return Long.MAX_VALUE;
		}

		long hoursSinceEpoch = startTimestamp / MILLIS_IN_HOUR;

		return ((hoursSinceEpoch + 1) * MILLIS_IN_HOUR) - MILLIS_IN_SECOND;
	}
	public static Long dayEndTimestamp(Long startTimestamp){
		if (startTimestamp == null){
			// if timestamp is null, return max long value so it is an unreachable date
			logger.error("startTimestamp field is null");
			return Long.MAX_VALUE;
		}

		long daysSinceEpoch = startTimestamp / MILLIS_IN_DAY;

		return ((daysSinceEpoch + 1) * MILLIS_IN_DAY) - MILLIS_IN_SECOND;
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
