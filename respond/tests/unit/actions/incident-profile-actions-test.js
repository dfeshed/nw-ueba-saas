import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import incidentReducer from 'respond/reducers/respond/incident';
import ACTION_TYPES from 'respond/actions/types';
import makePackAction from '../../helpers/make-pack-action';

const initialState = Immutable.from({
  info: null,
  infoStatus: null
});

module('Unit | Utility | Incident Profile Actions - Reducers');

test('When FETCH_INCIDENT_DETAILS starts, the infoStatus changes to wait', function(assert) {
  const infoStatus = 'wait';
  const expectedEndState = {
    ...initialState,
    infoStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_INCIDENT_DETAILS });
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_DETAILS fails, the infoStatus changes to error', function(assert) {
  const infoStatus = 'error';
  const expectedEndState = {
    ...initialState,
    infoStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_INCIDENT_DETAILS });
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_DETAILS succeeds, the info obj and infoStatus update appropriately', function(assert) {
  const info = { testing: 123 };
  const infoStatus = 'completed';
  const expectedEndState = {
    ...initialState,
    infoStatus,
    info
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_INCIDENT_DETAILS, payload: { data: info } });
  const endState = incidentReducer(initialState, action);
  assert.deepEqual(endState, expectedEndState);
});


test('With INITIALIZE_INCIDENT, the incident state is updated as expected', function(assert) {
  const initState = {
    id: 'INC-123',
    info: {
      name: 'Testing 123'
    },
    storyline: [{ id: 'x' }],
    storylineEvents: [{ id: 'y' }],
    viewMode: 'storyline',
    inspectorWidth: 500,
    hideViz: true,
    isShowingTasksAndJournal: true,
    tasksJournalMode: 'search',
    defaultSearchTimeFrameName: 'ALL_DATA',
    defaultSearchEntityType: 'DOMAIN',
    addRelatedIndicatorsStatus: 'success',
    infoStatus: 'success',
    stopStorylineStream: () => {},
    searchEntity: {},
    searchStatus: 'complete',
    searchResults: [{}],
    searchTimeFrameName: 'ALL_DATA',
    selection: {
      type: 'storyPoint',
      ids: ['12345']
    },
    storylineEventsStatus: 'complete',
    storylineStatus: 'complete',
    tasks: [{}],
    tasksStatus: 'wait'
  };

  const expectedEndState = {
    id: 'INC-321',
    info: null,
    storyline: null,
    storylineEvents: null,
    storylineEventsStatus: null,
    viewMode: 'storyline',
    inspectorWidth: 500,
    hideViz: true,
    isShowingTasksAndJournal: true,
    tasksJournalMode: 'search',
    defaultSearchTimeFrameName: 'ALL_DATA',
    defaultSearchEntityType: 'DOMAIN',
    addRelatedIndicatorsStatus: null,
    infoStatus: null,
    stopStorylineStream: null,
    searchEntity: null,
    searchStatus: null,
    searchResults: null,
    searchTimeFrameName: null,
    selection: {
      type: '',
      ids: []
    },
    storylineStatus: null,
    tasks: [],
    tasksStatus: null
  };

  const action = {
    type: ACTION_TYPES.INITIALIZE_INCIDENT,
    payload: 'INC-321'
  };
  const endState = incidentReducer(Immutable.from(initState), action);
  assert.deepEqual(endState, expectedEndState, 'incident state should be reset to defaults except for inspectorWidth,' +
    'hideViz, viewMode, isShowingTasksAndJournal, tasksJournalMode, defaultSearchTimeFrameName, defaultSearchEntityType');
});
