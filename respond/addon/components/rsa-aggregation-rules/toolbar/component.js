import Component from '@ember/component';
import Confirmable from 'respond/mixins/confirmable';
import { deleteRule, cloneRule } from 'respond/actions/creators/aggregation-rule-creators';
import { hasSelectedRule, getSelectedAggregationRuleId } from 'respond/selectors/aggregation-rules';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';

const stateToComputed = (state) => ({
  hasSelectedRule: hasSelectedRule(state),
  selectedRuleId: getSelectedAggregationRuleId(state)
});

const dispatchToActions = function(dispatch) {
  return {
    clone: () => {
      const templateRuleId = this.get('selectedRuleId');
      const onSuccess = (clonedRuleId) => {
        const transitionToRule = this.get('transitionToRule');
        transitionToRule(clonedRuleId);
      };
      dispatch(cloneRule(templateRuleId, onSuccess));
    },

    delete: () => {
      const ruleId = this.get('selectedRuleId');
      this.send('showConfirmationDialog', 'delete-rule', {}, () => {
        dispatch(deleteRule(ruleId));
      });
    }
  };
};

/**
 * @class AggregationRulesToolbar
 * @public
 */

const AggregationRulesToolbar = Component.extend(Confirmable, {
  tagName: 'hbox',
  accessControl: inject(),
  classNames: ['aggregation-rules-toolbar']
});

export default connect(stateToComputed, dispatchToActions)(AggregationRulesToolbar);