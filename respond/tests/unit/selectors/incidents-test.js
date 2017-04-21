import { module, test } from 'qunit';
import { hasSelectedClosedIncidents } from 'respond/selectors/incidents';
import { incidents } from '../../server/data';

module('Unit | Mixin | Incidents Selector');

test('hasSelectedClosedIncidents is false when none of the selected incidents are closed', function(assert) {

  const state = {
    respond: {
      incidents: {
        incidents,
        incidentsSelected: ['INC-96', 'INC-97', 'INC-101', 'INC-109']
      }
    }
  };

  const result = hasSelectedClosedIncidents(state);
  assert.equal(result, false, 'None of the selected incidents are closed');
});

test('hasSelectedClosedIncidents is true when at least one of the incidents has status "Closed"', function(assert) {

  const state = {
    respond: {
      incidents: {
        incidents,
        incidentsSelected: ['INC-96', 'INC-97', 'INC-101', 'INC-109', 'INC-95']
      }
    }
  };

  const result = hasSelectedClosedIncidents(state);
  assert.equal(result, true, 'hasSelectedClosedIncidents is true when one is closed');
});

test('hasSelectedClosedIncidents is true when at least one of the incidents has status "Closed - False Positive"', function(assert) {

  const state = {
    respond: {
      incidents: {
        incidents,
        incidentsSelected: ['INC-96', 'INC-97', 'INC-101', 'INC-109', 'INC-93']
      }
    }
  };

  const result = hasSelectedClosedIncidents(state);
  assert.equal(result, true, 'hasSelectedClosedIncidents is true when one is Closed - False Positive');
});