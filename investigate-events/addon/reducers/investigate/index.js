import { combineReducers } from 'redux';
import data from './data-reducer';
import columnGroup from './column-group/reducer';
import metaGroup from './meta-group/reducer';
import profile from './profile/reducer';
import dictionaries from './dictionaries/reducer';
import eventCount from './event-count/reducer';
import eventResults from './event-results/reducer';
import eventTimeline from './event-timeline/reducer';
import files from './files/reducer';
import meta from './meta/reducer';
import notifications from './notifications/reducer';
import queryNode from './query-node/reducer';
import queryStats from './query-stats/reducer';
import services from './services/reducer';

export default combineReducers({
  data,
  columnGroup,
  metaGroup,
  profile,
  dictionaries,
  eventCount,
  eventResults,
  eventTimeline,
  files,
  meta,
  notifications,
  queryNode,
  queryStats,
  services
});
