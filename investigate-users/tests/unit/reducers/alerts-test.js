import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'investigate-users/reducers/alerts/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-users/actions/types';
import alertOverview from '../../data/presidio/alert_overview';
import existAnomalyTypesAlerts from '../../data/presidio/exist_anomaly_types_alerts';
import alertByDayAndSeverity from '../../data/presidio/alert-by-day-and-severity';
import alertsList from '../../data/presidio/alerts-list';

const resetState = Immutable.from({
  topAlerts: [],
  topAlertsError: null,
  alertList: {},
  alertListError: null,
  existAnomalyTypes: null,
  alertsForTimeline: null,
  alertsForTimelineError: null,
  currentAlertsCount: 0,
  topAlertsEntity: 'all',
  topAlertsTimeFrame: {
    name: 'IN_LAST_THREE_MONTH',
    unit: 'Months',
    value: 3
  },
  relativeDateFilter: {
    name: 'alertTimeRange',
    operator: 'LESS_THAN',
    unit: 'Months',
    value: [ 3 ]
  },
  alertsSeverity: {
    total_severity_count: {
      Critical: null,
      High: null,
      Low: null,
      Medium: null
    }
  },
  filter: {
    sort_direction: 'DESC',
    sort_field: 'startDate',
    entityType: 'all',
    showCustomDate: false,
    total_severity_count: true,
    severity: null,
    feedback: null,
    indicator_types: null,
    alert_start_range: null,
    fromPage: 1,
    size: 25
  },
  totalAlerts: null
});

module('Unit | Reducers | Alerts Reducer', (hooks) => {
  setupTest(hooks);

  test('test restore default should reset state back', (assert) => {
    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESTORE_DEFAULT
    });
    result = result.without('filter');
    const newResetState = resetState.without('filter');
    assert.deepEqual(result, newResetState);
  });

  test('test top 10 alerts', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_TOP_ALERTS,
      payload: alertOverview
    });

    assert.equal(result.topAlerts[0].data.length, 10);
  });

  test('test exists anomaly types', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_EXIST_ANOMALY_TYPES_ALERT,
      payload: existAnomalyTypesAlerts
    });

    assert.equal(result.existAnomalyTypes.abnormal_active_directory_day_time_operation, 34);
  });

  test('test top alerts error', (assert) => {

    const result = reducer(Immutable.from(resetState), {
      type: ACTION_TYPES.TOP_ALERTS_ERROR,
      payload: 'error'
    });

    assert.equal(result.topAlertsError, 'error');
  });

  test('test alerts error', (assert) => {

    const result = reducer(Immutable.from(resetState), {
      type: ACTION_TYPES.ALERT_LIST_ERROR,
      payload: 'error'
    });

    assert.equal(result.alertListError, 'error');
  });

  test('test alerts  time line error', (assert) => {

    const result = reducer(Immutable.from(resetState), {
      type: ACTION_TYPES.ALERTS_FOR_TIMELINE_ERROR,
      payload: 'error'
    });

    assert.equal(result.alertsForTimelineError, 'error');
  });

  test('test alerts For Timeline', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_ALERTS_FOR_TIMELINE,
      payload: alertByDayAndSeverity
    });

    assert.equal(result.alertsForTimeline.length, 5);
  });

  test('test update filter for alerts and reset the same', (assert) => {

    assert.equal(resetState.filter.feedback, null);

    let result = reducer(Immutable.from({ filter: resetState.filter }), {
      type: ACTION_TYPES.UPDATE_FILTER_FOR_ALERTS,
      payload: { filter: { feedback: 'none', showCustomDate: true }, relativeDateFilter: {
        name: 'alertTimeRange',
        operator: 'LESS_THAN',
        unit: 'Months',
        value: [ 1 ]
      } }
    });

    assert.equal(result.filter.feedback, 'none');
    assert.equal(result.filter.entityType, 'all');
    assert.equal(result.filter.showCustomDate, true);
    assert.deepEqual(result.relativeDateFilter, {
      name: 'alertTimeRange',
      operator: 'LESS_THAN',
      unit: 'Months',
      value: [ 1 ]
    });

    result = reducer(Immutable.from({ filter: resetState.filter }), {
      type: ACTION_TYPES.UPDATE_FILTER_FOR_ALERTS,
      payload: { filter: null, relativeDateFilter: null }
    });
    assert.equal(result.filter.fromPage, resetState.filter.fromPage);
    assert.deepEqual(result.relativeDateFilter, {
      name: 'alertTimeRange',
      operator: 'LESS_THAN',
      unit: 'Months',
      value: [ 3 ]
    });

  });

  test('test alerts', (assert) => {

    let result = reducer(Immutable.from({ alertList: [] }), {
      type: ACTION_TYPES.GET_ALERTS,
      payload: { data: { 'jul 12 2019': [[{ name: 'CriticalALert' }], [{ name: 'HighALert' }], [], [{ name: 'LowAlert' }]] }, info: alertsList.info, total: 30, currentCount: 10 }
    });
    assert.equal(result.alertList['jul 12 2019'].length, 4);
    assert.equal(result.totalAlerts, 30);
    assert.equal(result.currentAlertsCount, 10);

    assert.deepEqual(result.alertsSeverity, {
      total_severity_count: {
        Critical: 41,
        High: 58,
        Low: 236,
        Medium: 89
      }
    });

    result = reducer(result, {
      type: ACTION_TYPES.RESET_ALERTS
    });
    assert.deepEqual(result.alertList, {});
    assert.equal(result.alertListError, null);
    assert.equal(result.alertListError, null);
    assert.equal(result.topAlertsError, null);
    assert.equal(result.currentAlertsCount, 0);
    assert.equal(result.alertsForTimelineError, null);
  });

  test('test alerts For TOP_ALERT_FILTER', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.TOP_ALERT_FILTER,
      payload: { entityType: 'ja3', timeRange: {
        name: 'LAST_THREE_MONTH',
        unit: 'Months',
        value: 3
      } }
    });

    assert.equal(result.topAlertsEntity, 'ja3');
    assert.deepEqual(result.topAlertsTimeFrame, {
      name: 'LAST_THREE_MONTH',
      unit: 'Months',
      value: 3
    });
  });

});