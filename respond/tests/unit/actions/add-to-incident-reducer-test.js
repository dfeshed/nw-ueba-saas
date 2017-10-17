import Immutable from 'seamless-immutable';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import ACTION_TYPES from 'respond/actions/types';
import reducer from 'respond/reducers/respond/alerts/add-to-incident';
import makePackAction from '../../helpers/make-pack-action';

module('Unit | Utility | Add To Incident Reducer');

const initialState = {
  incidentSearchText: null,
  incidentSearchSortBy: 'created',
  incidentSearchSortIsDescending: true,
  incidentSearchStatus: null,
  incidentSearchResults: [],
  selectedIncident: null,
  stopSearchStream: null,
  isAddAlertsInProgress: false
};

test('With ALERTS_SEARCH_INCIDENTS_STARTED, the incidentSearchStatus changes to streaming', function(assert) {
  const incident = { id: 'INC-123' };
  const state = {
    ...initialState,
    incidentSearchResults: [incident],
    selectedIncident: incident
  };
  const incidentSearchStatus = 'streaming';
  const expectedEndState = {
    ...initialState,
    incidentSearchStatus,
    incidentSearchResults: [],
    selectedIncident: null
  };
  const action = { type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_STARTED };
  const endState = reducer(Immutable.from(state), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With ALERTS_SEARCH_INCIDENTS_ERROR fails, the incidentSearchStatus changes to error', function(assert) {
  const incidentSearchStatus = 'error';
  const stopSearchStream = null;
  const expectedEndState = {
    ...initialState,
    incidentSearchStatus,
    stopSearchStream
  };
  const action = { type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_ERROR };
  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With ALERTS_SEARCH_INCIDENTS_RETRIEVE_BATCH, the incidentSearchResults are updated excluding any CLOSED incidents', function(assert) {
  const closedIncident = { id: 'INC-789', status: 'CLOSED' };
  const incidentSearchResults = [ { id: 'INC-123' }, { id: 'INC-321' }, closedIncident];
  const expectedEndState = {
    ...initialState,
    incidentSearchResults: incidentSearchResults.without(closedIncident),
    incidentSearchStatus: 'streaming'
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_RETRIEVE_BATCH,
    payload: { data: incidentSearchResults, meta: { complete: false } }
  });
  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With ALERTS_SEARCH_INCIDENTS_STREAM_INITIALIZED, the payload function is set to stopSearchStream', function(assert) {
  const stopStreamFunc = () => {};
  const expectedEndState = {
    ...initialState,
    stopSearchStream: stopStreamFunc
  };
  const action = { type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_STREAM_INITIALIZED, payload: stopStreamFunc };
  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With ALERTS_SEARCH_INCIDENTS_COMPLETED, the stopSearchStream is set to null', function(assert) {
  const stopSearchStreamFunc = () => {};
  const state = {
    ...initialState,
    stopSearchStream: stopSearchStreamFunc
  };
  const expectedEndState = {
    ...initialState // default in initial state has stopSearchStream as null
  };
  const action = { type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_COMPLETED };
  const endState = reducer(Immutable.from(state), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With ALERTS_UPDATE_SEARCH_INCIDENTS_TEXT, the incidentSearchText is updated', function(assert) {
  const incidentSearchText = 'fake news';
  const expectedEndState = {
    ...initialState,
    incidentSearchText
  };
  const action = { type: ACTION_TYPES.ALERTS_UPDATE_SEARCH_INCIDENTS_TEXT, payload: incidentSearchText };
  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With ALERTS_UPDATE_SEARCH_INCIDENTS_SORTBY, the incidentSearchSortBy and incidentSearchSortIsDescending is updated', function(assert) {
  const incidentSearchSortBy = 'name';
  const incidentSearchSortIsDescending = false;
  const expectedEndState = {
    ...initialState,
    incidentSearchSortBy,
    incidentSearchSortIsDescending
  };
  const action = {
    type: ACTION_TYPES.ALERTS_UPDATE_SEARCH_INCIDENTS_SORTBY,
    payload: {
      sortField: incidentSearchSortBy,
      isSortDescending: incidentSearchSortIsDescending
    }
  };
  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With ALERTS_ADD_TO_INCIDENT, the isAddAlertsInProgress is set to true on start', function(assert) {
  const expectedEndState = {
    ...initialState,
    isAddAlertsInProgress: true
  };
  const action = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.ALERTS_ADD_TO_INCIDENT
  });
  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});


test('With ALERTS_SEARCH_INCIDENTS_SELECT, the selectedIncident is updated', function(assert) {
  const selectedIncident = { id: 'INC-123' };
  const expectedEndState = {
    ...initialState,
    selectedIncident
  };
  const action = {
    type: ACTION_TYPES.ALERTS_SEARCH_INCIDENTS_SELECT,
    payload: selectedIncident
  };
  const endState = reducer(Immutable.from(initialState), action);
  assert.deepEqual(endState, expectedEndState);
});

test('With CLEAR_SEARCH_INCIDENTS_RESULTS_FOR_ALERTS, the state is reset to initial state', function(assert) {
  const state = {
    ...initialState,
    incidentSearchResults: [{}, {}, {}],
    incidentSearchStatus: 'streaming',
    selectedIncident: {}
  };
  const expectedEndState = { ...initialState };

  const action = { type: ACTION_TYPES.CLEAR_SEARCH_INCIDENTS_RESULTS_FOR_ALERTS };
  const endState = reducer(Immutable.from(state), action);
  assert.deepEqual(endState, expectedEndState);
});