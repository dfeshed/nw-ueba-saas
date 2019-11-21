import { module, test } from 'qunit';
import { setupTest } from 'ember-qunit';
import * as Users from 'investigate-users/reducers/users/selectors';
import notRiskyCount from '../../data/presidio/not_risky_count';
import usersTabSeverityBar from '../../data/presidio/users_tab_severityBar';
import usrOverview from '../../data/presidio/usr_overview';
import watchedCount from '../../data/presidio/watched_count';
import existAnomalyTypes from '../../data/presidio/exist_anomaly_types';
import existAlertTypes from '../../data/presidio/exist_alert_types';
import favoriteFilter from '../../data/presidio/favorite_filter';
import userList from '../../data/presidio/user-list';
import Immutable from 'seamless-immutable';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const state = Immutable.from({
  users: {
    topUsers: usrOverview.data,
    topUsersError: null,
    riskyUserCount: notRiskyCount,
    watchedUserCount: watchedCount,
    usersSeverity: [usersTabSeverityBar.data],
    existAnomalyTypes,
    existAlertTypes: existAlertTypes.data,
    users: userList.data,
    usersError: null,
    favorites: favoriteFilter.data,
    trendRange: {
      key: 'daily',
      name: 'lastDay'
    },
    sortOnTrending: false,
    allWatched: true,
    currentUserCount: 100,
    totalUsers: 100,
    filter: {
      addAlertsAndDevices: true,
      addAllWatched: true,
      alertTypes: ['snooping_user'],
      departments: null,
      indicatorTypes: ['abnormal_file_action_operation_type'],
      isWatched: false,
      allWatched: true,
      entityType: 'userId',
      locations: null,
      minScore: 0,
      severity: ['high'],
      sortDirection: 'DESC',
      sortField: 'score',
      fromPage: 1,
      size: 25,
      userTags: null
    }
  }
});

