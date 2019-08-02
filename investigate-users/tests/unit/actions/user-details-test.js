import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { patchFetch } from '../../helpers/patch-fetch';
import { Promise } from 'rsvp';
import dataIndex from '../../data/presidio';
import { patchFlash } from '../../helpers/patch-flash';
import { getRiskyUserCount, getWatchedUserCount, getUserOverview, resetUser, initiateUser, updateEntityType } from 'investigate-users/actions/user-details';

let patchUrl = null;


module('Unit | Actions | User Details Actions', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    patchFetch((url) => {
      patchUrl = url;
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

  test('it can getRiskyUserCount for ja3 certificate', (assert) => {
    assert.expect(3);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.ok(patchUrl.indexOf('entityType=ja3') > -1);
      assert.equal(type, 'INVESTIGATE_USER::GET_RISKY_USER_COUNT');
      assert.equal(payload, 57);
      patchUrl = null;
      done();
    };
    getRiskyUserCount('ja3')(dispatch);
  });

  test('it can give flash message if getRiskyUserCount is not coming from server', (assert) => {
    const done = assert.async();
    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject({
          ok: true,
          error: 'some error'
        });
      });
    });
    getRiskyUserCount()();

    patchFlash((flash) => {
      assert.equal(flash.type, 'error');
      done();
    });
  });

  test('it can getWatchedUserCount', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_WATCHED_USER_COUNT');
      assert.equal(payload, 100);
      done();
    };
    getWatchedUserCount()(dispatch);
  });

  test('it can getWatchedUserCount for ja3 certificate', (assert) => {
    assert.expect(3);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.ok(patchUrl.indexOf('entityType=ja3') > -1);
      assert.equal(type, 'INVESTIGATE_USER::GET_WATCHED_USER_COUNT');
      assert.equal(payload, 100);
      patchUrl = null;
      done();
    };
    getWatchedUserCount('ja3')(dispatch);
  });

  test('it can give flash message if getWatchedUserCount is not coming from server', (assert) => {
    const done = assert.async();
    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject({
          ok: true,
          error: 'some error'
        });
      });
    });
    getWatchedUserCount()();

    patchFlash((flash) => {
      assert.equal(flash.type, 'error');
      done();
    });
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

  test('it can getUserOverview for SSlSubject', (assert) => {
    assert.expect(3);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.ok(patchUrl.indexOf('entityType=sslSubject') > -1);
      assert.equal(type, 'INVESTIGATE_USER::GET_TOP_RISKY_USER');
      assert.equal(payload.length, 5);
      patchUrl = null;
      done();
    };
    getUserOverview('sslSubject')(dispatch);
  });

  test('it should dispatch error if getUserOverview is failing', (assert) => {
    patchFetch(() => {
      return new Promise(function(resolve, reject) {
        reject({
          ok: true,
          error: 'some error'
        });
      });
    });
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::TOP_USERS_ERROR');
      assert.equal(payload, 'topUsersError');
      done();
    };
    getUserOverview()(dispatch);
  });

  test('it should dispatch error if no user data is present for getUserOverview', (assert) => {
    patchFetch(() => {
      return new Promise(function(resolve) {
        resolve({
          ok: true,
          json() {
            return { data: [] };
          }
        });
      });
    });
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::TOP_USERS_ERROR');
      assert.equal(payload, 'noUserData');
      done();
    };
    getUserOverview()(dispatch);
  });

  test('it can resetUser', (assert) => {
    assert.expect(1);
    const done = assert.async();
    const dispatch = ({ type }) => {
      assert.equal(type, 'INVESTIGATE_USER::RESET_USER');
      done();
    };
    dispatch(resetUser());
  });

  test('it can updateEntityType', (assert) => {
    assert.expect(5);
    const actions = [
      'INVESTIGATE_USER::UPDATE_FILTER_FOR_USERS',
      'INVESTIGATE_USER::RESET_USERS',
      'INVESTIGATE_USER::GET_RISKY_USER_COUNT',
      'INVESTIGATE_USER::GET_WATCHED_USER_COUNT',
      'INVESTIGATE_USER::GET_TOP_RISKY_USER'
    ];
    const dispatch = (obj) => {
      if (obj.type) {
        assert.ok(actions.includes(obj.type));
      }
      if (obj.payload) {
        assert.equal(obj.payload.sortField, 'score');
      }
      if (typeof obj === 'function') {
        obj(({ type }) => {
          assert.ok(actions.includes(type));
        });
      }
    };
    const getState = () => {
      return Immutable.from({ users: { filter: { entityType: 'userId' } } });
    };
    updateEntityType('sslSubject')(dispatch, getState);
  });

  test('it can initiateUser', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::INITIATE_USER');
      assert.equal(payload.userId, 123);
      done();
    };
    dispatch(initiateUser({ userId: 123 }));
  });

});