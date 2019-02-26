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
        currentEntityId: null,
        alertsError: null,
        eventsData: [],
        eventsLoadingStatus: null,
        expandedEventId: null,
        isRiskScoreReset: true,
        isRespondServerOffline: false,
        riskScoreContext: null,
        riskScoreContextError: null,
        eventContext: null,
        eventContextError: null,
        selectedAlert: null,
        alertsLoadingStatus: null
      });
  });

  test('The GET_ESA_EVENTS action will will append new events', function(assert) {
    const previous = Immutable.from({
      eventsData: [{ 'agent_id': '123-abc', 'device_type': 'nwendpoint' }]
    });
    assert.equal(previous.eventsData.length, 1, 'Initial length is 1');
    const newEndState = reducer(previous, {
      type: ACTION_TYPES.GET_ESA_EVENTS,
      payload: { indicatorId: '234-xyz', events: [
        {
          sessionId: 102921,
          time: '2018-12-07T04:23:43.000+0000',
          metas: [
            ['time', '2018-12-07T05:19:22.000+0000']
          ]
        }]
      }
    });
    assert.equal(newEndState.eventsData[1].id, '234-xyz:0', 'unique id is properly set for each event');
    assert.equal(newEndState.eventsData.length, 2);
  });

  test('The GET_EVENTS_COMPLETED action will reset the loading status', function(assert) {
    const previous = Immutable.from({
      eventsLoadingStatus: 'loading'
    });
    const result = reducer(previous, { type: ACTION_TYPES.GET_EVENTS_COMPLETED });
    assert.equal(result.eventsLoadingStatus, 'completed');
  });

  test('The GET_EVENTS_INITIALIZED action will set the loading status to loading', function(assert) {
    const previous = Immutable.from({
      eventsLoadingStatus: null
    });
    const result = reducer(previous, { type: ACTION_TYPES.GET_EVENTS_INITIALIZED });
    assert.equal(result.eventsLoadingStatus, 'loading');
  });

  test('The CLEAR_EVENTS action will set reset events', function(assert) {
    const previous = Immutable.from({
      eventsData: [{}, {}, {}],
      eventsLoadingStatus: 'loading'
    });
    assert.equal(previous.eventsData.length, 3, 'Initial length is 3');
    const result = reducer(previous, { type: ACTION_TYPES.CLEAR_EVENTS });
    assert.equal(result.eventsData.length, 0, 'Events are cleared');
    assert.equal(result.eventsLoadingStatus, null, 'loading status is reset');
  });

  test('The GET_RESPOND_EVENTS action will set append new events', function(assert) {
    const previous = Immutable.from({
      eventsData: [{ 'agent_id': '123-abc', 'device_type': 'nwendpoint' }]
    });
    assert.equal(previous.eventsData.length, 1, 'Initial length is 1');

    const newEndState = reducer(previous, {
      type: ACTION_TYPES.GET_RESPOND_EVENTS,
      payload: { events: [{ id: '234-xyz', alert: { events: [{ 'agent_id': '234-xyz', 'device_type': 'nwendpoint', category: 'Process Event' }] } },
        { id: '235-xyz', alert: { events: [{ 'agent_id': '235-xyz', 'device_type': 'nwendpoint', category: 'File Event' }] } }] }
    });

    assert.equal(newEndState.eventsData[1].id, '234-xyz:0', 'unique id is properly set for second event');
    assert.equal(newEndState.eventsData[2].id, '235-xyz:0', 'unique id is properly set for third event');
    assert.equal(newEndState.eventsData.length, 3);
  });

  test('The ACTIVE_RISK_SEVERITY_TAB action will reset the selected alert', function(assert) {
    const previous = Immutable.from({
      selectedAlert: 'some alert',
      expandedEventId: '123'
    });
    const result = reducer(previous, { type: ACTION_TYPES.ACTIVE_RISK_SEVERITY_TAB, payload: { tabName: 'high' } });
    assert.equal(result.selectedAlert, null);
    assert.equal(result.expandedEventId, null);
  });


  test('The SET_SELECTED_ALERT action will set the selected alert', function(assert) {
    const previous = Immutable.from({
      alertName: 'Old Alert',
      expandedEventId: '123'
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_SELECTED_ALERT, payload: { alertName: 'New Alert' } });
    assert.equal(result.selectedAlert, 'New Alert');
    assert.equal(result.expandedEventId, null);
  });

  test('The SET_CURRENT_ENTITY_ID action will set the selected alert', function(assert) {
    const previous = Immutable.from({
      currentEntityId: '123'
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_CURRENT_ENTITY_ID, payload: { id: '456' } });
    assert.equal(result.currentEntityId, '456');
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
      }],
      riskScoreContextError: 'Context fetch Error',
      activeRiskSeverityTab: 'high',
      eventsLoadingStatus: 'completed',
      selectedAlert: 'Random Alert',
      eventsData: [{}, {}],
      expandedEventId: '1',
      alertsError: 'Alerts error',
      alertsLoadingStatus: 'completed',
      currentEntityId: '123',
      eventContext: [{}, {}],
      eventContextError: 'Error'
    });
    const result = reducer(previous, { type: ACTION_TYPES.RESET_RISK_CONTEXT });
    assert.equal(result.riskScoreContext, null, 'riskScoreContext is reset');
    assert.equal(result.riskScoreContextError, null, 'riskScoreContextError is reset');
    assert.equal(result.activeRiskSeverityTab, 'critical', 'active tab is reset to critical');
    assert.equal(result.eventsLoadingStatus, null, 'eventsLoadingStatus is reset');
    assert.equal(result.selectedAlert, null, 'selectedAlert is reset');
    assert.equal(result.eventsData.length, 0, 'Events data is reset');
    assert.equal(result.expandedEventId, null, 'expandedEventId is reset');
    assert.equal(result.alertsError, null, 'alertsError is reset');
    assert.equal(result.alertsLoadingStatus, null, 'alertsLoadingStatus is reset');
    assert.equal(result.currentEntityId, null, 'currentEntityId is reset');
    assert.equal(result.eventContext, null, 'eventContext is reset');
    assert.equal(result.eventContextError, null, 'eventContextError is reset');
  });

  test('The GET_RISK_SCORE_CONTEXT sets the risk score context ', function(assert) {
    // Initial state
    const initialResult = reducer(undefined, {});
    assert.equal(initialResult.riskScoreContext, null, 'original riskScoreContext value');
    assert.equal(initialResult.alertsLoadingStatus, null, 'original alertsLoadingStatus value');

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
    assert.deepEqual(result.alertsLoadingStatus, 'completed', 'alertsLoadingStatus value is set');

  });

  test('The GET_RISK_SCORE_CONTEXT sets the risk score context error on failure', function(assert) {
    // Initial state
    const initialResult = reducer(undefined, {});
    assert.equal(initialResult.riskScoreContextError, null, 'original riskScoreContextError value');
    assert.equal(initialResult.alertsLoadingStatus, null, 'original alertsLoadingStatus value');

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

    const newAction = makePackAction(LIFECYCLE.FAILURE, {
      type: ACTION_TYPES.GET_RISK_SCORE_CONTEXT,
      payload: { meta: response }
    });

    const result = reducer(initialResult, newAction);
    assert.deepEqual(result.riskScoreContextError, response, 'riskScoreContext value is set');
    assert.deepEqual(result.alertsLoadingStatus, null, 'alertsLoadingStatus value is set');

  });

  test('The GET_RISK_SCORE_CONTEXT sets the alertsLoadingStatus on start ', function(assert) {
    // Initial state
    const initialResult = reducer(undefined, {});
    assert.equal(initialResult.riskScoreContext, null, 'original riskScoreContext value');
    assert.equal(initialResult.alertsLoadingStatus, null, 'original alertsLoadingStatus value');

    const newAction = makePackAction(LIFECYCLE.START, {
      type: ACTION_TYPES.GET_RISK_SCORE_CONTEXT,
      payload: {}
    });

    const result = reducer(initialResult, newAction);
    assert.deepEqual(result.alertsLoadingStatus, 'loading', 'alertsLoadingStatus value is set to loading');
  });

  test('the RESET_RISK_SCORE sets the isRiskScoreReset on success', function(assert) {
    const previous = Immutable.from({
      isRiskScoreReset: false
    });

    const newAction = makePackAction(LIFECYCLE.SUCCESS, {
      type: ACTION_TYPES.RESET_RISK_SCORE,
      payload: {}
    });
    const result = reducer(previous, newAction);
    assert.deepEqual(result.isRiskScoreReset, true, 'isRiskScoreReset is set');
  });

  test('the RESET_RISK_SCORE sets the isRiskScoreReset on failure', function(assert) {
    const previous = Immutable.from({
      isRiskScoreReset: true
    });

    const newAction = makePackAction(LIFECYCLE.FAILURE, {
      type: ACTION_TYPES.RESET_RISK_SCORE,
      payload: {}
    });
    const result = reducer(previous, newAction);
    assert.deepEqual(result.isRiskScoreReset, false, 'isRiskScoreReset is set');
  });

  test('The GET_RESPOND_SERVER_STATUS sets isRespondServerOffline', function(assert) {
    const previous = Immutable.from({
      isRespondServerOffline: false
    });
    const result = reducer(previous, { type: ACTION_TYPES.GET_RESPOND_SERVER_STATUS, payload: true });
    assert.equal(result.isRespondServerOffline, true, 'isRespondServerOffline is set to true');
  });

  test('SET_RISK_SCORE_DETAIL_CONTEXT sets eventContext', function(assert) {
    const testPayload = {
      data: {
        distinctAlertCount: {},
        categorizedAlerts: {
          Critical: {
            Blacklisted: {
              alertCount: 3,
              eventContexts: [{
                id: '5c74c5b9a08cc476828be5ad'
              }]
            }
          }
        }
      }
    };
    const previous = Immutable.from({
      eventContext: [{}, {}],
      activeRiskSeverityTab: 'Critical',
      selectedAlert: 'Blacklisted'
    });
    const result = reducer(previous, { type: ACTION_TYPES.SET_RISK_SCORE_DETAIL_CONTEXT, payload: testPayload });
    assert.equal(result.eventContext.length, 1, 'eventContext is set');
  });
});
