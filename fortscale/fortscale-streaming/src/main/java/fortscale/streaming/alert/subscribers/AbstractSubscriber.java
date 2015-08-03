package fortscale.streaming.alert.subscribers;

import com.espertech.esper.client.EPStatement;


/**
 * Created by rans on 09/07/15.
 * an abstract calss for the Alert subscribers
 * no need to add the update method as an abstract method,
 * since it syntax can be different for every implementation , in the manner of args types, and we don't want to limit the usability
 */
public abstract class AbstractSubscriber {

    protected EPStatement esperStatement;

    public EPStatement getEsperStatement() {
        return esperStatement;
    }

    public void setEsperStatement(EPStatement esperStatement) {
        this.esperStatement = esperStatement;
    }

}
