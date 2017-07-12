import Ember from 'ember';
import { module, test } from 'qunit';
import incidentReducer from 'respond/reducers/respond/incident';
import ACTION_TYPES from 'respond/actions/types';
import makePackAction from '../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

const { copy } = Ember;

const initialState = {
  storyline: null,
  storylineStatus: false,
  searchResults: null
};

module('Unit | Utility | Incident Storyline Actions - Reducers');

test('When FETCH_INCIDENT_STORYLINE starts, the storylineStatus changes to streaming', function(assert) {
  const initState = copy(initialState);
  const storylineStatus = 'streaming';
  const storyline = [];
  const expectedEndState = {
    ...initState,
    storyline,
    storylineStatus
  };
  const action = { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_STARTED };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE fails, the storylineStatus changes to error', function(assert) {
  const initState = copy(initialState);
  const storylineStatus = 'error';
  const stopStorylineStream = null;
  const expectedEndState = {
    ...initState,
    storylineStatus,
    stopStorylineStream
  };
  const action = { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_ERROR };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_STORYLINE retrieves a batch, the storyline and storylineStatus update appropriately', function(assert) {
  const initState = copy(initialState);
  const storyline = [ { testing: 123 } ];
  const storylineStatus = 'completed';
  const expectedEndState = {
    ...initState,
    storylineStatus,
    storyline
  };
  const action = { type: ACTION_TYPES.FETCH_INCIDENT_STORYLINE_RETRIEVE_BATCH, payload: { data: storyline, meta: { complete: true } } };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_STARTED, the incident search state is reset appropriately', function(assert) {
  const initState = copy(initialState);
  const entityId = '10.20.30.40';
  const entityType = 'IP';
  const timeFrameName = 'LAST_24_HOURS';
  const devices = ['source.device'];
  const searchResults = [];
  const searchStatus = 'streaming';
  const expectedEndState = {
    ...initState,
    searchEntity: { type: entityType, id: entityId },
    searchTimeFrameName: timeFrameName,
    searchDevices: devices,
    searchResults,
    searchStatus
  };
  const action = {
    type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_STARTED,
    payload: { entityId, entityType, timeFrameName, devices }
  };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_STREAM_INITIALIZED, the given stop function is stored in state', function(assert) {
  const initState = copy(initialState);
  const stopSearchStream = function() {};
  const expectedEndState = {
    ...initState,
    stopSearchStream
  };
  const action = { type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_STREAM_INITIALIZED, payload: stopSearchStream };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_RETRIEVE_BATCH, the searchResults & searchStatus state are updated correctly', function(assert) {
  const initState = copy(initialState);
  const data = [ { id: 1, alert: { name: 'Alert 1' } } ];
  const expectedEndState = {
    ...initState,
    searchResults: data,
    searchStatus: 'complete'
  };
  const action = {
    type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_RETRIEVE_BATCH,
    payload: {
      code: 0,
      data,
      meta: {
        complete: true
      }
    }
  };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_COMPLETED, the search stop function is cleared from state', function(assert) {
  const initState = copy(initialState);
  const stopSearchStream = null;
  const expectedEndState = {
    ...initState,
    stopSearchStream
  };
  const action = { type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_COMPLETED };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_STOPPED, the search status & stop function state are updated correctly', function(assert) {
  const initState = copy(initialState);
  const stopSearchStream = null;
  const searchStatus = 'stopped';
  const expectedEndState = {
    ...initState,
    stopSearchStream,
    searchStatus
  };
  const action = { type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_STOPPED };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When SEARCH_RELATED_INDICATORS_ERROR, the search status & stop function state are updated correctly', function(assert) {
  const initState = copy(initialState);
  const stopSearchStream = null;
  const searchStatus = 'error';
  const expectedEndState = {
    ...initState,
    stopSearchStream,
    searchStatus
  };
  const action = { type: ACTION_TYPES.SEARCH_RELATED_INDICATORS_ERROR };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When ADD_RELATED_INDICATORS succeeds, the alerts in the payload are added to the storyline', function(assert) {
  const alert1 = { id: 1, alert: { name: 'Alert1' }, partOfIncident: true, incidentId: 'INC-101' };
  const alert2 = { id: 2, alert: { name: 'Alert2' }, partOfIncident: true, incidentId: 'INC-101' };
  const alert3 = { id: 3, alert: { name: 'Alert3' }, partOfIncident: true, incidentId: 'INC-101' };
  const initStoryline = [ alert1 ];
  const payload = { code: 0, data: [ alert2, alert3 ] };
  const expectedEndStoryline = initStoryline.concat(payload.data);

  const initState = copy(initialState);
  initState.storyline = initStoryline;
  const expectedEndState = {
    ...initState,
    storyline: expectedEndStoryline,
    addRelatedIndicatorsStatus: 'success'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.ADD_RELATED_INDICATORS, payload });
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When ADD_RELATED_INDICATORS succeeds, the added alerts are updated in searchResults, if found there', function(assert) {
  const alert1 = { id: 1, alert: { name: 'Alert1' } };
  const alert2 = { id: 2, alert: { name: 'Alert2' } };
  const alert1Updated = { id: 1, alert: { name: 'Alert1' }, partOfIncident: true, incidentId: 'INC-101' };
  const initStoryline = null;
  const initSearchResults = [ alert1, alert2 ];
  const payload = { code: 0, data: [ alert1Updated ] };
  const expectedEndStoryline = [ alert1Updated ];
  const expectedEndSearchResults = [ alert1Updated, alert2 ];

  const initState = copy(initialState);
  initState.storyline = initStoryline;
  initState.searchResults = initSearchResults;
  const expectedEndState = {
    ...initState,
    storyline: expectedEndStoryline,
    searchResults: expectedEndSearchResults,
    addRelatedIndicatorsStatus: 'success'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.ADD_RELATED_INDICATORS, payload });
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When CLEAR_ADD_RELATED_INDICATORS_STATUS, the add status is reset to null', function(assert) {
  const initState = copy(initialState);
  initState.addRelatedIndicatorsStatus = 'wait';
  const expectedEndState = {
    ...initState,
    addRelatedIndicatorsStatus: null
  };
  const action = { type: ACTION_TYPES.CLEAR_ADD_RELATED_INDICATORS_STATUS };
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});
