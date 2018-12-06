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

test('FETCH_AGENT_STATUS', function(assert) {
  const payload = { data: [{ agentId: '123', scanStatus: 'idle' }, { agentId: '134', scanStatus: 'scanPending' }] };
  const previous1 = Immutable.from({ hostDetails: null });
  const result1 = reducer(previous1,
    { type: ACTION_TYPES.FETCH_AGENT_STATUS, payload });

  assert.deepEqual(result1.hostDetails, null, 'hostDetails is null');

  const previous2 = Immutable.from({ hostDetails: { id: '123', agentStatus: { agentId: '123', scanStatus: 'scanning' } } });
  const result2 = reducer(previous2,
    { type: ACTION_TYPES.FETCH_AGENT_STATUS, payload });

  assert.equal(result2.hostDetails.agentStatus.scanStatus, 'idle', 'updated scanStatus is idle');

  const previous3 = Immutable.from({ hostDetails: { id: '123', agentStatus: { agentId: '123', scanStatus: 'scanning' } } });
  const result3 = reducer(previous3,
    { type: ACTION_TYPES.FETCH_AGENT_STATUS, payload: null });

  assert.equal(result3.hostDetails.agentStatus.scanStatus, 'scanning', 'scanStatus is not updated');
});
