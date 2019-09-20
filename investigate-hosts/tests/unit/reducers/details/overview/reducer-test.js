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
  activeAlertTab: 'critical',
  hostOverview: null
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

test('FETCH_HOST_OVERVIEW', function(assert) {
  const previous = Immutable.from({
    hostOverview: null
  });
  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_HOST_OVERVIEW,
    payload: { data: { items: [{ id: '123' }] } }
  });
  const endState = reducer(previous, newAction);
  assert.deepEqual(endState.hostOverview, { id: '123' });
});

test('The UPDATE_EXCLUSION_LIST sets the updated isolation information', function(assert) {
  const previous = Immutable.from({
    endpoint: {
      overview: {
        hostOverview: {
          agentStatus: {
            isolationStatus: {
              isolated: true,
              comment: 'Test comment',
              excludedIps: ['1.2.3.4', '3ffe:1900:4545:3:200:f8ff:fe21:67cf']
            }
          }
        }
      }
    }
  });
  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.UPDATE_EXCLUSION_LIST,
    payload: { request: { data: { comment: 'updated comment', exclusionList: [{ ip: '1.2.3.8', ipv4: true }] } } }
  });
  const newEndState = reducer(previous, newAction);
  assert.deepEqual(newEndState.hostOverview.agentStatus.isolationStatus.excludedIps, ['1.2.3.8']);
  assert.equal(newEndState.hostOverview.agentStatus.isolationStatus.comment, 'updated comment', 'comment has been updated');
});