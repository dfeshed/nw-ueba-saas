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
import alertsList from './alerts-list';

const urlMap = [{
  url: 'presidio/api/user?min_score=0&size=5',
  data: usrOverview
}, {
  url: 'presidio/data/controls/user_types',
  data: userType
}, {
  url: 'presidio/api/user/usersTagsCount',
  data: userTag
}, {
  url: 'presidio/api/user/count?userTags=admin',
  data: userAdminCount
}, {
  url: 'presidio/api/user/count?isWatched=true',
  data: watchedCount
}, {
  url: 'presidio/api/user/count?minScore=0',
  data: notRiskyCount
}, {
  url: 'presidio/api/alerts?alert_start_range=',
  data: alertOverview
}, {
  url: 'presidio/api/user?min_score=0&size=5&sort_direction=DESC&sort_field=score',
  data: userData
}, {
  url: 'presidio/api/user/severityBar',
  data: usersTabSeverityBar
}, {
  url: 'presidio/api/user/exist-anomaly-types',
  data: existAnomalyTypes
}, {
  url: 'presidio/api/user/exist-alert-types?ignore_rejected=true',
  data: existAlertTypes
}, {
  url: 'presidio/api/user/favoriteFilter',
  data: favoriteFilter
}, {
  url: 'presidio/api/user?addAlertsAndDevices=true&addAllWatched=true',
  data: userList
}, {
  url: 'presidio/api/alerts/exist-anomaly-types',
  data: existAnomalyTypesAlerts
}, {
  url: 'presidio/api/alerts?alert_start_range=1519948800000,1535587199000&fromPage=1&size=10&sort_direction=DESC&sort_field=startDate&total_severity_count=true',
  data: alertsList
}];

export default (req) => {
  return urlMap.find(({ url }) => req.indexOf(url) > 0) ? urlMap.find(({ url }) => req.indexOf(url) > 0).data : userList;
};