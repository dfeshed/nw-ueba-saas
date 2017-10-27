import { combineReducers } from 'redux';
import preferences from './preferences';
import recon from 'recon/reducers';

export default combineReducers({
  preferences,
  ...recon
});
