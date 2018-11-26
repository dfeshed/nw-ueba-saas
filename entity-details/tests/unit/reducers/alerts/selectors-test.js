import { module, test } from 'qunit';
import { alertId } from 'entity-details/reducers/alerts/selectors';

module('Unit | Selector | Alerts Selector');

const state = {
  alerts: {
    alertId: 'alert-1'
  }
};

test('test alert state', function(assert) {
  assert.equal(alertId(state), 'alert-1');
});