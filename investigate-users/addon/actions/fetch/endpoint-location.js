import moment from 'moment';

export default {
  // Overview Tab Urls.
  riskyUserCount: '/presidio/api/entity/count?minScore=0',
  watchedUserCount: '/presidio/api/entity/count?isWatched=true',
  userOverview: '/presidio/api/entity?min_score=0&size=5&sort_direction=DESC&sort_field=score',
  alertOverview: `/presidio/api/alerts?alert_start_range=${moment().subtract('months', 3).unix() * 1000},${moment().unix() * 1000}&page=1&size=10&sort_direction=DESC&sort_field=score&status=open`,
  alertTimeline: `/presidio/api/alerts/alert-by-day-and-severity?alert_start_range=${moment().subtract('months', 3).unix()},${moment().unix()}`,

  // Alerts Tab Urls
  alertList: '/presidio/api/alerts?',
  existAnomalyTypesForAlerts: '/presidio/api/alerts/exist-anomaly-types',
  alertsExport: '/presidio/api/alerts/export?',

  // User Tab Urls
  existAlertTypes: '/presidio/api/entity/exist-alert-types?ignore_rejected=true',
  existAnomalyTypes: '/presidio/api/entity/exist-anomaly-types',
  favoriteFilter: '/presidio/api/entity/favoriteFilter',
  severityBarForUser: '/presidio/api/entity/severityBar?',
  userList: '/presidio/api/entity?',
  usersExport: '/presidio/api/entity/export?',
  followUsers: '/presidio/api/entity/true/followUsers', // Post call with filters
  unfollowUsers: '/presidio/api/entity/false/followUsers', // Post call with filters
  createfavoriteFilter: '/presidio/api/entity/{filterName}/favoriteFilter', // Post call with filters
  deletefavoriteFilter: '/presidio/api/entity/favoriteFilter/{filterId}',

  // For global user search
  searchUsers: '/presidio/api/entity?page=1&size=10&sort_field=displayName&sort_direction=ASC&'
};