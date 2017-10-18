import { combineReducers } from 'redux';
import investigate from './investigate';
import recon from 'recon/reducers';
import preferences from 'preferences/reducers';

export default combineReducers({
  investigate,
  ...recon,
  ...preferences
});
