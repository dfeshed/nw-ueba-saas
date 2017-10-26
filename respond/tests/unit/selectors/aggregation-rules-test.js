import { module, test } from 'qunit';
import {
  getAggregationRules,
  getAggregationRulesStatus,
  getIsAggregationRulesTransactionUnderway,
  getSelectedAggregationRuleId,
  hasSelectedRule
} from 'respond/selectors/aggregation-rules';

module('Unit | Utility | Aggregation Rules Selectors');

const rules = [{ id: '123', name: 'Test rule 1' }, { id: '124', name: 'Test rule 2' }];

const aggregationRules = {
  rules,
  rulesStatus: 'complete',
  selectedRule: '124',
  isTransactionUnderway: true
};

const state = {
  respond: {
    aggregationRules
  }
};

test('Basic Aggregation Rules selectors', function(assert) {
  assert.equal(getAggregationRules(state), rules, 'The returned value from the getAggregationRules selector is as expected');
  assert.equal(getAggregationRulesStatus(state), 'complete', 'The returned value from the getAggregationRulesStatus selector is as expected');
  assert.equal(getSelectedAggregationRuleId(state), '124', 'The returned value from the getSelectedAggregationRuleId selector is as expected');
  assert.equal(hasSelectedRule(state), true, 'The returned value from the hasSelectedRule selector is as expected');
  assert.equal(getIsAggregationRulesTransactionUnderway(state), true, 'The returned value from the getIsAggregationRulesTransactionUnderway selector is as expected');
});