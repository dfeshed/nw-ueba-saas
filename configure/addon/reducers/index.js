import { combineReducers } from 'redux';
import respond from './respond';
import content from './content';
const configure = combineReducers({
  respond,
  content
});

export default combineReducers({
  configure
});
