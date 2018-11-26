import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'entity-details/reducers/indicators/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'entity-details/actions/types';

module('Unit | Reducers | Indicators Reducer', (hooks) => {
  setupTest(hooks);

  test('test init indicator', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_ENTITY,
      payload: { entityId: 123, entityType: 'user', alertId: 'alert-1', indicatorId: 'inc-1' }
    });

    assert.equal(result.indicatorId, 'inc-1');
  });

  test('test reset indicator', (assert) => {

    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_ENTITY,
      payload: { entityId: 123, entityType: 'user', alertId: 'alert-1', indicatorId: 'inc-1' }
    });

    assert.equal(result.indicatorId, 'inc-1');

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESET_ENTITY
    });

    assert.equal(result.indicatorId, null);
  });

});