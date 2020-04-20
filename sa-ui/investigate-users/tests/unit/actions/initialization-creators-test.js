import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialFilterState } from 'investigate-users/reducers/users/selectors';
import { initTabs } from 'investigate-users/actions/initialization-creators';

module('Unit | Actions | Initialization-creators Actions', (hooks) => {
  setupTest(hooks);

  test('it can initTabs for overview', (assert) => {
    assert.expect(2);
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.equal(type, 'INVESTIGATE_USER::UPDATE_ACTIVE_TAB');
        assert.equal(payload, 'overview');
      }
    };
    initTabs('overview')(dispatch);
  });

  test('it can initTabs for Users', (assert) => {
    assert.expect(2);
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.equal(type, 'INVESTIGATE_USER::UPDATE_ACTIVE_TAB');
        assert.equal(payload, 'users');
      }
    };
    initTabs('users', initialFilterState)(dispatch);
  });

  test('it can initTabs for Alerts', (assert) => {
    assert.expect(2);
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.equal(type, 'INVESTIGATE_USER::UPDATE_ACTIVE_TAB');
        assert.equal(payload, 'alerts');
      }
    };
    initTabs('alerts', initialFilterState)(dispatch);
  });

});