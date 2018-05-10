import { combineReducers } from 'redux';
import respond from './respond/index';
import recon from 'recon/reducers';

export default combineReducers({
  respond,
  ...recon
});
