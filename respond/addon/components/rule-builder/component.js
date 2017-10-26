import Component from '@ember/component';
import { getRootConditionGroup } from 'respond/selectors/aggregation-rule';
import { addGroup } from 'respond/actions/creators/aggregation-rule-creators';
import { connect } from 'ember-redux';

const stateToComputed = (state) => {
  return {
    rootGroup: getRootConditionGroup(state)
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
  classNames: ['rsa-rule-builder']
});

export default connect(stateToComputed, dispatchToActions)(AggregationRuleBuilder);