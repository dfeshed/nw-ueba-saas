import { combineReducers } from 'redux';
import data from './data-reducer';
import columnGroup from './column-group/reducer';
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
import listManager from 'rsa-list-manager/reducers/list-manager/reducer';
import { reducerPredicate, createFilteredReducer } from 'component-lib/utils/reducer-wrapper';

export default combineReducers({
  data,
  columnGroup,
  dictionaries,
  eventCount,
  eventResults,
  eventTimeline,
  files,
  meta,
  notifications,
  queryNode,
  queryStats,
  listManagers: combineReducers({
    columnGroups: createFilteredReducer(listManager, reducerPredicate('COLUMN_GROUPS'))
  }),
  services
});
