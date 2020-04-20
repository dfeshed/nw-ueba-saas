import { module, test } from 'qunit';
import ruleNormalizer from 'configure/reducers/respond/incident-rules/incident-rule-normalizer';

module('Unit | Utility | Incident Rule Normalizer');

// All of the match conditions in an incident rule are persisted as stringified JSON. The match conditions are
// composed of groups, and groups are composed of groups or conditions that belong to groups. It can have groups within
// groups. This nesting can make it very hard to work with. The Aggregation Rule Normalizer supports two primary features.
//
// First, it can take the stringified JSON that is persisted and turn it into a flat set of groups and conditions,
// where each group and each condition has a pointer to another group (by groupId) as well as its own ID. This allows
// for easy lookup and easy modification of a group or condition without having to traverse a tree structure.
//
// Second, it can reconstruct the original, nested, stringified JSON format, which is what what the Respond server
// microservice understands.

const originalFormat = '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"and",' +
  '"filters":[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.signature_id","operator":"=","value":"1"}},' +
  '{"alertRuleFilter":{"filterType":"FILTER","property":"alert.name","operator":"=","value":"matt"}},' +
  '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"or","filters":' +
  '[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.events.domain","operator":"=","value":"g00gle.com"}}]}},' +
  '{"alertRuleFilterGroup":{"filterType":"FILTER_GROUP","logicalOperator":"not","filters":' +
  '[{"alertRuleFilter":{"filterType":"FILTER","property":"alert.destination_country","operator":"=","value":"USA"}}]}}]}}';
const normalizedConditionGroups = {
  0: {
    filterType: 'FILTER_GROUP',
    logicalOperator: 'and',
    id: 0
  },
  1: {
    id: 1,
    filterType: 'FILTER_GROUP',
    logicalOperator: 'or',
    groupId: 0
  },
  2: {
    id: 2,
    filterType: 'FILTER_GROUP',
    logicalOperator: 'not',
    groupId: 0
  }
};
const normalizedConditions = {
  0: {
    id: 0,
    filterType: 'FILTER',
    property: 'alert.signature_id',
    operator: '=',
    value: '1',
    groupId: 0
  },
  1: {
    id: 1,
    filterType: 'FILTER',
    property: 'alert.name',
    operator: '=',
    value: 'matt',
    groupId: 0
  },
  2: {
    id: 2,
    filterType: 'FILTER',
    property: 'alert.events.domain',
    operator: '=',
    value: 'g00gle.com',
    groupId: 1
  },
  3: {
    id: 3,
    filterType: 'FILTER',
    property: 'alert.destination_country',
    operator: '=',
    value: 'USA',
    groupId: 2
  }
};

test('it will properly flatten the original format into a normalized data structure', function(assert) {
  const normalized = ruleNormalizer.processRuleConfiguration(originalFormat);
  assert.deepEqual(normalized.groups, normalizedConditionGroups);
  assert.deepEqual(normalized.conditions, normalizedConditions);
});

test('it will properly reconstruct JSON payload format from normalized state', function(assert) {
  const json = ruleNormalizer.toJSON(normalizedConditionGroups, normalizedConditions);
  assert.equal(json, originalFormat);
});
