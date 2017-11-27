import { combineReducers } from 'redux';
import respond from './respond';
import hostsScan from 'hosts-scan-configure/reducers/hosts-scan/reducer';
const configure = combineReducers({
  respond
});

export default combineReducers({
  configure,
  hostsScan
});
