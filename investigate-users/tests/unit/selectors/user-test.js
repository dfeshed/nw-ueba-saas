import { module, test } from 'qunit';
import { userId, alertId, indicatorId } from 'investigate-users/reducers/user/selectors';

module('Unit | Selector | User Selector');

const state = {
  user: {
    userId: 'user-1',
    alertId: 'alert-1',
    indicatorId: 'ind-1'
  }
};

test('test user state', function(assert) {
  assert.equal(userId(state), 'user-1');
  assert.equal(alertId(state), 'alert-1');
  assert.equal(indicatorId(state), 'ind-1');
});