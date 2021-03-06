import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'entity-details/reducers/alerts/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'entity-details/actions/types';
import userAlerts from '../../../data/presidio/user_alerts';

module('Unit | Reducers | Alerts Reducer', (hooks) => {
  setupTest(hooks);

  test('test init alerts', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_ALERT,
      payload: 'alert-1'
    });

    assert.equal(result.selectedAlertId, 'alert-1');
  });

  test('test get alerts for given entity', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_ALERTS,
      payload: userAlerts
    });

    assert.deepEqual(result.alerts, userAlerts);
  });

  test('test update sort for alerts', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.UPDATE_SORT,
      payload: 'name'
    });

    assert.deepEqual(result.sortBy, 'name');
  });

  test('test select alert for alerts', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.SELECT_ALERT,
      payload: 'Alert-1'
    });

    assert.deepEqual(result.selectedAlertId, 'Alert-1');
  });

  test('test reset alerts', (assert) => {

    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_ALERT,
      payload: 'alert-1'
    });

    assert.deepEqual(result, {
      selectedAlertId: 'alert-1',
      sortBy: 'severity',
      alerts: [],
      errorMessage: null
    });

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESET_ALERT
    });

    assert.deepEqual(result, {
      selectedAlertId: null,
      sortBy: 'severity',
      alerts: [],
      errorMessage: null
    });
  });

  test('test alerts state for error', (assert) => {

    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_ALERT,
      payload: 'alert-1'
    });
    assert.deepEqual(result, {
      selectedAlertId: 'alert-1',
      sortBy: 'severity',
      alerts: [],
      errorMessage: null
    });

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.ALERT_ERROR
    });

    assert.equal(result.errorMessage, 'error');

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.ALERT_ERROR,
      payload: 'noAlertsData'
    });

    assert.equal(result.errorMessage, 'noAlertsData');

  });
});