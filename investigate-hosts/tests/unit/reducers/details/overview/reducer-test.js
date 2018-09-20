import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/overview/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../../helpers/make-pack-action';

module('Unit | Reducers | overview');

const initialState = {
  hostDetails: null,
  downloadId: null,
  exportJSONStatus: 'completed',
  arrangeSecurityConfigsBy: 'alphabetical',
  policyDetails: null,
  activeAlertTab: 'critical'
};

test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});

test('The RESET_INPUT_DATA will reset the state', function(assert) {
  const previous = Immutable.from({
    hostDetails: { hostName: 'test' }
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_INPUT_DATA });

  assert.deepEqual(result, initialState);
});

test('The FETCH_HOST_DETAILS sets the host details information', function(assert) {
  const previous = Immutable.from({
    hostDetails: null
  });
  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_HOST_DETAILS,
    payload: { data: { hostName: 'test' } }
  });
  const newEndState = reducer(previous, newAction);
  assert.equal(newEndState.hostDetails.hostName, 'test');
});

test('The FETCH_POLICY_DETAILS sets the policy details information', function(assert) {
  const previous = Immutable.from({
    policyDetails: null
  });
  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_POLICY_DETAILS,
    payload: { data: { scheduledScanConfig: { enabled: true } } }
  });
  const newEndState = reducer(previous, newAction);
  assert.equal(newEndState.policyDetails.scheduledScanConfig.enabled, true);
});

test('The FETCH_DOWNLOAD_FILECONTEXT_JOB_ID download id', function(assert) {
  const previous = Immutable.from({
    downloadId: null,
    exportJSONStatus: null
  });

  const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_DOWNLOAD_FILECONTEXT_JOB_ID });
  const startEndState = reducer(previous, startAction);
  assert.deepEqual(startEndState.exportJSONStatus, 'streaming');

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_DOWNLOAD_FILECONTEXT_JOB_ID,
    payload: { data: { id: 123123 } }
  });
  const newEndState = reducer(previous, newAction);
  assert.equal(newEndState.downloadId, 123123);
});

test('The ARRANGE_SECURITY_CONFIGURATIONS will set arrangeSecurityConfigsby to either alphabetical or status', function(assert) {
  const previous = Immutable.from({ arrangeSecurityConfigsBy: null });
  const result = reducer(previous,
    { type: ACTION_TYPES.ARRANGE_SECURITY_CONFIGURATIONS,
      payload: { arrangeBy: 'status' } });

  assert.deepEqual(result.arrangeSecurityConfigsBy, 'status');
});

test('The CHANGE_ALERT_TAB sets new tab to state', function(assert) {
  const previous = Immutable.from({
    activeAlertTab: 'critical'
  });

  const expectedEndState = {
    activeAlertTab: 'HIGH'
  };

  const endState = reducer(previous, { type: ACTION_TYPES.CHANGE_ALERT_TAB, payload: { tabName: 'HIGH' } });
  assert.deepEqual(endState, expectedEndState);
});
