import { combineReducers } from 'redux';
import investigate from './investigate';
import recon from 'recon/reducers';

export default combineReducers({
  investigate,
  ...recon
});
