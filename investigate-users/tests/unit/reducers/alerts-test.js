import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'investigate-users/reducers/alerts/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-users/actions/types';
import alertOverview from '../../data/presidio/alert_overview';
import existAnomalyTypesAlerts from '../../data/presidio/exist_anomaly_types_alerts';
import alertsList from '../../data/presidio/alerts-list';

const resetState = Immutable.from({
  topAlerts: [],
  alertList: [],
  existAnomalyTypes: null,
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
    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESTORE_DEFAULT
    });

    assert.deepEqual(result, resetState);
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

  test('test update filter for alerts', (assert) => {

    assert.equal(resetState.filter.feedback, null);

    const result = reducer(Immutable.from({ filter: resetState.filter }), {
      type: ACTION_TYPES.UPDATE_FILTER_FOR_ALERTS,
      payload: { feedback: 'none' }
    });

    assert.equal(result.filter.feedback, 'none');
  });

  test('test alerts', (assert) => {

    let result = reducer(Immutable.from({ alertList: [] }), {
      type: ACTION_TYPES.GET_ALERTS,
      payload: { data: alertsList.data, info: alertsList.info, total: 30 }
    });
    assert.equal(result.alertList.length, 3);
    assert.equal(result.totalAlerts, 30);

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
    assert.equal(result.alertList.length, 0);
  });

});