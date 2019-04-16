import Component from '@ember/component';
import {
  getIncidentRules,
  getIncidentRulesStatus,
  getIsIncidentRulesTransactionUnderway,
  getSelectedIncidentRules
} from 'configure/reducers/respond/incident-rules/selectors';
import { selectRule, reorderRules } from 'configure/actions/creators/respond/incident-rule-creators';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import _ from 'lodash';

const stateToComputed = (state) => ({
  rules: getIncidentRules(state),
  rulesStatus: getIncidentRulesStatus(state),
  selectedRules: getSelectedIncidentRules(state),
  isTransactionUnderway: getIsIncidentRulesTransactionUnderway(state)
});

const dispatchToActions = function(dispatch) {
  return {
    selectRule: (item) => {
      dispatch(selectRule(item.id));
    },
    reorderRules: (reorderedItems) => {
      // Only if a rule order change has actually occurred
      if (!_.isEqual(this.get('rules'), reorderedItems)) {
        const reorderedIds = reorderedItems.map((item) => item.id);
        dispatch(reorderRules(reorderedIds));
      }
    }
  };
};

/**
 * Component that displays the listing of incident rules along with a toolbar for performing actions on items in the list
 * @class IncidentRules
 * @public
 */
const IncidentRules = Component.extend({
  accessControl: inject(),
  tagName: 'vbox',
  classNames: ['rsa-incident-rules', 'flexi-fit'],
  classNameBindings: ['isTransactionUnderway:transaction-in-progress'],

  actions: {
    /**
     * Handler for a click on one of the table rows. Used as one of the mechanisms for selecting the row.
     * Here we only send the select action if the cell itself is being clicked (and not the radio button that exists
     * in the select column's cell)
     * @param item
     * @private
     */
    handleRowClick(item) {
      this.send('selectRule', item);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentRules);
