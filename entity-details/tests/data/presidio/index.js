import userDetails from './user_details';
import alertMarkRisk from './alert-mark-risk';
import indicatorCount from './indicator-count';
import indicatorDistinctEventsByTime from './indicator-distinctEventsByTime';
import indicatorHourlyCountGroupByDayOfWeek from './indicator-hourlyCountGroupByDayOfWeek';
import indicatorEvents from './indicator-events';
import userAlerts from './user_alerts';

const urlMap = [{
  url: 'details',
  data: userDetails
}, {
  url: 'presidio/api/alerts/',
  data: alertMarkRisk
}, {
  url: 'function=Count',
  data: indicatorCount
}, {
  url: 'function=distinctEventsByTime',
  data: indicatorDistinctEventsByTime
}, {
  url: 'function=hourlyCountGroupByDayOfWeek',
  data: indicatorHourlyCountGroupByDayOfWeek
}, {
  url: 'events?',
  data: indicatorEvents
}, {
  url: 'presidio/api/alerts?',
  data: userAlerts
}];

export default (req) => {
  return urlMap.find(({ url }) => req.indexOf(url) > 0) ? urlMap.find(({ url }) => req.indexOf(url) > 0).data : userDetails;
};