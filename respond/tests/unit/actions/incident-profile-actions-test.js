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

