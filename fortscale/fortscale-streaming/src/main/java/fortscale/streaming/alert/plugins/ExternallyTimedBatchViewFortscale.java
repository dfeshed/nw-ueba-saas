//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package fortscale.streaming.alert.plugins;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.EventType;
import com.espertech.esper.collection.ViewUpdatedCollection;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.expression.core.ExprNode;
import com.espertech.esper.epl.expression.time.ExprTimePeriodEvalDeltaConst;
import com.espertech.esper.epl.expression.time.ExprTimePeriodEvalDeltaResult;
import com.espertech.esper.event.EventBeanUtility;
import com.espertech.esper.view.*;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

/*
 * This class is a copy of ExternallyTimedBatchView with one small difference
 * It filter out events that occur before the current window have started.
 * it is meant to solve some of the problems caused from our events arrived in an ot of order manner.
 * we sill have problem of events arriving after the window have closed (in order to handel this problem we add a preceding batch window that order the events.
 * this might be optimized in the future with handling the sort in a streaming way and not as a batch.
 */
public class ExternallyTimedBatchViewFortscale extends ViewSupport implements DataWindowView, CloneableView {
	private final ExternallyTimedBatchViewFortscaleFactory factory;
	private final ExprNode timestampExpression;
	private final ExprEvaluator timestampExpressionEval;
	private final ExprTimePeriodEvalDeltaConst timeDeltaComputation;
	private final EventBean[] eventsPerStream = new EventBean[1];
	protected EventBean[] lastBatch;
	private Long oldestTimestamp;
	protected final Set<EventBean> window = new LinkedHashSet();
	protected Long referenceTimestamp;
	protected ViewUpdatedCollection viewUpdatedCollection;
	protected AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext;

	public ExternallyTimedBatchViewFortscale(ExternallyTimedBatchViewFortscaleFactory factory, ExprNode timestampExpression,
			ExprEvaluator timestampExpressionEval, ExprTimePeriodEvalDeltaConst timeDeltaComputation,
			Long optionalReferencePoint, ViewUpdatedCollection viewUpdatedCollection,
			AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
		this.factory = factory;
		this.timestampExpression = timestampExpression;
		this.timestampExpressionEval = timestampExpressionEval;
		this.timeDeltaComputation = timeDeltaComputation;
		this.viewUpdatedCollection = viewUpdatedCollection;
		this.agentInstanceViewFactoryContext = agentInstanceViewFactoryContext;
		this.referenceTimestamp = optionalReferencePoint;
	}

	public View cloneView() {
		return this.factory.makeView(this.agentInstanceViewFactoryContext);
	}

	public final ExprNode getTimestampExpression() {
		return this.timestampExpression;
	}

	public final EventType getEventType() {
		return this.parent.getEventType();
	}

	public final void update(EventBean[] newData, EventBean[] oldData) {
		EventBean[] batchNewData;
		int len$;
		if(oldData != null && oldData.length != 0) {
			batchNewData = oldData;
			int arr$ = oldData.length;

			for(len$ = 0; len$ < arr$; ++len$) {
				EventBean i$ = batchNewData[len$];
				this.window.remove(i$);
				this.handleInternalRemovedEvent(i$);
			}

			this.determineOldestTimestamp();
		}

		batchNewData = null;
		if(newData != null) {
			EventBean[] var11 = newData;
			len$ = newData.length;

			for(int var12 = 0; var12 < len$; ++var12) {
				EventBean newEvent = var11[var12];
				long timestamp = this.getLongValue(newEvent);
				if(this.referenceTimestamp == null) {
					this.referenceTimestamp = Long.valueOf(timestamp);
				}

				if(this.oldestTimestamp == null) {
					this.oldestTimestamp = Long.valueOf(timestamp);
				} else{
					ExprTimePeriodEvalDeltaResult delta = this.timeDeltaComputation.deltaMillisecondsAddWReference(this.oldestTimestamp.longValue(), this.referenceTimestamp.longValue());
					this.referenceTimestamp = Long.valueOf(delta.getLastReference());
					if(timestamp - this.oldestTimestamp.longValue() >= delta.getDelta()) {
						if(batchNewData == null) {
							batchNewData = (EventBean[])this.window.toArray(new EventBean[this.window.size()]);
						} else {
							batchNewData = EventBeanUtility.addToArray(batchNewData, this.window);
						}

						this.window.clear();
						this.oldestTimestamp = null;
					}
				}

				// this condition is filtering events that occur before the current window have started
				if(this.oldestTimestamp == null || timestamp >= this.oldestTimestamp.longValue()) {
					this.window.add(newEvent);
					this.handleInternalAddEvent(newEvent, batchNewData != null);
				}
			}
		}

		if(batchNewData != null) {
			this.handleInternalPostBatch(this.window, batchNewData);
			if(this.viewUpdatedCollection != null) {
				this.viewUpdatedCollection.update(batchNewData, this.lastBatch);
			}

			this.updateChildren(batchNewData, this.lastBatch);
			this.lastBatch = batchNewData;
			this.determineOldestTimestamp();
		}

		if(oldData != null && oldData.length > 0) {
			if(this.viewUpdatedCollection != null) {
				this.viewUpdatedCollection.update((EventBean[])null, oldData);
			}

			this.updateChildren((EventBean[])null, oldData);
		}

	}

	public final Iterator<EventBean> iterator() {
		return this.window.iterator();
	}

	public final String toString() {
		return this.getClass().getName() + " timestampExpression=" + this.timestampExpression;
	}

	public boolean isEmpty() {
		return this.window.isEmpty();
	}

	public ExprTimePeriodEvalDeltaConst getTimeDeltaComputation() {
		return this.timeDeltaComputation;
	}

	public void visitView(ViewDataVisitor viewDataVisitor) {
		viewDataVisitor.visitPrimary(this.window, true, this.factory.getViewName(), (Integer)null);
	}

	public ViewFactory getViewFactory() {
		return this.factory;
	}

	protected void determineOldestTimestamp() {
		if(this.window.isEmpty()) {
			this.oldestTimestamp = null;
		} else {
			this.oldestTimestamp = Long.valueOf(this.getLongValue((EventBean)this.window.iterator().next()));
		}

	}

	protected void handleInternalPostBatch(Set<EventBean> window, EventBean[] batchNewData) {
	}

	protected void handleInternalRemovedEvent(EventBean anOldData) {
	}

	protected void handleInternalAddEvent(EventBean anNewData, boolean isNextBatch) {
	}

	private long getLongValue(EventBean obj) {
		this.eventsPerStream[0] = obj;
		Number num = (Number)this.timestampExpressionEval.evaluate(this.eventsPerStream, true, this.agentInstanceViewFactoryContext);
		return num.longValue();
	}
}
