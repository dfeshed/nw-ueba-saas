import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'investigate-users/reducers/tabs/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-users/actions/types';

module('Unit | Reducers | Tabs Reducer', (hooks) => {
  setupTest(hooks);

  test('test restore default should reset state back', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESTORE_DEFAULT
    });

    assert.deepEqual(result, {
      activeTabName: 'overview'
    });
  });

  test('test update active tab', (assert) => {
    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.UPDATE_ACTIVE_TAB,
      payload: 'users'
    });

    assert.deepEqual(result, {
      activeTabName: 'users'
    });
  });

  test('test update active tab as alert tab', (assert) => {
    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.UPDATE_ACTIVE_TAB,
      payload: 'alerts'
    });

    assert.deepEqual(result, {
      activeTabName: 'alerts'
    });
  });
});
