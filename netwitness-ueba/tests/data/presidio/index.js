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
  url: 'api/entity?page=1&size=10&sort_field=displayName&search_field_contains',
  data: userSearch
}, {
  url: 'api/entity?min_score=0&size=10',
  data: usrOverview
}, {
  url: 'ata/controls/user_types',
  data: userType
}, {
  url: 'api/entity/usersTagsCount',
  data: userTag
}, {
  url: 'api/entity/count?userTags=admin',
  data: userAdminCount
}, {
  url: 'api/entity/count?isWatched=true',
  data: watchedCount
}, {
  url: 'api/entity/count?entityType=',
  data: watchedCount
}, {
  url: 'api/entity/count?minScore=0',
  data: notRiskyCount
}, {
  url: 'api/alerts?',
  data: alertOverview
}, {
  url: 'api/entity?min_score=0&size=5&sort_direction=DESC&sort_field=score',
  data: userData
}, {
  url: 'api/entity/severityBar',
  data: usersTabSeverityBar
}, {
  url: 'api/entity/exist-anomaly-types',
  data: existAnomalyTypes
}, {
  url: 'api/entity/exist-alert-types?ignore_rejected=true',
  data: existAlertTypes
}, {
  url: 'api/entity/favoriteFilter',
  data: favoriteFilter
}, {
  url: 'api/entity?addAlertsAndDevices=true&addAllWatched=true',
  data: userList
}, {
  url: 'api/alerts/exist-anomaly-types',
  data: existAnomalyTypesAlerts
}, {
  url: 'api/alerts/alert-by-day-and-severity',
  data: alertsTimeline
}, {
  url: 'api/entity/favoriteFilter/',
  data: true
}, {
  url: 'favoriteFilter',
  data: true
}];

export default (req) => {
  return urlMap.find(({ url }) => req.indexOf(url) > 0) ? urlMap.find(({ url }) => req.indexOf(url) > 0).data : userList;
};