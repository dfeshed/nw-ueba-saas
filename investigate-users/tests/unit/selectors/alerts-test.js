import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import * as Alerts from 'investigate-users/reducers/alerts/selectors';
import alertOverview from '../../data/presidio/alert_overview';
import existAnomalyTypesAlerts from '../../data/presidio/exist_anomaly_types_alerts';
import alertByDayAndSeverity from '../../data/presidio/alert-by-day-and-severity';
import alertsList from '../../data/presidio/alerts-list';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const state = Immutable.from({
  alerts: {
    topAlerts: alertOverview.data,
    topAlertsError: null,
    alertList: alertsList,
    alertListError: null,
    alertsForTimeline: alertByDayAndSeverity,
    alertsForTimelineError: null,
    totalAlerts: 50,
    alertsSeverity: { total_severity_count: { Critical: 50, High: 10, Medium: 30, Low: 12 } },
    existAnomalyTypes: existAnomalyTypesAlerts,
    filter: {
      indicator_types: ['high_number_of_successful_file_permission_change'],
      sort_direction: 'DESC',
      sort_field: 'startDate',
      total_severity_count: true,
      severity: ['high'],
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

  test('test top alerts error', function(assert) {
    assert.equal(Alerts.topAlertsError(state), null);
    const newState = state.setIn(['alerts', 'topAlertsError'], 'error');
    assert.equal(Alerts.topAlertsError(newState), 'error');
  });

  test('test alert state for alerts are present or not', function(assert) {
    assert.equal(Alerts.hasAlerts(state), true);
    const newState = state.setIn(['alerts', 'alertList'], null);
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

  test('test Alerts Grouped Hourly', function(assert) {
    assert.equal(Alerts.getAlertsGroupedHourly(state).undefined.length, 5);
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
});