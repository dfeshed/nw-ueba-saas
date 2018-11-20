import Immutable from 'seamless-immutable';

const _set = (obj, key, val) => {
  if (obj[key]) {
    obj[key] = val;
    return;
  }

  const keys = key.split('.');
  const firstKey = keys.shift();

  if (!obj[firstKey]) {
    obj[firstKey] = {};
  }

  if (keys.length === 0) {
    obj[firstKey] = val;
    return;
  } else {
    _set(obj[firstKey], keys.join('.'), val);
  }
};

export default class DataHelper {
  constructor(setState) {
    this.state = {
      tabs: {
        activeTabName: 'overview'
      },
      users: {
        topUsers: [],
        riskyUserCount: 0,
        watchedUserCount: 0,
        adminUserCount: 0,
        usersSeverity: [{
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
        }],
        existAnomalyTypes: null,
        existAlertTypes: null,
        favorites: null,
        totalUsers: null,
        filter: {
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
        }
      },
      alerts: {
        topAlerts: [],
        alertList: [],
        existAnomalyTypes: null,
        alertsSeverity: {
          total_severity_count: {
            Critical: null,
            High: null,
            Low: null,
            Medium: null
          }
        },
        filter: {
          sort_direction: 'DESC',
          sort_field: 'startDate',
          total_severity_count: true,
          severity: null,
          feedback: null,
          indicator_types: null,
          alert_start_range: null,
          fromPage: 1,
          size: 25
        },
        totalAlerts: null
      }
    };
    this.setState = setState;
  }

  // Trigger setState, also return the resulting state
  // in case it needs to be used/checked
  build() {
    const state = Immutable.from({
      ...this.state
    });
    if (this.setState) {
      this.setState(state);
    }
    return state.asMutable();
  }

  existAnomalyTypesForFilter(obj) {
    _set(this.state, 'alerts.existAnomalyTypes', obj);
    return this;
  }

  alertTimeLine(obj) {
    _set(this.state, 'alerts.alertsForTimeline', obj);
    return this;
  }

  existAnomalyTypesForUsers(existAnomalyTypes) {
    _set(this.state, 'users.existAnomalyTypes', existAnomalyTypes);
    return this;
  }

  alertsSeverity(obj) {
    _set(this.state, 'alerts.alertsSeverity', obj);
    return this;
  }

  alertsListdata(obj) {
    _set(this.state, 'alerts.alertList', obj);
    return this;
  }

  activeTab(tabName) {
    _set(this.state, 'tabs.activeTabName', tabName);
    return this;
  }

  topUsers(users) {
    _set(this.state, 'users.topUsers', users);
    return this;
  }

  allWatched(isAllWatched) {
    _set(this.state, 'users.allWatched', isAllWatched);
    return this;
  }

  topAlerts(alerts) {
    _set(this.state, 'alerts.topAlerts', alerts);
    return this;
  }

  userSeverity(usersSeverity) {
    _set(this.state, 'users.usersSeverity', usersSeverity);
    return this;
  }

  users(users) {
    _set(this.state, 'users.users', users);
    return this;
  }

  totalUsers(totalUsers) {
    _set(this.state, 'users.totalUsers', totalUsers);
    return this;
  }

  usersExistAlertTypes(existAlertTypes) {
    _set(this.state, 'users.existAlertTypes', existAlertTypes);
    return this;
  }

  usersFavorites(favorites) {
    _set(this.state, 'users.favorites', favorites);
    return this;
  }

  usersFilter(filter) {
    _set(this.state, 'users.filter', filter);
    return this;
  }

  usersCount(riskyUserCount, watchedUserCount, adminUserCount) {
    _set(this.state, 'users.riskyUserCount', riskyUserCount);
    _set(this.state, 'users.watchedUserCount', watchedUserCount);
    _set(this.state, 'users.adminUserCount', adminUserCount);
    return this;
  }

  userDetails(userDetails) {
    _set(this.state, 'user', userDetails);
    return this;
  }
}
