package fortscale.streaming.alert.event.wrappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import fortscale.domain.core.SessionTimeUpdate;
import fortscale.domain.core.SessionUpdateType;
import fortscale.streaming.alert.statement.decorators.SessionStatementDecorator;
import fortscale.streaming.alert.statement.decorators.StatementDecorator;

/**
 * Created by danal on 04/08/2015.
 */
public class SessionTimeUpdateWrapper implements EventWrapper<SessionTimeUpdate> {

	/**
	 * JSON serializer
	 */
	protected ObjectMapper mapper = new ObjectMapper();

	public SessionTimeUpdate convertEvent(String inputTopic, String messageKey,String sessionTimeUpdateMessageString) throws Exception{
		SessionTimeUpdate sessionTimeUpdate = (SessionTimeUpdate) mapper.readValue(sessionTimeUpdateMessageString, SessionTimeUpdate.class);
		if (sessionTimeUpdate.getEndTimestamp() == null) {
			sessionTimeUpdate.setEndTimestamp(Long.MAX_VALUE);
		}
		return sessionTimeUpdate;
	}

	@Override public boolean shouldCreateDynamicStatements(SessionTimeUpdate sessionTimeUpdate) {
		return sessionTimeUpdate.getSessionUpdateType() == SessionUpdateType.New;
	}

	@Override public StatementDecorator getStatementDecorator() {
		return new SessionStatementDecorator();
	}

	@Override public Object[] getDecoratorParams(SessionTimeUpdate sessionTimeUpdate) {
		Object[] params = {sessionTimeUpdate.getSessionId()};
		return params;
	}

}
