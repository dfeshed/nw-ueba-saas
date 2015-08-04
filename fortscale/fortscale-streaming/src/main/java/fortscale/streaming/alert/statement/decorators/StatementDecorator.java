package fortscale.streaming.alert.statement.decorators;

import fortscale.streaming.alert.rule.RuleConfig;

/**
 * Created by danal on 04/08/2015.
 */
public interface StatementDecorator {

	public RuleConfig prepareStatement(RuleConfig ruleConfig, Object... extraParams) throws IllegalArgumentException;
}
