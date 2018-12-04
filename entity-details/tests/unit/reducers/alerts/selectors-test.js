import { module, test } from 'qunit';
import { selectedAlertId, alertsData, getSelectedAlertData } from 'entity-details/reducers/alerts/selectors';
import userAlerts from '../../../data/presidio/user_alerts';

module('Unit | Selector | Alerts Selector');

const state = {
  alerts: {
    selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
    alerts: userAlerts.data
  }
};

test('test alert state for selected alert id', function(assert) {
  assert.equal(selectedAlertId(state), '0bd963d0-a0ae-4601-8497-b0c363becd1f');
});

test('test alert state for alert data for given entity', function(assert) {
  assert.deepEqual(alertsData(state), userAlerts.data);
});

test('test alert state for selected alert', function(assert) {
  assert.deepEqual(getSelectedAlertData(state), userAlerts.data[0]);
});