import { combineReducers } from 'redux';
import schema from './schema/reducer';
import fileList from './file-list/reducer';
import filter from './filter/reducer';
import preferences from 'preferences/reducers';

export default combineReducers({
  files: combineReducers({
    fileList,
    schema,
    filter
  }),
  preferences
});
