import { module, test, skip } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { patchFetch } from '../../helpers/patch-fetch';
import { Promise } from 'rsvp';
import dataIndex from '../../data/presidio';
import moment from 'moment';
import { later } from '@ember/runloop';
import { updateDateRangeFilter, exportAlerts, getTopTenAlerts, getAlertsForTimeline, resetAlerts, updateFilter, getExistAnomalyTypesForAlert, getAlertsForGivenTimeInterval } from 'investigate-users/actions/alert-details';

export const initialFilterState = Immutable.from({
  fromPage: 1,
  sort_direction: 'DESC',
  sort_field: 'startDate',
  total_severity_count: true,
  severity: null,
  feedback: null,
  indicator_types: null,
  alert_start_range: `${moment().subtract('months', 3).unix() * 1000},${moment().unix() * 1000}`,
  size: 25
});

module('Unit | Actions | Alert Details', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    patchFetch((url) => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return dataIndex(url);
          }
        });
      });
    });
  });

  test('it can getTopTenAlerts', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_TOP_ALERTS');
      assert.equal(payload.length, 10);
      done();
    };
    getTopTenAlerts()(dispatch);
  });

  test('it can resetAlerts', (assert) => {
    assert.expect(1);
    const dispatch = ({ type }) => {
      assert.equal(type, 'INVESTIGATE_USER::RESET_ALERTS');
    };
    dispatch(resetAlerts());
  });

  test('it can updateFilter', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const actions = ['INVESTIGATE_USER::GET_ALERTS', 'INVESTIGATE_USER::UPDATE_FILTER_FOR_ALERTS', 'INVESTIGATE_USER::RESET_ALERTS'];
    const dispatch = ({ type, payload }) => {
      if (type && payload) {
        assert.ok(actions.includes(type));
        assert.equal(payload.sort_field, 'startDate');
        done();
      }
    };
    updateFilter(initialFilterState)(dispatch);
  });

  test('it can updateFilter without updating alerts', (assert) => {
    assert.expect(4);
    const actions = ['INVESTIGATE_USER::UPDATE_FILTER_FOR_ALERTS', 'INVESTIGATE_USER::RESET_ALERTS'];
    const dispatch = ({ type, payload }) => {
      if (type && payload) {
        assert.ok(actions.includes(type));
        assert.equal(payload.sort_field, 'startDate');
      }
      assert.notOk('INVESTIGATE_USER::GET_ALERTS' === type);
    };
    updateFilter(initialFilterState, true)(dispatch);
  });

  test('it can getExistAnomalyTypesForAlert', (assert) => {
    assert.expect(2);
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_EXIST_ANOMALY_TYPES_ALERT');
      assert.equal(payload.abnormal_active_directory_day_time_operation, 34);
    };
    getExistAnomalyTypesForAlert()(dispatch);
  });

  test('it can getAlertsForTimeline', (assert) => {
    assert.expect(2);
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_ALERTS_FOR_TIMELINE');
      assert.equal(payload.length, 5);
    };
    getAlertsForTimeline()(dispatch);
  });

  test('it can exportAlerts', (assert) => {
    assert.expect(1);
    window.URL.createObjectURL = () => {
      assert.ok(true, 'This function supposed to be called for altert export');
    };
    const getState = () => {
      return { alerts: { filter: initialFilterState } };
    };
    exportAlerts(initialFilterState)(null, getState);
  });

  test('it can getAlertsForGivenTimeInterval', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      if (type === 'INVESTIGATE_USER::GET_ALERTS') {
        assert.equal(type, 'INVESTIGATE_USER::GET_ALERTS');
        assert.equal(payload.data.length, 3);
        done();
      }
    };
    const getState = () => {
      return { alerts: { filter: initialFilterState } };
    };
    getAlertsForGivenTimeInterval()(dispatch, getState);
  });

  skip('it can updateDateRangeFilter for relative date', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const filterValue = {
      name: 'alertTimeRange',
      operator: 'GREATER_THAN',
      value: [3],
      unit: 'Months'
    };
    const dispatch = (fn) => {
      fn(({ type, payload }) => {
        if (type && payload) {
          const alertDateRange = payload.alert_start_range.split(',');
          assert.equal('INVESTIGATE_USER::UPDATE_FILTER_FOR_ALERTS', type);
          assert.equal(new Date(alertDateRange[1] - alertDateRange[0]).getMonth(), 3);
          done();
        }
      });
    };
    const getState = () => {
      return { alerts: { filter: initialFilterState } };
    };
    updateDateRangeFilter(filterValue)(dispatch, getState);
  });

  test('it can updateDateRangeFilter for wrong custom date', (assert) => {
    const done = assert.async();
    assert.expect(0);
    const filterValue = {
      name: 'alertTimeRange',
      operator: 'BETWEEN',
      value: [null, 1540381527100],
      unit: 'Months'
    };
    const dispatch = (fn) => {
      fn(() => {
        assert.notOk(true, 'This should not execute');
      });
    };
    const getState = () => {
      return { alerts: { filter: initialFilterState } };
    };
    updateDateRangeFilter(filterValue)(dispatch, getState);
    later(() => {
      done();
    }, 500);
  });

  test('it can updateDateRangeFilter for invalid custom date', (assert) => {
    const done = assert.async();
    assert.expect(0);
    const filterValue = {
      name: 'alertTimeRange',
      operator: 'BETWEEN',
      value: [1540381627100, 1540381527100],
      unit: 'Months'
    };
    const dispatch = (fn) => {
      fn(() => {
        assert.notOk(true, 'This should not execute');
      });
    };
    const getState = () => {
      return { alerts: { filter: initialFilterState } };
    };
    updateDateRangeFilter(filterValue)(dispatch, getState);
    later(() => {
      done();
    }, 500);
  });

  test('it can updateDateRangeFilter for valid custom date', (assert) => {
    const done = assert.async();
    assert.expect(2);
    const filterValue = {
      name: 'alertTimeRange',
      operator: 'BETWEEN',
      value: [1540381627100, 1540481527100],
      unit: 'Months'
    };
    const dispatch = (fn) => {
      fn(({ type, payload }) => {
        if (type && payload) {
          assert.equal('INVESTIGATE_USER::UPDATE_FILTER_FOR_ALERTS', type);
          assert.equal(payload.alert_start_range, '1540381627100,1540481527100');
          done();
        }
      });
    };
    const getState = () => {
      return { alerts: { filter: initialFilterState } };
    };
    updateDateRangeFilter(filterValue)(dispatch, getState);
  });
});