import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'entity-details/reducers/indicators/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'entity-details/actions/types';
import indicatorEvents from '../../../data/presidio/indicator-events';
import indicatorCount from '../../../data/presidio/indicator-count';

module('Unit | Reducers | Indicators Reducer', (hooks) => {
  setupTest(hooks);

  test('test init indicator', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_INDICATOR,
      payload: { entityId: 123, entityType: 'user', alertId: 'alert-1', indicatorId: 'inc-1' }
    });

    assert.equal(result.indicatorId, 'inc-1');
  });

  test('test reset indicator', (assert) => {

    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_INDICATOR,
      payload: { entityId: 123, entityType: 'user', alertId: 'alert-1', indicatorId: 'inc-1' }
    });

    assert.equal(result.indicatorId, 'inc-1');

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESET_INDICATOR
    });

    assert.equal(result.indicatorId, null);
  });

  test('test indicator events', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_INDICATOR_EVENTS,
      payload: indicatorEvents.data
    });

    assert.deepEqual(result.events, indicatorEvents.data);
  });

  test('test indicator historical', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_INDICATOR_HISTORICAL_DATA,
      payload: indicatorCount
    });

    assert.deepEqual(result.historicalData, indicatorCount);
  });

});