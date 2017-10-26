import Component from '@ember/component';
import { getRuleInfo, isLoading } from 'respond/selectors/aggregation-rule';
import { connect } from 'ember-redux';

const stateToComputed = (state) => {
  return {
    ruleInfo: getRuleInfo(state),
    isLoading: isLoading(state)
  };
};

/**
 * Represents the details of an individual aggregation rule including edit functionality
 * @class Aggregation Rule
 * @public
 */
const AggregationRule = Component.extend({
  classNames: ['rsa-aggregation-rule']
});

export default connect(stateToComputed)(AggregationRule);