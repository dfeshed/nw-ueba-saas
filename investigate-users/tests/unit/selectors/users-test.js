import { module, test } from 'qunit';
import * as Users from 'investigate-users/reducers/users/selectors';
import notRiskyCount from '../../data/presidio/not_risky_count';
import userAdminCount from '../../data/presidio/user_admin_count';
import usersTabSeverityBar from '../../data/presidio/users_tab_severityBar';
import usrOverview from '../../data/presidio/usr_overview';
import watchedCount from '../../data/presidio/watched_count';
import existAnomalyTypes from '../../data/presidio/exist_anomaly_types';
import existAlertTypes from '../../data/presidio/exist_alert_types';
import favoriteFilter from '../../data/presidio/favorite_filter';
import userList from '../../data/presidio/user-list';
import Immutable from 'seamless-immutable';

module('Unit | Selectors | Users Selectors');

const state = Immutable.from({
  users: {
    topUsers: usrOverview,
    riskyUserCount: notRiskyCount,
    watchedUserCount: watchedCount,
    adminUserCount: userAdminCount,
    usersSeverity: [usersTabSeverityBar.data],
    existAnomalyTypes,
    existAlertTypes: existAlertTypes.data,
    users: userList,
    favorites: favoriteFilter,
    totalUsers: 100,
    filter: {
      addAlertsAndDevices: true,
      addAllWatched: true,
      alertTypes: ['snooping_user'],
      departments: null,
      indicatorTypes: ['abnormal_file_action_operation_type'],
      isWatched: false,
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

test('test Top Risky Users', (assert) => {
  assert.equal(Users.getTopRiskyUsers(state).data.length, 5);
});

test('test Total Users', (assert) => {
  assert.equal(Users.getTotalUsers(state), 100);
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
  assert.equal(Users.getFilterSeverity(state)[0], 'high');
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
  assert.equal(Users.getExistAnomalyTypes(state).length, 26);
});

test('test Selected Alert Types', (assert) => {
  assert.deepEqual(Users.getSelectedAlertTypes(state), [{
    id: 'snooping_user',
    name: 'snooping_user (14 Users)'
  }]);
});

test('test Selected Anomaly Types', (assert) => {
  assert.deepEqual(Users.getSelectedAnomalyTypes(state), [{
    id: 'abnormal_file_action_operation_type',
    name: 'abnormal_file_action_operation_type (3 Users)'
  }]);
});

test('test Favorites', (assert) => {
  assert.equal(Users.getFavorites(state).data.length, 2);
});

test('test Users', (assert) => {
  assert.equal(Users.getUsers(state).data.length, 2);
});

test('test users Severity', (assert) => {
  assert.deepEqual(Users.getUsersSeverity(state), {
    critical: 0,
    high: 2,
    low: 182,
    medium: 0
  });

  const newState = { users: { usersSeverity: [] } };
  assert.equal(Users.getUsersSeverity(newState), null);
});