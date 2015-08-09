package fortscale.streaming.alert.statement.decorators;

import fortscale.streaming.alert.rule.RuleConfig;

/**
 * Created by danal on 04/08/2015.
 */
public class SessionStatementDecorator implements StatementDecorator{


	@Override public RuleConfig prepareStatement(RuleConfig ruleConfig, Object... extraParams) throws IllegalArgumentException {
		if (extraParams.length != 1 || !(extraParams[0] instanceof String)){
			throw  new IllegalArgumentException();
		}
		RuleConfig newRuleConfig = new RuleConfig(ruleConfig);
		String newStatement = ruleConfig.getStatement().replace("##sessionId##",(String) extraParams[0]);
		newRuleConfig.setStatement(newStatement);
		return newRuleConfig;
	}
}
