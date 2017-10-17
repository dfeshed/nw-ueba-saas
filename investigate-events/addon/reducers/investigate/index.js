import { combineReducers } from 'redux';
import data from './data-reducer';
import dictionaries from './dictionaries/reducer';
import eventCount from './event-count/reducer';
import eventResults from './event-results/reducer';
import eventTimeline from './event-timeline/reducer';
import meta from './meta/reducer';
import queryNode from './query-node/reducer';
import services from './services/reducer';

export default combineReducers({
  data,
  dictionaries,
  eventCount,
  eventResults,
  eventTimeline,
  meta,
  queryNode,
  services
});
