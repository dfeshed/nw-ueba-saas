import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'investigate-hosts/reducers/details/reducer';
import * as ACTION_TYPES from 'investigate-hosts/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | details');
const initialState = {
  agentId: null,
  scanTime: null,
  animation: 'default',

  snapShots: null,
  isOverviewPanelVisible: true,
  showNonEmptyProperty: false
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
    scanTime: null
  });
  const result = reducer(previous, { type: ACTION_TYPES.INITIALIZE_DATA, payload: { agentId: 111 } });

  assert.equal(result.agentId, 111, 'Expecting agentIe equals to 111');
});

test('The TOGGLE_SHOW_PROPERTY_WITH_VALUES will toggles isPropPanelshowNonEmptyProperty', function(assert) {
  const previous = Immutable.from({
    howNonEmptyProperty: false
  });
  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_SHOW_PROPERTY_WITH_VALUES });
  assert.equal(result.showNonEmptyProperty, true);
});

test('The TOGGLE_OVERVIEW_PANEL will toggles isOverviewPanelVisible', function(assert) {
  const previous = Immutable.from({
    isOverviewPanelVisible: true
  });
  const result = reducer(previous, { type: ACTION_TYPES.TOGGLE_OVERVIEW_PANEL });
  assert.equal(result.isOverviewPanelVisible, false);
});

test('The SET_ANIMATION will sets the animation to the state', function(assert) {
  const previous = Immutable.from({
    animation: 'default'
  });
  const result = reducer(previous, { type: ACTION_TYPES.SET_ANIMATION, payload: 'toDown' });
  assert.equal(result.animation, 'toDown');
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
    snapShots: null
  });

  const newAction = makePackAction(LIFECYCLE.SUCCESS, {
    type: ACTION_TYPES.FETCH_ALL_SNAP_SHOTS,
    payload: { data: [ 1231231, 1231231] }
  });
  const newEndState = reducer(previous, newAction);

  assert.equal(newEndState.snapShots.length, 2);
});

