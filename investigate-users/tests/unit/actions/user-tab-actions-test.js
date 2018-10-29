import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import { patchFetch } from '../../helpers/patch-fetch';
import { Promise } from 'rsvp';
import dataIndex from '../../data/presidio';
import { followUsers, unfollowUsers, deleteFavorite, exportUsers, saveAsFavorite, getSeverityDetailsForUserTabs, getExistAlertTypess, getExistAnomalyTypes, getFavorites, resetUsers, updateFilter, getUsers } from 'investigate-users/actions/user-tab-actions';

export const initialFilterState = Immutable.from({
  addAlertsAndDevices: true,
  addAllWatched: true,
  alertTypes: null,
  departments: null,
  indicatorTypes: null,
  isWatched: false,
  locations: null,
  minScore: null,
  severity: null,
  sortDirection: 'DESC',
  sortField: 'score',
  fromPage: 1,
  size: 25,
  userTags: null
});

module('Unit | Actions | User Tab Actions', (hooks) => {
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

  test('it can getSeverityDetailsForUserTabs', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_SEVERITY_FOR_USERS');
      assert.equal(payload.High.userCount, 2);
      done();
    };
    getSeverityDetailsForUserTabs()(dispatch);
  });

  test('it can getExistAnomalyTypes', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_EXIST_ANOMALY_TYPES');
      assert.equal(payload.abnormal_active_directory_day_time_operation, 20);
      done();
    };
    getExistAnomalyTypes()(dispatch);
  });

  test('it can getExistAlertTypess', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.equal(type, 'INVESTIGATE_USER::GET_EXIST_ALERT_TYPES');
        assert.equal(payload.length, 9);
        done();
      }
    };
    getExistAlertTypess(initialFilterState)(dispatch);
  });

  test('it can getFavorites', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      assert.equal(type, 'INVESTIGATE_USER::GET_FAVORITES');
      assert.equal(payload.length, 2);
      done();
    };
    getFavorites()(dispatch);
  });

  test('it can resetUsers', (assert) => {
    assert.expect(1);
    const dispatch = ({ type }) => {
      assert.equal(type, 'INVESTIGATE_USER::RESET_USERS');
    };
    dispatch(resetUsers());
  });

  test('it can updateFilter', (assert) => {
    const done = assert.async();
    const actions = ['INVESTIGATE_USER::UPDATE_FILTER_FOR_USERS', 'INVESTIGATE_USER::RESET_USERS'];
    const dispatch = ({ type, payload }) => {
      if (type) {
        assert.ok(actions.includes(type));
      }
      if (payload) {
        assert.equal(payload.sortField, 'score');
        done();
      }
    };
    updateFilter(initialFilterState)(dispatch);
  });

  test('it can updateFilter', (assert) => {
    assert.expect(5);
    const actions = ['INVESTIGATE_USER::UPDATE_FILTER_FOR_USERS', 'INVESTIGATE_USER::RESET_USERS', 'INVESTIGATE_USER::GET_SEVERITY_FOR_USERS', 'INVESTIGATE_USER::GET_USERS'];
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
    updateFilter(initialFilterState)(dispatch);
  });

  test('it should reset Filter', (assert) => {
    assert.expect(2);
    const actions = ['INVESTIGATE_USER::UPDATE_FILTER_FOR_USERS', 'INVESTIGATE_USER::RESET_USERS', 'INVESTIGATE_USER::GET_SEVERITY_FOR_USERS', 'INVESTIGATE_USER::GET_USERS'];
    const dispatch = (obj) => {
      if (obj.type) {
        assert.ok(actions.includes(obj.type));
      }
      if (obj.payload) {
        assert.equal(obj.payload, null);
      }
      if (typeof obj === 'function') {
        obj(() => {
          assert.notOk(true, 'this should not be called.');
        });
      }
    };
    updateFilter('RESET', true)(dispatch);
  });

  test('it can updateFilter without fetching user details', (assert) => {
    assert.expect(3);
    const actions = ['INVESTIGATE_USER::UPDATE_FILTER_FOR_USERS', 'INVESTIGATE_USER::RESET_USERS', 'INVESTIGATE_USER::GET_SEVERITY_FOR_USERS', 'INVESTIGATE_USER::GET_USERS'];
    const dispatch = (obj) => {
      if (obj.type) {
        assert.ok(actions.includes(obj.type));
      }
      if (obj.payload) {
        assert.equal(obj.payload.sortField, 'score');
      }
      if (typeof obj === 'function') {
        obj(() => {
          assert.notOk(true, 'this should not be called.');
        });
      }
    };
    updateFilter(initialFilterState, true)(dispatch);
  });

  test('it can getUsers', (assert) => {
    assert.expect(2);
    const done = assert.async();
    const dispatch = ({ type, payload }) => {
      if (type === 'INVESTIGATE_USER::GET_USERS') {
        assert.equal(type, 'INVESTIGATE_USER::GET_USERS');
        assert.equal(payload.data.length, 2);
        done();
      }
    };
    getUsers(initialFilterState)(dispatch);
  });

  test('it can saveAsFavorite', (assert) => {
    assert.expect(1);
    const done = assert.async();
    const getState = () => {
      return { users: { filter: initialFilterState } };
    };
    const dispatch = (fn) => {
      fn(({ type }) => {
        assert.equal(type, 'INVESTIGATE_USER::GET_FAVORITES');
        done();
      });
    };
    saveAsFavorite('Test')(dispatch, getState);
  });

  test('it can exportUsers', (assert) => {
    assert.expect(1);
    window.URL.createObjectURL = () => {
      assert.ok(true, 'This function supposed to be called for altert export');
    };
    const getState = () => {
      return { users: { filter: initialFilterState } };
    };
    exportUsers(initialFilterState)(null, getState);
  });

  test('it can deleteFavorite', (assert) => {
    assert.expect(1);
    const done = assert.async();
    const dispatch = (fn) => {
      fn(({ type }) => {
        assert.equal(type, 'INVESTIGATE_USER::GET_FAVORITES');
        done();
      });
    };
    deleteFavorite('TestId')(dispatch);
  });

  test('it can followUsers', (assert) => {
    assert.expect(1);
    const getState = () => {
      return { users: { filter: initialFilterState } };
    };
    const dispatchInt = ({ type }) => {
      if (type) {
        assert.equal(type, 'INVESTIGATE_USER::RESET_USERS');
      }
    };
    const dispatch = (fn) => {
      fn(dispatchInt, getState);
    };
    followUsers()(dispatch, getState);
  });

  test('it can unfollowUsers', (assert) => {
    assert.expect(1);
    const getState = () => {
      return { users: { filter: initialFilterState } };
    };
    const dispatchInt = ({ type }) => {
      if (type) {
        assert.equal(type, 'INVESTIGATE_USER::RESET_USERS');
      }
    };
    const dispatch = (fn) => {
      fn(dispatchInt, getState);
    };
    unfollowUsers()(dispatch, getState);
  });

});