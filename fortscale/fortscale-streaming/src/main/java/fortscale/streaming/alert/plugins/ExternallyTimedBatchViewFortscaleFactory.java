
package fortscale.streaming.alert.plugins;

import com.espertech.esper.client.EventType;
import com.espertech.esper.core.context.util.AgentInstanceViewFactoryChainContext;
import com.espertech.esper.core.service.StatementContext;
import com.espertech.esper.epl.expression.core.ExprEvaluator;
import com.espertech.esper.epl.expression.core.ExprNode;
import com.espertech.esper.epl.expression.core.ExprNodeUtility;
import com.espertech.esper.epl.expression.time.ExprTimePeriodEvalDeltaConst;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.view.*;
import com.espertech.esper.view.window.ExternallyTimedBatchView;
import com.espertech.esper.view.window.IStreamRelativeAccess;
import com.espertech.esper.view.window.RelativeAccessByEventNIndexMap;

import java.util.List;

/*
 * This class is a copy of ExternallyTimedBatchViewFactory with one small difference.
 * It creates a ExternallyFullTimedBatchView, see this class for the differences from the regular ExternallyTimedBatchView.
 */
public class ExternallyTimedBatchViewFortscaleFactory implements DataWindowBatchingViewFactory, DataWindowViewFactory, DataWindowViewWithPrevious {
	private List<ExprNode> viewParameters;
	private EventType eventType;
	protected ExprNode timestampExpression;
	protected ExprEvaluator timestampExpressionEval;
	protected Long optionalReferencePoint;
	protected ExprTimePeriodEvalDeltaConst timeDeltaComputation;

	public ExternallyTimedBatchViewFortscaleFactory() {
	}

	public void setViewParameters(ViewFactoryContext viewFactoryContext, List<ExprNode> expressionParameters) throws ViewParameterException {
		this.viewParameters = expressionParameters;
	}

	public void attach(EventType parentEventType, StatementContext statementContext, ViewFactory optionalParentFactory, List<ViewFactory> parentViewFactories) throws ViewParameterException {
		String windowName = this.getViewName();
		ExprNode[] validated = ViewFactorySupport.validate(windowName, parentEventType, statementContext, this.viewParameters, true);
		if(this.viewParameters.size() >= 2 && this.viewParameters.size() <= 3) {
			if(!JavaClassHelper.isNumeric(validated[0].getExprEvaluator().getType())) {
				throw new ViewParameterException(this.getViewParamMessage());
			} else {
				this.timestampExpression = validated[0];
				this.timestampExpressionEval = this.timestampExpression.getExprEvaluator();
				ViewFactorySupport.assertReturnsNonConstant(windowName, validated[0], 0);
				this.timeDeltaComputation = ViewFactoryTimePeriodHelper.validateAndEvaluateTimeDelta(this.getViewName(), statementContext, (ExprNode)this.viewParameters.get(1), this.getViewParamMessage(), 1);
				if(validated.length == 3) {
					Object constant = ViewFactorySupport.validateAndEvaluate(windowName, statementContext, validated[2]);
					if(!(constant instanceof Number) || JavaClassHelper.isFloatingPointNumber((Number)constant)) {
						throw new ViewParameterException("Externally-full-timed batch view requires a Long-typed reference point in msec as a third parameter");
					}

					this.optionalReferencePoint = Long.valueOf(((Number)constant).longValue());
				}

				this.eventType = parentEventType;
			}
		} else {
			throw new ViewParameterException(this.getViewParamMessage());
		}
	}

	public Object makePreviousGetter() {
		return new RelativeAccessByEventNIndexMap();
	}

	public View makeView(AgentInstanceViewFactoryChainContext agentInstanceViewFactoryContext) {
		IStreamRelativeAccess relativeAccessByEvent = ViewServiceHelper.getOptPreviousExprRelativeAccess(agentInstanceViewFactoryContext);
		return new ExternallyTimedBatchViewFortscale(this, this.timestampExpression, this.timestampExpressionEval, this.timeDeltaComputation, this.optionalReferencePoint, relativeAccessByEvent, agentInstanceViewFactoryContext);
	}

	public EventType getEventType() {
		return this.eventType;
	}

	public boolean canReuse(View view) {
		if(!(view instanceof ExternallyTimedBatchView)) {
			return false;
		} else {
			ExternallyTimedBatchView myView = (ExternallyTimedBatchView)view;
			return this.timeDeltaComputation.equalsTimePeriod(myView.getTimeDeltaComputation()) && ExprNodeUtility.deepEquals(myView.getTimestampExpression(), this.timestampExpression)?myView.isEmpty():false;
		}
	}

	public String getViewName() {
		return "Externally-timed-batch-fortscale";
	}

	private String getViewParamMessage() {
		return this.getViewName() + " view requires a timestamp expression and a numeric or time period parameter for window size and an optional long-typed reference point in msec, and an optional list of control keywords as a string parameter (please see the documentation)";
	}
}
