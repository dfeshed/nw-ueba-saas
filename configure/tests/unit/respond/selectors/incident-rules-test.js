import { module, test } from 'qunit';
import {
  getIncidentRules,
  getIncidentRulesStatus,
  getIsIncidentRulesTransactionUnderway,
  getSelectedIncidentRules,
  hasOneSelectedRule,
  isNoneSelected,
  isAllSelected
} from 'configure/reducers/respond/incident-rules/selectors';

module('Unit | Utility | Incident Rules Selectors');

const rules = [{ id: '123', name: 'Test rule 1' }, { id: '124', name: 'Test rule 2' }];

const incidentRules = {
  rules,
  rulesStatus: 'complete',
  selectedRules: ['124'],
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
  assert.equal(getSelectedIncidentRules(state), '124', 'The returned value from the getSelectedIncidentRules selector is as expected');
  assert.equal(hasOneSelectedRule(state), true, 'The returned value from the hasOneSelectedRule selector is as expected');
  assert.equal(getIsIncidentRulesTransactionUnderway(state), true, 'The returned value from the getIsIncidentRulesTransactionUnderway selector is as expected');
});

test('hasOneSelectedRule returns false when 2 rules are selected', function(assert) {
  const state = {
    configure: {
      respond: {
        incidentRules: {
          selectedRules: ['124', '999']
        }
      }
    }
  };
  assert.equal(hasOneSelectedRule(state), false, 'The returned value from the hasOneSelectedRule selector is as expected');
});

test('hasOneSelectedRule returns false when 0 rules are selected', function(assert) {
  const state = {
    configure: {
      respond: {
        incidentRules: {
          selectedRules: []
        }
      }
    }
  };
  assert.equal(hasOneSelectedRule(state), false, 'The returned value from the hasOneSelectedRule selector is as expected');
});

test('isNoneSelected returns true only when 0 rules are selected', function(assert) {
  const state = {
    configure: {
      respond: {
        incidentRules: {
          selectedRules: []
        }
      }
    }
  };
  assert.equal(isNoneSelected(state), true, 'The returned value from the isNoneSelected selector is as expected');

  const state2 = {
    configure: {
      respond: {
        incidentRules: {
          selectedRules: ['foo']
        }
      }
    }
  };
  assert.equal(isNoneSelected(state2), false, 'The returned value from the isNoneSelected selector is as expected');
});

test('isAllSelected returns true only when all rules are selected', function(assert) {
  assert.equal(isAllSelected(state), false, 'The returned value from the isAllSelected selector is as expected');

  const state2 = {
    configure: {
      respond: {
        incidentRules: {
          ...incidentRules,
          selectedRules: ['123', '124']
        }
      }
    }
  };
  assert.equal(isAllSelected(state2), true, 'The returned value from the isAllSelected selector is as expected');
});