import { combineReducers } from 'redux';
import schema from './schema/reducer';
import fileList from './file-list/reducer';
import filter from 'investigate-shared/reducers/endpoint-filter/reducer';
import visuals from './visuals/reducer';
import preferences from './preferences/reducer';
import endpointServer from './endpoint-server/reducer';
import endpointQuery from './endpoint-query/reducer';
import investigateQuery from './investigate-query/reducer';
import { createFilteredReducer } from 'component-lib/utils/reducer-wrapper';

const reducerPredicate = (action) => action.meta && action.meta.name === 'FILE';

export default combineReducers({
  files: combineReducers({
    fileList,
    schema,
    filter: createFilteredReducer(filter, reducerPredicate),
    visuals
  }),
  preferences,
  endpointServer,
  endpointQuery,
  investigateQuery
});
