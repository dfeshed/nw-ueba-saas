import Component from '@ember/component';
import {
  getAggregationRules,
  getAggregationRulesStatus,
  getIsAggregationRulesTransactionUnderway,
  getSelectedAggregationRuleId } from 'respond/selectors/aggregation-rules';
import { selectRule, reorderRules } from 'respond/actions/creators/aggregation-rule-creators';
import { connect } from 'ember-redux';
import columns from './columns';
import _ from 'lodash';

const stateToComputed = (state) => ({
  rules: getAggregationRules(state),
  rulesStatus: getAggregationRulesStatus(state),
  selectedRuleId: getSelectedAggregationRuleId(state),
  isTransactionUnderway: getIsAggregationRulesTransactionUnderway(state)
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
 * Component that displays the listing of aggregation rules along with a toolbar for performing actions on items in the list
 * @class AggregationRules
 * @public
 */
const AggregationRules = Component.extend({
  tagName: 'vbox',
  classNames: ['rsa-aggregation-rules', 'flexi-fit'],
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

export default connect(stateToComputed, dispatchToActions)(AggregationRules);