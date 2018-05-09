import { combineReducers } from 'redux';
import respond from './respond';
import hostsScan from 'hosts-scan-configure/reducers/hosts-scan/reducer';
import content from './content';
const configure = combineReducers({
  respond,
  content
});

export default combineReducers({
  configure,
  hostsScan
});
