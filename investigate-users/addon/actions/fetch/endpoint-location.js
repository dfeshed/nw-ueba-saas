import moment from 'moment';

export default {
  // Overview Tab Urls.
  riskyUserCount: '/presidio/api/user/count?minScore=0',
  adminUserCount: '/presidio/api/user/count?userTags=admin',
  watchedUserCount: '/presidio/api/user/count?isWatched=true',
  userOverview: '/presidio/api/user?min_score=0&size=5&sort_direction=DESC&sort_field=score',
  alertOverview: `/presidio/api/alerts?alert_start_range=${moment().subtract('months', 3).unix()},${moment().unix()}&page=1&size=10&sort_direction=DESC&sort_field=score&status=open`,

  // Alerts Tab Urls
  alertList: `/presidio/api/alerts?alert_start_range=${moment().subtract('months', 3).unix()},${moment().unix()}&`,
  existAnomalyTypesForAlerts: '/presidio/api/alerts/exist-anomaly-types',

  // User Tab Urls
  existAlertTypes: '/presidio/api/user/exist-alert-types?ignore_rejected=true',
  existAnomalyTypes: '/presidio/api/user/exist-anomaly-types',
  favoriteFilter: '/presidio/api/user/favoriteFilter',
  severityBarForUser: '/presidio/api/user/severityBar?',
  userList: '/presidio/api/user?'
};