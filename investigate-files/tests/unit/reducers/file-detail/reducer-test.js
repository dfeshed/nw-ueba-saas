import { setupTest } from 'ember-qunit';
import { test, module } from 'qunit';
import reducer from 'investigate-files/reducers/file-detail/reducer';
import * as ACTION_TYPES from 'investigate-files/actions/types';
import Immutable from 'seamless-immutable';

module('Unit | Reducers | file-detail', function(hooks) {
  setupTest(hooks);

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result, {
      eventsData: null,
      eventsLoadingStatus: null,
      expandedEventId: null,
      alertsError: null,
      selectedAlert: null
    });
  });

  test('The GET_EVENTS action will reset the loading status', function(assert) {
    const previous = Immutable.from({
      eventsData: null,
      eventsLoadingStatus: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.GET_EVENTS, payload: [{}, {}, {}] });
    assert.equal(result.eventsLoadingStatus, 'loading');
    assert.equal(result.eventsData.length, 3);
  });

  test('The GET_EVENTS_COMPLETED action will reset the loading status', function(assert) {
    const previous = Immutable.from({
      eventsLoadingStatus: 'loading'
    });
    const result = reducer(previous, { type: ACTION_TYPES.GET_EVENTS_COMPLETED });
    assert.equal(result.eventsLoadingStatus, 'completed');
  });

  test('The GET_EVENTS_ERROR action will set the error status', function(assert) {
    const previous = Immutable.from({
      eventsLoadingStatus: 'loading'
    });
    const result = reducer(previous, { type: ACTION_TYPES.GET_EVENTS_ERROR });
    assert.equal(result.eventsLoadingStatus, 'error');
  });

  test('The ACTIVE_RISK_SEVERITY_TAB action will reset the selected alert', function(assert) {
    const previous = Immutable.from({
      selectedAlert: 'some alert'
    });
    const result = reducer(previous, { type: ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB });
    assert.equal(result.selectedAlert, null);
  });


  test('The SET_SELECTED_ALERT action will set the selected alert', function(assert) {
    const previous = Immutable.from({
      alertName: 'Old Alert'
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_ALERT, payload: { alertName: 'New Alert' } });
    assert.equal(result.selectedAlert, 'New Alert');
  });

  test('The EXPANDED_EVENT action will set expanded event id', function(assert) {
    const previous = Immutable.from({
      expandedEventId: 1
    });
    let result = reducer(previous, { type: ACTION_TYPES.EXPANDED_EVENT, id: 2 });
    assert.equal(result.expandedEventId, 2, 'expanded id is 2');
    result = reducer(previous, { type: ACTION_TYPES.EXPANDED_EVENT, id: 1 });
    assert.equal(result.expandedEventId, undefined, 'expanded id is reset');
  });
});
