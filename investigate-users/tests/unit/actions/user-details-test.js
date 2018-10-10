import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import { initialFilterState } from 'investigate-users/reducers/users/selectors';
import { getAdminUserCount, getRiskyUserCount, getWatchedUserCount, getUserOverview } from 'investigate-users/actions/user-details';

module('Unit | Actions | User Details Actions', (hooks) => {
  setupTest(hooks);

  test('it can getAdminUserCount', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_ADMIN_USER_COUNT');
      assert.equal(payload, 40);
      done();
    };
    getAdminUserCount()(dispatch);
  });

  test('it can getRiskyUserCount', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_RISKY_USER_COUNT');
      assert.equal(payload, 57);
      done();
    };
    getRiskyUserCount()(dispatch);
  });

  test('it can getWatchedUserCount', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_WATCHED_USER_COUNT');
      assert.equal(payload, 100);
      done();
    };
    getWatchedUserCount(initialFilterState)(dispatch);
  });

  test('it can getUserOverview', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_TOP_RISKY_USER');
      assert.equal(payload.length, 5);
      done();
    };
    getUserOverview()(dispatch);
  });

});