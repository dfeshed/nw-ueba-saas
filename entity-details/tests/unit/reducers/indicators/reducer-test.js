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
      payload: 'inc-1'
    });

    assert.equal(result.selectedIndicatorId, 'inc-1');
  });

  test('test reset indicator', (assert) => {

    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_INDICATOR,
      payload: 'inc-1'
    });

    assert.equal(result.selectedIndicatorId, 'inc-1');

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESET_INDICATOR
    });

    assert.equal(result.selectedIndicatorId, null);
  });

  test('test indicator events', (assert) => {
    const { data, total } = indicatorEvents;
    const result = reducer(Immutable.from({
      selectedIndicatorId: null,
      events: [],
      historicalData: null,
      totalEvents: null,
      eventFilter: {
        page: 1,
        size: 100,
        sort_direction: 'DESC'
      }
    }), {
      type: ACTION_TYPES.GET_INDICATOR_EVENTS,
      payload: { data, total }
    });
    assert.deepEqual(result.events, indicatorEvents.data);
    assert.deepEqual(result.eventFilter, {
      page: 2,
      size: 100,
      sort_direction: 'DESC'
    });
  });

  test('test indicator historical data', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_INDICATOR_HISTORICAL_DATA,
      payload: indicatorCount
    });

    assert.deepEqual(result.historicalData, indicatorCount);
  });

  test('test indicator for select alert', (assert) => {
    const { data, total } = indicatorEvents;
    const result = reducer(Immutable.from({
      selectedIndicatorId: 'INC-1',
      events: data,
      totalEvents: total
    }), {
      type: ACTION_TYPES.SELECT_ALERT
    });
    assert.equal(result.selectedIndicatorId, null);
    assert.equal(result.totalEvents, null);
    assert.deepEqual(result.events, []);
  });

  test('test indicator state for errors', (assert) => {

    let result = reducer(Immutable.from({
      indicatorEventError: false,
      indicatorGraphError: false
    }), {
      type: ACTION_TYPES.INITIATE_INDICATOR,
      payload: 'inc-1'
    });
    assert.equal(result.indicatorEventError, false);
    assert.equal(result.indicatorGraphError, false);

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INDICATOR_EVENTS_ERROR
    });

    assert.equal(result.indicatorEventError, true);

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INDICATOR_GRAPH_ERROR
    });

    assert.equal(result.indicatorGraphError, true);

  });

});