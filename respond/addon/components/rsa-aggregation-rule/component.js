import Component from '@ember/component';
import { getRule, getFields } from 'respond/actions/creators/aggregation-rule-creators';
import { getRuleInfo, isLoading } from 'respond/selectors/aggregation-rule';
import { connect } from 'ember-redux';

const stateToComputed = (state) => {
  return {
    ruleInfo: getRuleInfo(state),
    isLoading: isLoading(state)
  };
};


const dispatchToActions = function(dispatch) {
  return {
    getRule(ruleId) {
      dispatch(getRule(ruleId));
    },
    getFields() {
      dispatch(getFields());
    }
  };
};

/**
 * Represents the details of an individual aggregation rule including edit functionality
 * @class Aggregation Rule
 * @public
 */
const AggregationRule = Component.extend({
  classNames: ['rsa-aggregation-rule'],

  ruleId: null,

  onInit: function() {
    this.send('getRule', this.get('ruleId'));
    this.send('getFields');
  }.on('init')
});

export default connect(stateToComputed, dispatchToActions)(AggregationRule);