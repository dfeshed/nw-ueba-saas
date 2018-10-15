import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { patchFetch } from '../../helpers/patch-fetch';
import { Promise } from 'rsvp';
import dataIndex from '../../data/presidio';
import { exportAlerts, getTopTenAlerts, getAlertsForTimeline, resetAlerts, updateFilter, getExistAnomalyTypesForAlert, getAlertsForGivenTimeInterval } from 'investigate-users/actions/alert-details';

export const initialFilterState = Immutable.from({
  sort_direction: 'DESC',
  sort_field: 'startDate',
  total_severity_count: true,
  severity: null,
  feedback: null,
  indicator_types: null,
  alert_start_range: null,
  fromPage: 1,
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
        assert.equal(payload.data.length, 10);
        done();
      }
    };
    const getState = () => {
      return { alerts: { filter: initialFilterState } };
    };
    getAlertsForGivenTimeInterval()(dispatch, getState);
  });
});