import { test, module } from 'qunit';
import { setupTest } from 'ember-qunit';
import reducer from 'investigate-users/reducers/users/reducer';
import Immutable from 'seamless-immutable';
import * as ACTION_TYPES from 'investigate-users/actions/types';
import notRiskyCount from '../../data/presidio/not_risky_count';
import usersTabSeverityBar from '../../data/presidio/users_tab_severityBar';
import usrOverview from '../../data/presidio/usr_overview';
import watchedCount from '../../data/presidio/watched_count';
import existAnomalyTypes from '../../data/presidio/exist_anomaly_types';
import existAlertTypes from '../../data/presidio/exist_alert_types';
import favoriteFilter from '../../data/presidio/favorite_filter';
import userList from '../../data/presidio/user-list';

export const initialFilterState = Immutable.from({
  addAlertsAndDevices: true,
  addAllWatched: true,
  alertTypes: null,
  departments: null,
  indicatorTypes: null,
  isWatched: false,
  locations: null,
  entityType: 'userId',
  minScore: null,
  severity: null,
  sortDirection: 'DESC',
  sortField: 'score',
  fromPage: 1,
  size: 25,
  userTags: null
});

const initialUsersSeverity = Immutable.from([{
  High: {
    userCount: null
  },
  Low: {
    userCount: null
  },
  Medium: {
    userCount: null
  },
  Critical: {
    userCount: null
  }
}]);

const resetState = Immutable.from({
  topUsers: [],
  topUsersError: null,
  trendRange: {
    key: 'daily',
    name: 'lastDay'
  },
  sortOnTrending: false,
  riskyUserCount: 0,
  watchedUserCount: 0,
  usersSeverity: initialUsersSeverity,
  existAnomalyTypes: null,
  existAlertTypes: null,
  favorites: null,
  users: [],
  usersError: null,
  currentUserCount: null,
  totalUsers: null,
  allWatched: false,
  filter: initialFilterState
});

module('Unit | Reducers | Users Reducer', (hooks) => {
  setupTest(hooks);

  test('test restore default should reset state back', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.RESTORE_DEFAULT
    });

    assert.deepEqual(result, resetState);
  });

  test('test top risky users', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_TOP_RISKY_USER,
      payload: usrOverview
    });

    assert.equal(result.topUsers.length, 5);
  });

  test('test get watched user count', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_WATCHED_USER_COUNT,
      payload: watchedCount
    });

    assert.equal(result.watchedUserCount.data, 100);
  });

  test('test top users error', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.TOP_USERS_ERROR,
      payload: 'error'
    });

    assert.equal(result.topUsersError, 'error');
  });

  test('test users error', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.USERS_ERROR,
      payload: 'error'
    });

    assert.equal(result.usersError, 'error');
  });

  test('test get risky user count', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_RISKY_USER_COUNT,
      payload: notRiskyCount
    });

    assert.equal(result.riskyUserCount.data, 57);
  });

  test('test get favorites', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_FAVORITES,
      payload: favoriteFilter
    });

    assert.equal(result.favorites.data.length, 2);
  });

  test('test get severity for users', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_SEVERITY_FOR_USERS,
      payload: usersTabSeverityBar
    });

    assert.equal(result.usersSeverity[0].data.Critical.userCount, 0);
    assert.equal(result.usersSeverity[0].data.High.userCount, 2);
  });

  test('test get anomaly type for filter', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_EXIST_ANOMALY_TYPES,
      payload: existAnomalyTypes
    });

    assert.equal(result.existAnomalyTypes.abnormal_active_directory_day_time_operation, 20);
  });

  test('test get alert types', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.GET_EXIST_ALERT_TYPES,
      payload: existAlertTypes
    });

    assert.equal(result.existAlertTypes.data.length, 9);
  });

  test('test get users', (assert) => {

    let result = reducer(Immutable.from({ users: [] }), {
      type: ACTION_TYPES.GET_USERS,
      payload: { data: userList, total: 50, info: { allWatched: true } }
    });

    assert.equal(result.users[0].data.length, 2);

    assert.equal(result.allWatched, true);

    result = reducer(result, {
      type: ACTION_TYPES.RESET_USERS
    });

    assert.equal(result.users.length, 0);

    assert.equal(result.allWatched, false);
    assert.equal(result.topUsersError, null);
    assert.equal(result.usersError, null);
  });

  test('test update filters for user', (assert) => {
    assert.notOk(initialFilterState.isWatched);

    let result = reducer(Immutable.from({ filter: initialFilterState }), {
      type: ACTION_TYPES.UPDATE_FILTER_FOR_USERS,
      payload: { isWatched: true }
    });

    assert.ok(result.filter.isWatched);

    result = reducer(Immutable.from({ filter: initialFilterState }), {
      type: ACTION_TYPES.UPDATE_FILTER_FOR_USERS,
      payload: null
    });
    assert.deepEqual(result.filter, initialFilterState);
  });

  test('test SORT_ON_TREND', (assert) => {

    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.SORT_ON_TREND
    });

    assert.equal(result.sortOnTrending, true);
  });

  test('test UPDATE_TREND_RANGE', (assert) => {
    const result = reducer(Immutable.from({}), {
      type: ACTION_TYPES.UPDATE_TREND_RANGE,
      payload: {
        key: 'daily',
        name: 'lastDay'
      }
    });
    assert.deepEqual(result.trendRange, {
      key: 'daily',
      name: 'lastDay'
    });
  });

});