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
      activeSpringboardId: null,
      fetchStatus: null
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

});