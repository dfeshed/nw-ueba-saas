package fortscale.streaming.alert.subscribers;

import fortscale.domain.core.SessionTimeUpdate;
import fortscale.domain.core.SessionUpdateType;

import java.util.Map;

/**
 * Created by danal on 04/08/2015.
 */
public class DestroyVariableUpdateStatementSubscriber extends AbstractSubscriber{

	public void update(Map insertStream) {
		if (esperStatement != null){
		SessionTimeUpdate sessionTimeUpdate = (SessionTimeUpdate) insertStream.values().toArray()[0];
		if(sessionTimeUpdate.getSessionUpdateType() == SessionUpdateType.Final){
				esperStatement.destroy();
			}
		}
	}
}
