import { combineReducers } from 'redux';
import schema from './schema/reducer';
import fileList from './file-list/reducer';
import filter from './file-filter/reducer';
import preferences from './preferences/reducer';
import endpointServer from './endpoint-server/reducer';

export default combineReducers({
  files: combineReducers({
    fileList,
    schema,
    filter
  }),
  preferences,
  endpointServer
});
