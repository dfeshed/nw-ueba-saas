import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'investigate-users/reducers/user/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-users/actions/types';

module('Unit | Reducers | User Reducer', (hooks) => {
  setupTest(hooks);

  test('test init user', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_USER,
      payload: { userId: 123 }
    });

    assert.equal(result.userId, 123);
  });

  test('test reset user', (assert) => {

    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_USER,
      payload: { userId: 123 }
    });

    assert.equal(result.userId, 123);

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESET_USER
    });

    assert.equal(result.userId, null);
  });

  test('test init user with alert and indicator id', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_USER,
      payload: { userId: 123, alertId: 'alert1', indicatorId: 'ind-1' }
    });

    assert.deepEqual(result, { userId: 123, alertId: 'alert1', indicatorId: 'ind-1' });
  });
});