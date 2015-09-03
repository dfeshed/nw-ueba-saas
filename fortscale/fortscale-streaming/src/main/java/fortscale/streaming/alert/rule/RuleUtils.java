package fortscale.streaming.alert.rule;

/**
 * Created by danal on 02/08/2015.
 */
public class RuleUtils {

	public static final String ENTITY_TYPE_PREFIX_USER = "normalized_username_";

	public static Long hourStartTimestamp(Long timestamp){
		if (timestamp == null){
			return 0L;
		}
		return Math.round(Math.floor(timestamp/3600000))*3600000;
	}

	public static Long dayStartTimestamp(Long timestamp){
		if (timestamp == null){
			return 0L;
		}
		return Math.round(Math.floor(timestamp/86400000))*86400000;
	}

	public static Long hourEndTimestamp(Long startTimestamp){
		if (startTimestamp == null){
			return 0L;
		}
		return startTimestamp+(60*60*1000-1);
	}
	public static Long dayEndTimestamp(Long startTimestamp){
		if (startTimestamp == null){
			return 0L;
		}
		return startTimestamp+(60*60*24*1000-1);
	}

	public static String extractNormalizedUsernameFromContextId(String contextId){
		if(contextId == null){
			return null;
		}
		 return contextId.split(ENTITY_TYPE_PREFIX_USER)[1];
	}
}
