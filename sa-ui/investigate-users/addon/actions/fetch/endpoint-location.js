import moment from 'moment';

export default {
  // Overview Tab Urls.
  riskyUserCount: '/api/entity/count?minScore=0&',
  watchedUserCount: '/api/entity/count?isWatched=true&',
  totalCount: '/api/entity/count?',
  userOverview: '/api/entity?min_score=0&size=10&sort_direction=DESC&',
  alertOverview: '/api/alerts?isOverview=true&page=1&size=12&sort_direction=DESC&sort_field=score&status=open&',
  alertTimeline: `/api/alerts/alert-by-day-and-severity?alert_start_range=${moment().subtract('months', 3).unix()},${moment().unix()}`,

  // Alerts Tab Urls
  alertList: '/api/alerts?',
  existAnomalyTypesForAlerts: '/api/alerts/exist-anomaly-types',
  alertsExport: '/api/alerts/export?',

  // User Tab Urls
  existAlertTypes: '/api/entity/exist-alert-types?ignore_rejected=true',
  existAnomalyTypes: '/api/entity/exist-anomaly-types',
  favoriteFilter: '/api/entity/favoriteFilter',
  severityBarForUser: '/api/entity/severityBar?',
  userList: '/api/entity?',
  usersExport: '/api/entity/export?',
  followUsers: '/api/entity/true/followUsers', // Post call with filters
  unfollowUsers: '/api/entity/false/followUsers', // Post call with filters
  createfavoriteFilter: '/api/entity/{filterName}/favoriteFilter', // Post call with filters
  deletefavoriteFilter: '/api/entity/favoriteFilter/{filterId}',

  // For global user search
  searchUsers: '/api/entity?page=1&size=10&sort_field=displayName&sort_direction=ASC&'
};