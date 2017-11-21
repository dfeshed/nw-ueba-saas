import Component from '@ember/component';
import {
  getIncidentRules,
  getIncidentRulesStatus,
  getIsIncidentRulesTransactionUnderway,
  getSelectedIncidentRuleId
} from 'configure/reducers/respond/incident-rules/selectors';
import { selectRule, reorderRules, getRules } from 'configure/actions/creators/respond/incident-rule-creators';
import { connect } from 'ember-redux';
import columns from './columns';
import _ from 'lodash';

const stateToComputed = (state) => ({
  rules: getIncidentRules(state),
  rulesStatus: getIncidentRulesStatus(state),
  selectedRuleId: getSelectedIncidentRuleId(state),
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
    },
    getRules() {
      dispatch(getRules());
    }
  };
};

/**
 * Component that displays the listing of incident rules along with a toolbar for performing actions on items in the list
 * @class IncidentRules
 * @public
 */
const IncidentRules = Component.extend({
  tagName: 'vbox',
  classNames: ['rsa-incident-rules', 'flexi-fit'],
  classNameBindings: ['isTransactionUnderway:transaction-in-progress'],

  /**
   * Column configuration for the listing data table
   * @property columns
   * @public
   */
  columns,

  /**
   * Configures whether the data table listing uses lazy rendering. Primarily used for integration tests that
   * benefit from disabling lazy rendering
   * @property useLazyRendering
   * @public
   */
  useLazyRendering: true,

  onInit: function() {
    this.send('getRules');
  }.on('init'),

  actions: {
    /**
     * Handler for a click on one of the table rows. Used as one of the mechanisms for selecting the row.
     * Here we only send the select action if the cell itself is being clicked (and not the radio button that exists
     * in the select column's cell)
     * @param item
     * @param index
     * @param event
     * @private
     */
    handleRowClick(item, index, event) {
      if (this.$(event.target).is('.rsa-data-table-body-cell')) {
        this.send('selectRule', item);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(IncidentRules);