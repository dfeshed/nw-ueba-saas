import { module, test } from 'qunit';
import Immutable from 'seamless-immutable';
import {
  hasSelectedClosedIncidents,
  getIncidentId,
  getIncidentInfo,
  getIncidentInfoStatus,
  getStorylineStatus,
  getViewMode,
  getInspectorWidth
} from 'respond/selectors/incidents';
import data from '../../data/data';

module('Unit | Utility | Incidents Selector');

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

const state = {
  respond: {
    incident
  }
};

test('Basic incident selectors', function(assert) {
  assert.equal(getIncidentId(state), 'INC-123', 'The returned value from the getIncidentId selector is as expected');
  assert.equal(getIncidentInfo(state), incident.info, 'The returned value from the getIncidentInfo selector is as expected');
  assert.equal(getIncidentInfoStatus(state), 'completed', 'The returned value from the getIncidentInfoStatus selector is as expected');
  assert.equal(getStorylineStatus(state), 'wait', 'The returned value from the getStorylineStatus selector is as expected');
  assert.equal(getViewMode(state), 'storyline', 'The returned value from the getViewMode selector is as expected');
  assert.equal(getInspectorWidth(state), 400, 'The returned value from the getInspectorWidth selector is as expected');
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

