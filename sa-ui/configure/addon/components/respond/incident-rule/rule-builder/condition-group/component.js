import Component from '@ember/component';
import { connect } from 'ember-redux';
import { alias } from 'ember-computed-decorators';
import {
  addCondition,
  removeGroup,
  updateGroup
} from 'configure/actions/creators/respond/incident-rule-creators';
import {
  getRuleConditionGroups,
  getRuleConditions
} from 'configure/reducers/respond/incident-rules/rule/selectors';

const stateToComputed = (state) => {
  return {
    conditions: getRuleConditions(state),
    conditionGroups: getRuleConditionGroups(state)
  };
};

const groupOperators = ['and', 'or', 'nor'];

const dispatchToActions = function(dispatch) {
  return {
    addCondition: () => {
      const groupId = this.get('info.id');
      dispatch(addCondition(groupId));
    },
    removeGroup: () => {
      const groupId = this.get('info.id');
      dispatch(removeGroup(groupId));
    },
    updateGroupOperator: (logicalOperator) => {
      const groupId = this.get('info.id');
      dispatch(updateGroup(groupId, { logicalOperator }));
    }
  };
};

/**
 * @class AggregationRuleGroup
 * @public
 */
const AggregationRuleGroup = Component.extend({
  classNames: ['rsa-rule-condition-group'],
  groupOperators,

  @alias('info.logicalOperator') selectedGroupOperator: null
});

export default connect(stateToComputed, dispatchToActions)(AggregationRuleGroup);