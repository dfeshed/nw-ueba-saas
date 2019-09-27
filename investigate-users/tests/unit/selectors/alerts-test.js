import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import * as Alerts from 'investigate-users/reducers/alerts/selectors';
import alertOverview from '../../data/presidio/alert_overview';
import existAnomalyTypesAlerts from '../../data/presidio/exist_anomaly_types_alerts';
import alertByDayAndSeverity from '../../data/presidio/alert-by-day-and-severity';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const state = Immutable.from({
  alerts: {
    topAlerts: alertOverview.data,
    topAlertsError: null,
    alertList: { 'jul 12 2019': [[{ name: 'CriticalALert' }], [{ name: 'HighALert' }], [], [{ name: 'LowAlert' }]] },
    alertListError: null,
    alertsForTimeline: alertByDayAndSeverity,
    alertsForTimelineError: null,
    currentAlertsCount: 10,
    totalAlerts: 50,
    topAlertsEntity: 'all',
    topAlertsTimeFrame: {
      name: 'LAST_THREE_MONTH',
      unit: 'Months',
      value: 3
    },
    alertsSeverity: { total_severity_count: { Critical: 50, High: 10, Medium: 30, Low: 12 } },
    relativeDateFilter: {
      name: 'alertTimeRange',
      operator: 'LESS_THAN',
      unit: 'Months',
      value: [ 3 ]
    },
    existAnomalyTypes: existAnomalyTypesAlerts,
    filter: {
      indicator_types: ['high_number_of_successful_file_permission_change'],
      sort_direction: 'DESC',
      sort_field: 'startDate',
      total_severity_count: true,
      severity: ['high'],
      entityType: null,
      feedback: 'none',
      alert_start_range: null,
      showCustomDate: false,
      fromPage: 1,
      size: 25
    }
  }
});

module('Unit | Selectors | Alerts Selectors', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('test TopAlerts', function(assert) {
    assert.equal(Alerts.getTopAlerts(state).length, 10);
  });

  test('test All Alerts should be less then current alerts', function(assert) {
    assert.equal(Alerts.allAlertsReceived(state), false);
  });

  test('test filter state', function(assert) {
    assert.equal(Alerts.getFilter(state).size, 25);
    assert.equal(Alerts.getFilter(state).sort_field, 'startDate');
  });


  test('test Selected FeedBack should return correct feedback', function(assert) {
    assert.equal(Alerts.getSelectedFeedBack(state), 'none');
  });

  test('test Selected Severity', function(assert) {
    assert.equal(Alerts.getSelectedSeverity(state)[0], 'high');
  });

  test('test for currentAlertsCount', function(assert) {
    assert.equal(Alerts.currentAlertsCount(state), 10);
  });

  test('test top alerts error', function(assert) {
    assert.equal(Alerts.topAlertsError(state), null);
    const newState = state.setIn(['alerts', 'topAlertsError'], 'error');
    assert.equal(Alerts.topAlertsError(newState), 'error');
  });

  test('test alert state for alerts are present or not', function(assert) {
    assert.equal(Alerts.hasAlerts(state), true);
    const newState = state.setIn(['alerts', 'alertList'], {});
    assert.equal(Alerts.hasAlerts(newState), false);
  });

  test('test alert state for top 10 alerts are present or not', function(assert) {
    assert.equal(Alerts.hasTopAlerts(state), true);
    const newState = state.setIn(['alerts', 'topAlerts'], null);
    assert.equal(Alerts.hasTopAlerts(newState), false);
  });

  test('test alerts error', function(assert) {
    assert.equal(Alerts.alertListError(state), null);
    const newState = state.setIn(['alerts', 'alertListError'], 'error');
    assert.equal(Alerts.alertListError(newState), 'error');
  });

  test('test alerts timeline error', function(assert) {
    assert.equal(Alerts.alertsForTimelineError(state), null);
    const newState = state.setIn(['alerts', 'alertsForTimelineError'], 'error');
    assert.equal(Alerts.alertsForTimelineError(newState), 'error');
  });

  test('test getAlertsForTimeline', function(assert) {
    assert.equal(Alerts.getAlertsForTimeline(state).length, 5);
    assert.deepEqual(Alerts.getAlertsForTimeline(state)[0], { Critical: 16, High: 0, Medium: 0, Low: 0, day: 1533686400000, total: 16 });
  });

  test('test dateTimeFilterOptionsForAlerts', function(assert) {
    assert.deepEqual(Alerts.dateTimeFilterOptionsForAlerts(state).filterValue, {
      name: 'alertTimeRange',
      operator: 'LESS_THAN',
      unit: 'Months',
      value: [3]
    });
  });

  test('test dateTimeFilterOptionsForAlerts for custom range', function(assert) {
    let newState = state.setIn(['alerts', 'filter', 'showCustomDate'], true);
    newState = newState.setIn(['alerts', 'filter', 'alert_start_range'], '1534032000000,1534118400');
    const { filterValue } = Alerts.dateTimeFilterOptionsForAlerts(newState);
    assert.equal(filterValue.value[0], 1534032000000);
    assert.equal(filterValue.value[1], 1534118400);
  });

  test('test getAlertsForTimeline for null', function(assert) {
    const newState = Immutable.from({
      alerts: {
        alertsForTimeline: null
      }
    });
    assert.equal(Alerts.getAlertsForTimeline(newState), null);
  });

  test('test Alerts Severity', function(assert) {
    assert.deepEqual(Alerts.getAlertsSeverity(state), { Critical: 50, High: 10, Medium: 30, Low: 12 });
  });

  test('test Alerts Severity for null', function(assert) {
    const newState = Immutable.from({
      alerts: {
        alertsSeverity: null
      }
    });
    assert.equal(Alerts.getAlertsSeverity(newState), null);
  });

  test('test Alerts Grouped Daily', function(assert) {
    assert.deepEqual(Alerts.alertsGroupedDaily(state), { 'jul 12 2019': [[{ name: 'CriticalALert' }], [{ name: 'HighALert' }], [], [{ name: 'LowAlert' }]] });
  });

  test('test Alerts Selected Entity', function(assert) {
    assert.deepEqual(Alerts.selectedEntities(state), undefined);
    const newState = Immutable.from({
      alerts: {
        filter: {
          entityType: 'ja3'
        }
      }
    });
    assert.deepEqual(Alerts.selectedEntities(newState), 'ja3');
  });

  test('test Exist Anomaly Types', function(assert) {
    assert.equal(Alerts.getExistAnomalyTypes(state).length, 26);
  });

  test('test getSelectedAnomalyTypes', function(assert) {
    assert.deepEqual(Alerts.getSelectedAnomalyTypes(state)[0], {
      id: 'high_number_of_successful_file_permission_change',
      displayLabel: 'Multiple File Access Permission Changes (11 Users)'
    });
  });

  test('test topAlertsEntity', function(assert) {
    assert.equal(Alerts.topAlertsEntity(state), 'all');
  });

  test('test topAlertsTimeFrame', function(assert) {
    assert.deepEqual(Alerts.topAlertsTimeFrame(state), {
      name: 'LAST_THREE_MONTH',
      unit: 'Months',
      value: 3
    });
  });
});