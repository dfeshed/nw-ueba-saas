import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import {
  initializeRule,
  updateRule,
  saveRule,
  clearMatchConditions
} from 'respond/actions/creators/aggregation-rule-creators';
import {
  getRuleInfo,
  getFields as getGroupByFields,
  isLoading,
  hasAdvancedQuery,
  isTransactionUnderway,
  hasMissingInformation,
  getVisited
} from 'respond/selectors/aggregation-rule';
import Confirmable from 'respond/mixins/confirmable';
import { connect } from 'ember-redux';
import { inject } from '@ember/service';

const stateToComputed = (state) => {
  return {
    ruleInfo: getRuleInfo(state),
    fields: getGroupByFields(state),
    isLoading: isLoading(state),
    hasAdvancedQuery: hasAdvancedQuery(state),
    isTransactionUnderway: isTransactionUnderway(state),
    isMissingRequiredData: hasMissingInformation(state),
    visited: getVisited(state)
  };
};

const dispatchToActions = function(dispatch) {
  return {
    // bootstrap fetch of the rule and all other required information (e.g., fields, categories, users)
    initializeRule(ruleId) {
      dispatch(initializeRule(ruleId));
    },
    // update the rule information using fully qualified field name (e.g., 'ruleInfo.incidentScoringOptions.type')
    update(field, value) {
      if (field && value !== undefined) {
        dispatch(updateRule(field, value));
      }
    },
    // remove/reset the rule builder configuration
    clearQuery() {
      dispatch(clearMatchConditions());
    },
    // save changes to the rule
    save() {
      // if the save operation is succesful, redirect the user to the rules listing page
      const onSuccess = () => {
        const transitionToRules = this.get('transitionToRules');
        transitionToRules();
      };
      dispatch(saveRule(onSuccess));
    }
  };
};

const ruleBuilderQueryMode = 'RULE_BUILDER';
const advancedQueryMode = 'ADVANCED';
const queryModes = [ruleBuilderQueryMode, advancedQueryMode];

/**
 * Represents the details of an individual aggregation rule including edit functionality
 * @class Aggregation Rule
 * @public
 */
const AggregationRule = Component.extend(Confirmable, {
  classNames: ['rsa-aggregation-rule'],
  classNameBindings: ['isTransactionUnderway:transaction-in-progress'],
  i18n: inject(),

  ruleId: null,

  /**
   * An array of strings each of which represents an identifier for a query mode (e.g., ADVANCED, RULE_BUILDER)
   * @property queryModes
   * @public
   */
  queryModes,

  /**
   * Returns the currently selected query mode
   * @property queryMode
   * @returns {string}
   * @public
   */
  @computed('hasAdvancedQuery')
  queryMode(hasAdvancedQuery) {
    return hasAdvancedQuery ? advancedQueryMode : ruleBuilderQueryMode;
  },

  onInit: function() {
    this.send('initializeRule', this.get('ruleId'));
  }.on('init'),

  actions: {
    handleNameChange(value) {
      this.send('update', 'ruleInfo.name', value);
    },
    handleDescriptionChange(value) {
      this.send('update', 'ruleInfo.description', value);
    },
    handleQueryTypeChange(value) {
      const isChangingToAdvanced = value === 'ADVANCED';
      const warning = isChangingToAdvanced ?
        this.i18n.t('respond.aggregationRules.confirmAdvancedQueryMessage') :
        this.i18n.t('respond.aggregationRules.confirmQueryBuilderMessage');
      this.send('showConfirmationDialog', 'change-query-type', { warning }, () => {
        this.send('update', 'ruleInfo.advancedUiFilterConditions', isChangingToAdvanced);
        this.send('clearQuery');
      });
    },
    handleAdvancedQueryChange(value) {
      this.send('update', 'ruleInfo.uiFilterConditions', value);
    },
    handleActionChange(value) {
      this.send('update', 'ruleInfo.action', value);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(AggregationRule);