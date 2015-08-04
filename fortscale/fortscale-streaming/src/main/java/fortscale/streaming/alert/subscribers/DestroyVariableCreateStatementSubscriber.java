package fortscale.streaming.alert.subscribers;

import java.util.Map;

/**
 * Created by danal on 04/08/2015.
 */
public class DestroyVariableCreateStatementSubscriber extends AbstractSubscriber{

	public void update(Map insertStream) {
		if (esperStatement != null) {
			// sleep to allow creating all relevant statements (before destroying this one)
			try {
				Thread.sleep(10000l);
			} catch (Exception e) {
			}
			esperStatement.destroy();
		}
	}
}
