import { module, test } from 'qunit';
import { selectedAlertId, sortBy, sortedAlertsData, getSelectedAlertData, alertSources, userScoreContribution, hasAlerts } from 'entity-details/reducers/alerts/selectors';
import userAlerts from '../../../data/presidio/user_alerts';

module('Unit | Selector | Alerts Selector');

const state = {
  alerts: {
    selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
    sortBy: 'severity',
    alerts: userAlerts.data
  }
};

test('test alert state for selected alert id', function(assert) {
  assert.equal(selectedAlertId(state), '0bd963d0-a0ae-4601-8497-b0c363becd1f');
});

test('test alert state for alert data for given entity', function(assert) {
  assert.deepEqual(sortedAlertsData(state), userAlerts.data);
});

test('test alert state for alert sort direction for given entity', function(assert) {
  assert.deepEqual(sortBy(state), 'severity');
});

test('test alert state for alertSources ', function(assert) {
  assert.equal(alertSources(state), 'FILE, ACTIVE_DIRECTORY');
});

test('test alert state for selected alert', function(assert) {
  assert.deepEqual(getSelectedAlertData(state), userAlerts.data[0]);
});

test('test alert state for userScoreContribution', function(assert) {
  assert.equal(userScoreContribution(state), 20);
  assert.equal(hasAlerts(state), true);
});
test('test alert state for userScoreContribution if alert details are not there', function(assert) {
  const newstate = {
    alerts: {
      selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
      sortBy: 'severity',
      alerts: null
    }
  };
  assert.equal(userScoreContribution(newstate), 0);
});

test('test alert state till data is not empty', function(assert) {
  const newstate = {
    alerts: {
      selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
      sortBy: 'severity',
      alerts: []
    }
  };
  assert.equal(hasAlerts(newstate), false);
});

test('test alert state till data is not there', function(assert) {
  const newstate = {
    alerts: {
      selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
      sortBy: 'severity',
      alerts: null
    }
  };
  assert.equal(hasAlerts(newstate), false);
});

test('test alert state for alertSources if alert details are not there', function(assert) {
  const newstate = {
    alerts: {
      selectedAlertId: '0bd963d0-a0ae-4601-8497-b0c363becd1f',
      sortBy: 'severity',
      alerts: null
    }
  };
  assert.equal(alertSources(newstate), '');
});