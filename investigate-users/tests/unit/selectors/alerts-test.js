import { module, test } from 'qunit';
import * as Alerts from 'investigate-users/reducers/alerts/selectors';
import alertOverview from '../../data/presidio/alert_overview';
import existAnomalyTypesAlerts from '../../data/presidio/exist_anomaly_types_alerts';
import alertByDayAndSeverity from '../../data/presidio/alert-by-day-and-severity';
import alertsList from '../../data/presidio/alerts-list';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | Alerts Selectors');

const state = Immutable.from({
  alerts: {
    topAlerts: alertOverview,
    alertList: alertsList,
    alertsForTimeline: alertByDayAndSeverity,
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
      fromPage: 1,
      size: 25
    }
  }
});

test('test TopAlerts', function(assert) {
  assert.equal(Alerts.getTopAlerts(state).data.length, 10);
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

test('test getAlertsForTimeline', function(assert) {
  assert.equal(Alerts.getAlertsForTimeline(state).length, 5);
  assert.deepEqual(Alerts.getAlertsForTimeline(state)[0], { Critical: 16, High: 0, Medium: 0, Low: 0, day: 1533686400000, total: 16 });
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
    count: '(11 Users)',
    name: 'high_number_of_successful_file_permission_change'
  });
});