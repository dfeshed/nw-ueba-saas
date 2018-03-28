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

module('Unit | Utility | Incident Profile Actions - Reducers', function() {

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
      viewMode: 'storyline',
      inspectorWidth: 500,
      hideViz: true,
      isShowingTasksAndJournal: true,
      tasksJournalMode: 'search',
      defaultSearchTimeFrameName: 'ALL_DATA',
      defaultSearchEntityType: 'DOMAIN',
      addRelatedIndicatorsStatus: 'success',
      infoStatus: 'success',
      searchEntity: {},
      searchStatus: 'complete',
      searchResults: [{}],
      searchTimeFrameName: 'ALL_DATA',
      selection: {
        type: 'storyPoint',
        ids: ['12345']
      },
      tasks: [{}],
      tasksStatus: 'wait'
    };

    const expectedEndState = {
      id: 'INC-321',
      info: null,
      viewMode: 'storyline',
      inspectorWidth: 500,
      hideViz: true,
      isShowingTasksAndJournal: true,
      tasksJournalMode: 'search',
      defaultSearchTimeFrameName: 'ALL_DATA',
      defaultSearchEntityType: 'DOMAIN',
      addRelatedIndicatorsStatus: null,
      infoStatus: null,
      searchEntity: null,
      searchStatus: null,
      searchResults: null,
      searchTimeFrameName: null,
      selection: {
        type: '',
        ids: []
      },
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

  test('When CREATE_REMEDIATION_TASK starts, the tasksStatus changes to "creating"', function(assert) {
    const initState = {
      tasks: [],
      tasksStatus: null
    };
    const tasksStatus = 'creating';
    const expectedEndState = {
      tasks: [],
      tasksStatus
    };
    const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.CREATE_REMEDIATION_TASK });
    const endState = incidentReducer(Immutable.from(initState), action);
    assert.deepEqual(endState, expectedEndState);
  });

  test('When CREATE_REMEDIATION_TASK completes, the tasksStatus changes to null, and tasks array is updated', function(assert) {
    const initState = {
      tasks: [],
      tasksStatus: 'creating'
    };
    const tasksStatus = null;
    const createdTask = { id: 'REM-1' };
    const expectedEndState = {
      tasks: [createdTask],
      tasksStatus
    };
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.CREATE_REMEDIATION_TASK,
      payload: { data: createdTask }
    });
    const endState = incidentReducer(Immutable.from(initState), action);
    assert.deepEqual(endState, expectedEndState);
  });

  test('When CREATE_REMEDIATION_TASK fails, the tasksStatus changes to null', function(assert) {
    const initState = {
      tasks: [],
      tasksStatus: 'creating'
    };
    const tasksStatus = null;
    const expectedEndState = {
      tasks: [],
      tasksStatus
    };
    const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.CREATE_REMEDIATION_TASK });
    const endState = incidentReducer(Immutable.from(initState), action);
    assert.deepEqual(endState, expectedEndState);
  });

  test('When ESCALATE_INCIDENT succeeds, the state is updated', function(assert) {
    const initState = {
      info: {
        escalationStatus: 'NON_ESCALATED'
      }
    };
    const expectedEndState = {
      info: {
        escalationStatus: 'ESCALATED'
      }
    };
    const action = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.ESCALATE_INCIDENT,
      payload: { data: { escalationStatus: 'ESCALATED' } }
    });
    const endState = incidentReducer(Immutable.from(initState), action);
    assert.deepEqual(endState, expectedEndState);
  });
});
