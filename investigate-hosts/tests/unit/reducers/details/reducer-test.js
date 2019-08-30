import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import * as SHARED_ACTION_TYPES from 'investigate-shared/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | details');
const initialState = {
  agentId: null,
  scanTime: null,
  animation: 'default',
  isLatestSnapshot: null,
  snapShots: null,
  isDetailRightPanelVisible: true,
  isSnapshotsLoading: false,
  activeHostDetailPropertyTab: 'FILE_DETAILS',
  downloadLink: null
};
test('should return the initial state', function(assert) {
  const result = reducer(undefined, {});
  assert.deepEqual(result, initialState);
});

test('The RESET_INPUT_DATA action reset the state to initial state', function(assert) {
  const previous = Immutable.from({
    agentId: 1,
    scanTime: new Date(),
    animation: 'toDown',
    snapShots: []
  });
  const result = reducer(previous, { type: ACTION_TYPES.RESET_INPUT_DATA });

  assert.deepEqual(result, initialState, 'Expecting to reset to initial state');
});

test('The INITIALIZE_DATA will set the agentId and scan time', function(assert) {
  const previous = Immutable.from({
    agentId: null,
    scanTime: null,
    snapShots: []
  });
  const result = reducer(previous, { type: ACTION_TYPES.INITIALIZE_DATA, payload: { agentId: 111 } });

  assert.equal(result.agentId, 111, 'Expecting agentId equals to 111');
  assert.equal(result.isLatestSnapshot, false, 'No latest snapshot present, hence false');
});

test('The INITIALIZE_DATA will set the agentId and scan time and isLatestSnapshot', function(assert) {
  const previous = Immutable.from({
    agentId: null,
    scanTime: null,
    snapShots: [{ scanStartTime: '2019-08-29T11:18:21.557+0000', serviceId: 'b0624320-0175-4ee1-9c8b-48e0bc7dfe4b', serviceName: 'EPS1 - Endpoint Server' },
      { scanStartTime: '2019-08-29T11:14:20.559+0000', serviceId: 'b0624320-0175-4ee1-9c8b-48e0bc7dfe4b', serviceName: 'EPS1 - Endpoint Server' }]
  });
  const result = reducer(previous, { type: ACTION_TYPES.INITIALIZE_DATA, payload: { agentId: 111, scanTime: '2019-08-29T11:18:21.557+0000' } });

  assert.equal(result.agentId, 111, 'Expecting agentId equals to 111');
  assert.equal(result.isLatestSnapshot, true, 'Latest snapshot selected, hence true');
});

test('The SET_ANIMATION will sets the animation to the state', function(assert) {
  const previous = Immutable.from({
    animation: 'default'
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_ANIMATION, payload: 'toDown' });
  assert.equal(result.animation, 'toDown');
});

test('The SET_DOWNLOAD_FILE_LINK will sets the file download link to the state', function(assert) {
  const previous = Immutable.from({
    downloadLink: 'oldLink'
  });
  const result = reducer(previous, { type: SHARED_ACTION_TYPES.SET_DOWNLOAD_FILE_LINK, payload: '/rsa/endpoint/serverId/file/download?id=id&filename=fileName.zip' });
  assert.equal(result.downloadLink, '/rsa/endpoint/serverId/file/download?id=id&filename=fileName.zip');
});

test('The SET_HOST_DETAIL_PROPERTY_TAB will set the state of active tab', function(assert) {
  const previous = Immutable.from({
    activeHostDetailPropertyTab: 'FILE_DETAILS'
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_HOST_DETAIL_PROPERTY_TAB, payload: { tabName: 'RISK' } });
  assert.equal(result.activeHostDetailPropertyTab, 'RISK', 'Risk tab is selected');
});


test('The SET_SCAN_TIME will sets the selected scan time to the state', function(assert) {
  const previous = Immutable.from({
    scanTime: '12345566'
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_SCAN_TIME, payload: 1234567890 });
  assert.equal(result.scanTime, 1234567890);
});

test('The FETCH_ALL_SNAP_SHOTS sets all the fetched snapshot to the state', function(assert) {
  const previous = Immutable.from({
    snapShots: null,
    isSnapshotsLoading: false
  });

  const startAction = makePackAction(LIFECYCLE.START, {
    type: ACTION_TYPES.FETCH_ALL_SNAP_SHOTS
  });

  const endState = reducer(previous, startAction);
  assert.equal(endState.isSnapshotsLoading, true);

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_ALL_SNAP_SHOTS,
    payload: { data: [ 1231231, 1231231] }
  });
  const newEndState = reducer(previous, newAction);

  assert.equal(newEndState.snapShots.length, 2);
  assert.equal(newEndState.isSnapshotsLoading, false);
});

test('The TOGGLE_DETAIL_RIGHT_PANEL will toggles isDetailRightPanelVisible', function(assert) {
  const previous = Immutable.from({
    isDetailRightPanelVisible: true
  });
  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_DETAIL_RIGHT_PANEL });
  assert.equal(result.isDetailRightPanelVisible, false);
});