module('Unit | Selectors | Users Selectors', (hooks) => {
  setupTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('test Top Risky Users', (assert) => {
    const topUsers = Users.getTopRiskyUsers(state);
    assert.equal(topUsers.length, 5);
    assert.equal(topUsers[0].trendingScore, '15.0');
    assert.equal(topUsers[0].displayName, 'ad_qa_1_9');
    assert.equal(topUsers[0].followed, true);
    assert.equal(topUsers[0].score, 131);
    assert.deepEqual(topUsers[0].alertGroup, {
      Critical: 0,
      High: 2,
      Medium: 9,
      Low: 1
    });
  });

  test('test Top Risky Users with trend', (assert) => {
    const newState = state.setIn(['users', 'sortOnTrending'], true);
    const topUsers = Users.getTopRiskyUsers(newState);
    assert.equal(topUsers.length, 5);
    assert.equal(topUsers[0].trendingScore, '5.0');
    assert.equal(topUsers[0].displayName, 'file_qa_1_18');
    assert.equal(topUsers[0].followed, false);
    assert.equal(topUsers[0].score, 85);
    assert.deepEqual(topUsers[0].alertGroup, {
      Critical: 0,
      High: 5,
      Medium: 0,
      Low: 10
    });
  });

  test('test Total Users', (assert) => {
    assert.equal(Users.totalEntitiesCount(state), 100);
  });

  test('test Sort Field', (assert) => {
    assert.deepEqual(Users.getSortField(state), { id: 'score' });
  });

  test('test All Users', (assert) => {
    assert.equal(Users.allUsersReceived(state), false);
  });

  test('test User Filter', (assert) => {
    assert.deepEqual(Users.getUserFilter(state), state.users.filter);
  });

  test('test risky Filter Applied', (assert) => {
    assert.equal(Users.isRisky(state), true);
    const newState = Immutable.from({ users: { filter: { minScore: null } } });
    assert.equal(Users.isRisky(newState), false);
  });

  test('test Filter Severity', (assert) => {
    assert.equal(Users.getSelectedSeverity(state)[0], 'high');
  });

  test('test admin Filter Applied', (assert) => {
    assert.equal(Users.isAdmin(state), null);
    const newState = Immutable.from({ users: { filter: { userTags: ['admin'] } } });
    assert.equal(Users.isAdmin(newState), true);
  });

  test('test watched Filter Applied', (assert) => {
    assert.equal(Users.isWatched(state), false);
  });

  test('test Exist Alert Types', (assert) => {
    assert.equal(Users.getExistAlertTypes(state).length, 9);
  });

  test('test Exist Anomaly Types', (assert) => {
    assert.equal(Users.getExistAnomalyTypes(state).length, 25);
    assert.deepEqual(Users.getExistAnomalyTypes(state)[0], {
      displayLabel: 'Abnormal File Access Event (3 USERS)',
      id: 'abnormal_file_action_operation_type'
    });
  });

  test('test users error', (assert) => {
    assert.equal(Users.usersError(state), null);
    const newState = state.setIn(['users', 'usersError'], 'error');
    assert.equal(Users.usersError(newState), 'error');
  });

  test('test top users error', (assert) => {
    assert.equal(Users.topUsersError(state), null);
    const newState = state.setIn(['users', 'topUsersError'], 'error');
    assert.equal(Users.topUsersError(newState), 'error');
  });

  test('test top users are present or not', function(assert) {
    assert.equal(Users.hasTopRiskyUsers(state), true);
    const newState = state.setIn(['users', 'topUsers'], null);
    assert.equal(Users.hasTopRiskyUsers(newState), false);
  });

  test('test users are present or not', function(assert) {
    assert.equal(Users.hasUsers(state), true);
    const newState = state.setIn(['users', 'users'], null);
    assert.equal(Users.hasUsers(newState), false);
  });

  test('test Selected Alert Types', (assert) => {
    assert.deepEqual(Users.getSelectedAlertTypes(state), [{
      id: 'snooping_user',
      displayLabel: 'Snooping User'
    }]);
  });

  test('test Selected Anomaly Types', (assert) => {
    assert.deepEqual(Users.getSelectedAnomalyTypes(state), [{
      id: 'abnormal_file_action_operation_type',
      displayLabel: 'Abnormal File Access Event (3 USERS)'
    }]);
  });

  test('test Favorites', (assert) => {
    assert.equal(favoriteFilter.data[1].filterName, 'Test1');
    const favFilter = Users.getFavorites(state);
    assert.equal(favFilter.length, 3);
    assert.equal(favFilter[1].filterName, 'Test1');
  });

  test('test Users', (assert) => {
    assert.equal(Users.getUsers(state).length, 2);
  });

  test('test AllWatched Users', (assert) => {
    assert.equal(Users.allWatched(state), true);
  });

  test('test entityFilter', (assert) => {
    assert.deepEqual(Users.entityFilter, ['userId', 'ja3', 'sslSubject']);
  });

  test('test selectedEntityType', (assert) => {
    assert.equal(Users.selectedEntityType(state), 'userId');
  });

  test('test users Severity', (assert) => {
    assert.deepEqual(Users.getUsersSeverity(state), {
      critical: 0,
      high: 2,
      low: 182,
      medium: 0
    });

    let newState = { users: { usersSeverity: [] } };
    assert.deepEqual(Users.getUsersSeverity(newState), {
      critical: 0,
      high: 0,
      low: 0,
      medium: 0
    });

    newState = { users: { usersSeverity: [{ Critical: null }] } };
    assert.deepEqual(Users.getUsersSeverity(newState), {
      critical: 0,
      high: 0,
      low: 0,
      medium: 0
    });
  });

  test('test trendRange', (assert) => {
    assert.deepEqual(Users.trendRange(state), {
      key: 'daily',
      name: 'lastDay'
    });
  });

  test('test sortOnTrending', (assert) => {
    assert.equal(Users.sortOnTrending(state), false);
  });
});