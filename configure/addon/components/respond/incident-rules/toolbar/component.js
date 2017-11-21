import Component from '@ember/component';
import Confirmable from 'configure/mixins/confirmable';
import { deleteRule, cloneRule } from 'configure/actions/creators/respond/incident-rule-creators';
import { hasSelectedRule, getSelectedIncidentRuleId } from 'configure/reducers/respond/incident-rules/selectors';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';

const stateToComputed = (state) => ({
  hasSelectedRule: hasSelectedRule(state),
  selectedRuleId: getSelectedIncidentRuleId(state)
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
 * @class IncidentRulesToolbar
 * @public
 */

const IncidentRulesToolbar = Component.extend(Confirmable, {
  tagName: 'hbox',
  accessControl: inject(),
  classNames: ['incident-rules-toolbar']
});

export default connect(stateToComputed, dispatchToActions)(IncidentRulesToolbar);