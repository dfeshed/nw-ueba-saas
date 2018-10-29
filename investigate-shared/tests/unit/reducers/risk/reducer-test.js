import { setupTest } from 'ember-qunit';
import { test, module } from 'qunit';
import reducer from 'investigate-shared/reducers/risk/reducer';
import * as ACTION_TYPES from 'investigate-shared/actions/types';
import Immutable from 'seamless-immutable';
import makePackAction from '../../../helpers/make-pack-action';
import { LIFECYCLE } from 'redux-pack';

module('Unit | Reducers | risk', function(hooks) {
  setupTest(hooks);

  test('should return the initial state', function(assert) {
    const result = reducer(undefined, {});
    assert.deepEqual(result,
      {
        activeRiskSeverityTab: 'critical',
        alertsError: null,
        eventsData: [],
        eventsLoadingStatus: null,
        expandedEventId: null,
        isRiskScoreReset: true,
        isRiskScoringServerOffline: false,
        riskScoreContext: null,
        riskScoreContextError: null,
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

  test('The CLEAR_EVENTS action will set reset events', function(assert) {
    const previous = Immutable.from({
      eventsData: [{}, {}, {}]
    });
    assert.equal(previous.eventsData.length, 3, 'Initial length is 3');
    const result = reducer(previous, { type: ACTION_TYPES.CLEAR_EVENTS });
    assert.equal(result.eventsData.length, 0, 'Events are cleared');
  });

  test('The GET_RESPOND_EVENTS action will set append new events', function(assert) {
    const previous = Immutable.from({
      eventsData: [{ 'agent_id': '123-abc', 'device_type': 'nwendpoint' }],
      eventsLoadingStatus: null
    });
    const startAction = makePackAction(LIFECYCLE.START, { type: ACTION_TYPES.GET_RESPOND_EVENTS });
    const endState = reducer(previous, startAction);

    assert.equal(endState.eventsLoadingStatus, 'loading');
    assert.equal(previous.eventsData.length, 1, 'Initial length is 1');

    const successAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_RESPOND_EVENTS,
      payload: { data: [{ 'agent_id': '123-xyz', 'device_type': 'nwendpoint' }] },
      meta: { indicatorId: '123-456' }
    });
    const newEndState = reducer(previous, successAction);

    assert.equal(newEndState.eventsLoadingStatus, 'completed');
    assert.equal(newEndState.eventsData[1].id, '123-456:0', 'unique id is properly set for each event');
    assert.equal(newEndState.eventsData.length, 2);
  });

  test('The ACTIVE_RISK_SEVERITY_TAB action will reset the selected alert', function(assert) {
    const previous = Immutable.from({
      selectedAlert: 'some alert'
    });
    const result = reducer(previous, { type: ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB, payload: { tabName: 'high' } });
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

  test('RESET_RISK_CONTEXT resets riskScoreContext', function(assert) {
    const previous = Immutable.from({
      riskScoreContext: [{
        hash: 'ccc8538dd62f20999717e2bbab58a18973b938968d699154df9233698a899efa',
        alertCount: {
          critical: 1,
          high: 10,
          medium: 20
        },
        categorizedAlerts: {}
      }]
    });
    const result = reducer(previous, { type: ACTION_TYPES.RESET_RISK_CONTEXT });
    assert.equal(result.riskScoreContext, null, 'riskScoreContext is reset');
  });

  test('The GET_RISK_SCORE_CONTEXT sets the risk score context ', function(assert) {
    // Initial state
    const initialResult = reducer(undefined, {});
    assert.equal(initialResult.riskScoreContext, null, 'original riskScoreContext value');

    const response = [{
      hash: 'ccc8538dd62f20999717e2bbab58a18973b938968d699154df9233698a899efa',
      alertCount: {
        critical: 1,
        high: 10,
        medium: 20
      },
      categorizedAlerts: {}
    }
    ];

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.GET_RISK_SCORE_CONTEXT,
      payload: { data: response }
    });

    const result = reducer(initialResult, newAction);
    assert.deepEqual(result.riskScoreContext, response, 'riskScoreContext value is set');

  });
});
