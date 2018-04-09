import { combineReducers } from 'redux';
import respond from './respond';
import hostsScan from 'hosts-scan-configure/reducers/hosts-scan/reducer';
import logs from './logs';
const configure = combineReducers({
  respond,
  logs
});

export default combineReducers({
  configure,
  hostsScan
});
