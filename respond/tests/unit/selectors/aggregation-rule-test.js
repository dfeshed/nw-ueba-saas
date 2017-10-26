import { module, test } from 'qunit';
import {
  getRuleInfo,
  getRuleStatus,
  getRuleConditionGroups,
  getRuleConditions,
  getRootConditionGroup,
  getFieldsStatus,
  getFields,
  isLoading
} from 'respond/selectors/aggregation-rule';
import rule from '../../data/subscriptions/aggregation-rules/queryRecord/data';
import fields from '../../data/subscriptions/aggregation-fields/findAll/data';

module('Unit | Utility | Aggregation Rule Selectors');

const rootGroup = { id: 2 };
const conditionGroups = { 2: rootGroup, 3: { id: 3 } };
const conditions = { 1: { id: 1, groupId: 2 } };

const aggregationRule = {
  rule,
  ruleStatus: 'wait',
  conditionGroups,
  conditions,
  fields,
  fieldsStatus: 'error'
};

const state = {
  respond: {
    aggregationRule
  }
};

test('Basic Aggregation Rules selectors', function(assert) {
  assert.equal(getRuleInfo(state), rule, 'The returned value from the getRuleInfo selector is as expected');
  assert.equal(getRuleStatus(state), 'wait', 'The returned value from the getRuleStatus selector is as expected');
  assert.equal(getRuleConditionGroups(state), conditionGroups, 'The returned value from the getRuleConditionGroups selector is as expected');
  assert.equal(getRuleConditions(state), conditions, 'The returned value from the getRuleConditions selector is as expected');
  assert.equal(getFieldsStatus(state), 'error', 'The returned value from the getFieldsStatus selector is as expected');
  assert.equal(getFields(state), fields, 'The returned value from the getFields selector is as expected');
  assert.equal(getRootConditionGroup(state), rootGroup, 'The returned value form the getRootConditionsGroup selector is as expected');
  assert.equal(isLoading(state), true, 'The returned value form the isLoading selector is as expected');
});