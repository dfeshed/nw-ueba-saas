import Immutable from 'seamless-immutable';
import { test, module } from 'qunit';
import reducer from 'springboard/reducers/springboard/reducer';
import * as ACTION_TYPES from 'springboard/actions/types';
import { LIFECYCLE } from 'redux-pack';
import makePackAction from '../../../helpers/make-pack-action';

module('Unit | Reducers | Springboard', function() {

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, {
      springboards: [],
      fetchStatus: null,
      activeSpringboardId: null,
      isPagerLeftDisabled: false,
      isPagerRightDisabled: false,
      pagerPosition: 0,
      defaultActiveLeads: 0
    });
  });

  test('FETCH_ALL_SPRINGBOARD should set data to state', function(assert) {
    const previous = Immutable.from({
      springboards: [],
      fetchStatus: null
    });

    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.FETCH_ALL_SPRINGBOARD });
    const endState = reducer(previous, startAction);

    assert.equal(endState.fetchStatus, 'wait');
    assert.equal(endState.springboards.length, 0);

    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.FETCH_ALL_SPRINGBOARD,
      payload: { data: { items: [ {} ] } }
    });
    const newEndState = reducer(previous, successAction);

    assert.equal(newEndState.fetchStatus, 'completed');
    assert.equal(newEndState.springboards.length, 1);
  });

  test('SET_ACTIVE_SPRINGBOARD_ID will set the active springboard id to state', function(assert) {
    const previous = Immutable.from({
      activeSpringboardId: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_ACTIVE_SPRINGBOARD_ID, payload: '1' });
    assert.equal(result.activeSpringboardId, 1, 'Setting the coorect id');
  });
  test('SET_DEFAULT_ACTIVE_LEADS will set the active springboard leads count to state', function(assert) {
    const previous = Immutable.from({
      defaultActiveLeads: 0
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_DEFAULT_ACTIVE_LEADS, payload: '2' });
    assert.equal(result.defaultActiveLeads, 2, 'Setting the coorect defaultActiveLeads');
  });
  test('SET_PAGER_POSITION will set the active springboard pager position to state', function(assert) {
    const previous = Immutable.from({
      pagerPosition: 0
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_PAGER_POSITION, payload: '2' });
    assert.equal(result.pagerPosition, 2, 'Setting the coorect pagerPosition');
  });
});