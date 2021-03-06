import alertOverview from './alert_overview';
import notRiskyCount from './not_risky_count';
import userAdminCount from './user_admin_count';
import userData from './user_data';
import usersTabSeverityBar from './users_tab_severityBar';
import userTag from './user_tag';
import userType from './user_type';
import usrOverview from './usr_overview';
import watchedCount from './watched_count';
import existAnomalyTypes from './exist_anomaly_types';
import existAnomalyTypesAlerts from './exist_anomaly_types_alerts';
import existAlertTypes from './exist_alert_types';
import favoriteFilter from './favorite_filter';
import userList from './user-list';
import userSearch from './user_search';
import alertsTimeline from './alert-by-day-and-severity';

const urlMap = [{
  url: 'presidio/api/entity?page=1&size=10&sort_field=displayName&search_field_contains',
  data: userSearch
}, {
  url: 'presidio/api/entity?min_score=0&size=10',
  data: usrOverview
}, {
  url: 'presidio/data/controls/user_types',
  data: userType
}, {
  url: 'presidio/api/entity/usersTagsCount',
  data: userTag
}, {
  url: 'presidio/api/entity/count?userTags=admin',
  data: userAdminCount
}, {
  url: 'presidio/api/entity/count?isWatched=true',
  data: watchedCount
}, {
  url: 'presidio/api/entity/count?entityType=',
  data: watchedCount
}, {
  url: 'presidio/api/entity/count?minScore=0',
  data: notRiskyCount
}, {
  url: 'presidio/api/alerts?',
  data: alertOverview
}, {
  url: 'presidio/api/entity?min_score=0&size=5&sort_direction=DESC&sort_field=score',
  data: userData
}, {
  url: 'presidio/api/entity/severityBar',
  data: usersTabSeverityBar
}, {
  url: 'presidio/api/entity/exist-anomaly-types',
  data: existAnomalyTypes
}, {
  url: 'presidio/api/entity/exist-alert-types?ignore_rejected=true',
  data: existAlertTypes
}, {
  url: 'presidio/api/entity/favoriteFilter',
  data: favoriteFilter
}, {
  url: 'presidio/api/entity?addAlertsAndDevices=true&addAllWatched=true',
  data: userList
}, {
  url: 'presidio/api/alerts/exist-anomaly-types',
  data: existAnomalyTypesAlerts
}, {
  url: 'presidio/api/alerts/alert-by-day-and-severity',
  data: alertsTimeline
}, {
  url: 'presidio/api/entity/favoriteFilter/',
  data: true
}, {
  url: 'favoriteFilter',
  data: true
}];

export default (req) => {
  return urlMap.find(({ url }) => req.indexOf(url) > 0) ? urlMap.find(({ url }) => req.indexOf(url) > 0).data : userList;
};