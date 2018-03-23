import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import {
  hasSelectedClosedIncidents,
  getIncidentId,
  getIncidentInfo,
  getIncidentInfoStatus,
  getViewMode,
  getInspectorWidth,
  getPriorityFilters,
  getStatusFilters,
  getIdFilters,
  getAssigneeFilters,
  hasAssigneeFilter,
  getIsUnassignedFilters,
  getCategoryFilters
} from 'respond/selectors/incidents';
import data from '../../data/data';

const { incidents: items } = data;

const info = { id: 'INC-123' };

const incident = Immutable.from({
  id: 'INC-123',
  info,
  infoStatus: 'completed',
  storylineStatus: 'wait',
  viewMode: 'storyline',
  inspectorWidth: 400
});

const incidents = Immutable.from({
  itemsFilters: {
    priority: ['LOW'],
    status: ['CLOSED', 'NEW'],
    id: 'INC-123',
    assignee: {
      field: 'assignee',
      isNull: true
    },
    'assignee.id': ['meiskm', 'local'],
    'categories.parent': ['ENVIRONMENTAL']
  }
});

const state = {
  respond: {
    incident,
    incidents
  }
};

module('Unit | Utility | Incidents Selector', function(hooks) {
  setupTest(hooks);

  test('Basic incident selectors', function(assert) {
    assert.equal(getIncidentId(state), 'INC-123', 'The returned value from the getIncidentId selector is as expected');
    assert.equal(getIncidentInfo(state), incident.info, 'The returned value from the getIncidentInfo selector is as expected');
    assert.equal(getIncidentInfoStatus(state), 'completed', 'The returned value from the getIncidentInfoStatus selector is as expected');
    assert.equal(getViewMode(state), 'storyline', 'The returned value from the getViewMode selector is as expected');
    assert.equal(getInspectorWidth(state), 400, 'The returned value from the getInspectorWidth selector is as expected');
  });

  test('Basic incidents selectors', function(assert) {
    assert.deepEqual(getPriorityFilters(state), ['LOW']);
    assert.deepEqual(getStatusFilters(state), ['CLOSED', 'NEW']);
    assert.equal(getIdFilters(state), 'INC-123');
    assert.deepEqual(getAssigneeFilters(state), ['meiskm', 'local']);
    assert.ok(hasAssigneeFilter(state));
    assert.ok(getIsUnassignedFilters(state));
    assert.deepEqual(getCategoryFilters(state), ['ENVIRONMENTAL']);
  });

  test('hasSelectedClosedIncidents is false when none of the selected incidents are closed', function(assert) {

    const state = {
      respond: {
        incidents: {
          items,
          itemsSelected: ['INC-96', 'INC-97', 'INC-101', 'INC-109']
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
          items,
          itemsSelected: ['INC-96', 'INC-97', 'INC-101', 'INC-109', 'INC-95']
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
          items,
          itemsSelected: ['INC-96', 'INC-97', 'INC-101', 'INC-109', 'INC-93']
        }
      }
    };

    const result = hasSelectedClosedIncidents(state);
    assert.equal(result, true, 'hasSelectedClosedIncidents is true when one is Closed - False Positive');
  });
});

