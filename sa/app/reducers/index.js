import { combineReducers } from 'redux';
import preferences from './preferences';
import context from 'context/reducers';
import recon from 'recon/reducers';

export default combineReducers({
  preferences,
  context,
  ...recon
});
