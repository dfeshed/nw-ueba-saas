import redux from 'redux';
import schema from './schema/reducer';
import fileList from './file-list/reducer';
import filter from './filter/reducer';

export default {
  files: redux.combineReducers({
    fileList,
    schema,
    filter
  })
};
