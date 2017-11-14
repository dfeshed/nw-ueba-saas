import Component from '@ember/component';
import {
  getRootConditionGroup,
  isRuleBuilderInvalid,
  hasGroupsWithoutConditions,
  hasMissingConditionInfo
} from 'respond/selectors/aggregation-rule';
import { addGroup } from 'respond/actions/creators/aggregation-rule-creators';
import { connect } from 'ember-redux';

const stateToComputed = (state) => {
  return {
    rootGroup: getRootConditionGroup(state),
    isInvalid: isRuleBuilderInvalid(state),
    hasGroupsWithoutConditions: hasGroupsWithoutConditions(state),
    hasMissingConditionInfo: hasMissingConditionInfo(state)
  };
};

const dispatchToActions = function(dispatch) {
  return {
    addGroup: () => {
      dispatch(addGroup());
    }
  };
};

/**
 * @class RuleBuilder
 * @public
 */

const AggregationRuleBuilder = Component.extend({
  classNames: ['rsa-rule-builder'],
  classNameBindings: ['isInvalid']
});

export default connect(stateToComputed, dispatchToActions)(AggregationRuleBuilder);