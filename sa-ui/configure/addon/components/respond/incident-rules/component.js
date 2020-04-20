import Component from '@ember/component';
import {
  getIncidentRules,
  getIncidentRulesStatus,
  getIsIncidentRulesTransactionUnderway,
  getSelectedIncidentRules,
  isAllSelected
} from 'configure/reducers/respond/incident-rules/selectors';
import { reorderRules, selectAllRules, selectRule, clearSelectedRules } from 'configure/actions/creators/respond/incident-rule-creators';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';
import _ from 'lodash';

const stateToComputed = (state) => ({
  rules: getIncidentRules(state),
  rulesStatus: getIncidentRulesStatus(state),
  selectedRules: getSelectedIncidentRules(state),
  isTransactionUnderway: getIsIncidentRulesTransactionUnderway(state),
  isAllSelected: isAllSelected(state)
});

const dispatchToActions = {
  selectRule,
  reorderRules,
  selectAllRules,
  clearSelectedRules
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
      if (!this.get('selectedRules').includes(item.id)) {
        this.send('clearSelectedRules');
        this.send('selectRule', item.id);
      }
    },

    /**
     * Handler for a click on one of the row checkboxes. Used as one of the mechanisms for selecting the row.
     * Here we only send the select action if the checkboxes itself is being clicked (and not the row ).
     * @param item
     * @private
     */
    handleCheckboxClick(item) {
      this.send('selectRule', item.id);
      return false;
    },

    reorder(reorderedItems) {
      // Dispatch only if a rule order change has actually occurred
      if (!_.isEqual(this.get('rules'), reorderedItems)) {
        const reorderedIds = reorderedItems.map((item) => item.id);
        this.send('reorderRules', reorderedIds);
      }
    },

    /**
     * Handler for the drag action on the rows. Used as one of the mechanisms for selecting the row.
     * Here we only send the select action if the row is dragged.
     * @param item
     * @private
     */

    handleDragStopped(item) {
      if (!this.get('selectedRules').includes(item.id)) {
        this.send('clearSelectedRules');
        this.send('selectRule', item.id);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentRules);
