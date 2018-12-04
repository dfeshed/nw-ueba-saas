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
      payload: { entityId: 123, entityType: 'user', alertId: 'alert-1' }
    });

    assert.equal(result.selectedAlertId, 'alert-1');
  });

  test('test reset alerts', (assert) => {

    let result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.INITIATE_ALERT,
      payload: { entityId: 123, entityType: 'user', alertId: 'alert-1' }
    });

    assert.equal(result.selectedAlertId, 'alert-1');

    result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESET_ALERT
    });


    assert.equal(result.alertId, null);
  });

  test('test get alerts for entity', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_ALERTS,
      payload: userAlerts
    });

    assert.deepEqual(result.alerts, userAlerts);
  });

});