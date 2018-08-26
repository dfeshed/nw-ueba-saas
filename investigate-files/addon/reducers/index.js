import { combineReducers } from 'redux';
import schema from './schema/reducer';
import fileList from './file-list/reducer';
import filter from './file-filter/reducer';
import preferences from './preferences/reducer';
import endpointServer from './endpoint-server/reducer';
import endpointQuery from './endpoint-query/reducer';
import investigateQuery from './investigate-query/reducer';

export default combineReducers({
  files: combineReducers({
    fileList,
    schema,
    filter
  }),
  preferences,
  endpointServer,
  endpointQuery,
  investigateQuery
});
