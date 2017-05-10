import Ember from 'ember';
import { module, test } from 'qunit';
import { LIFECYCLE } from 'redux-pack';
import incidentReducer from 'respond/reducers/respond/incident';
import ACTION_TYPES from 'respond/actions/types';
import makePackAction from '../../helpers/make-pack-action';

const { copy } = Ember;

const initialState = {
  info: null,
  infoStatus: null
};

module('Unit | Utility | Incident Profile Actions - Reducers');

test('When FETCH_INCIDENT_DETAILS starts, the infoStatus changes to wait', function(assert) {
  const initState = copy(initialState);
  const infoStatus = 'wait';
  const expectedEndState = {
    ...initState,
    infoStatus
  };
  const action = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_INCIDENT_DETAILS });
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_DETAILS fails, the infoStatus changes to error', function(assert) {
  const initState = copy(initialState);
  const infoStatus = 'error';
  const expectedEndState = {
    ...initState,
    infoStatus
  };
  const action = makePackAction(LIFECYCLE.FAILURE, { type: ACTION_TYPES.FETCH_INCIDENT_DETAILS });
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

test('When FETCH_INCIDENT_DETAILS succeeds, the info obj and infoStatus update appropriately', function(assert) {
  const initState = copy(initialState);
  const info = { testing: 123 };
  const infoStatus = 'completed';
  const expectedEndState = {
    ...initState,
    infoStatus,
    info
  };
  const action = makePackAction(LIFECYCLE.SUCCESS, { type: ACTION_TYPES.FETCH_INCIDENT_DETAILS, payload: { data: info } });
  const endState = incidentReducer(initState, action);
  assert.deepEqual(endState, expectedEndState);
});

