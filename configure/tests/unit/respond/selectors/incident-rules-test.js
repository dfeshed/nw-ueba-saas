import { module, test } from 'qunit';
import {
  getIncidentRules,
  getIncidentRulesStatus,
  getIsIncidentRulesTransactionUnderway,
  getSelectedIncidentRuleId,
  hasSelectedRule
} from 'configure/reducers/respond/incident-rules/selectors';

module('Unit | Utility | Incident Rules Selectors');

const rules = [{ id: '123', name: 'Test rule 1' }, { id: '124', name: 'Test rule 2' }];

const incidentRules = {
  rules,
  rulesStatus: 'complete',
  selectedRule: '124',
  isTransactionUnderway: true
};

const state = {
  configure: {
    respond: {
      incidentRules
    }
  }
};

test('Basic Incident Rules selectors', function(assert) {
  assert.equal(getIncidentRules(state), rules, 'The returned value from the getIncidentRules selector is as expected');
  assert.equal(getIncidentRulesStatus(state), 'complete', 'The returned value from the getIncidentRulesStatus selector is as expected');
  assert.equal(getSelectedIncidentRuleId(state), '124', 'The returned value from the getSelectedIncidentRuleId selector is as expected');
  assert.equal(hasSelectedRule(state), true, 'The returned value from the hasSelectedRule selector is as expected');
  assert.equal(getIsIncidentRulesTransactionUnderway(state), true, 'The returned value from the getIsIncidentRulesTransactionUnderway selector is as expected');
});