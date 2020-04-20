import { combineReducers } from 'redux';
import respond from './respond';
import content from './content';
import endpoint from './endpoint';
const configure = combineReducers({
  respond,
  content,
  endpoint
});

export default combineReducers({
  configure
});
