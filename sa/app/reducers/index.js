import { combineReducers } from 'redux';
import global from './global';
import context from 'context/reducers';
import recon from 'recon/reducers';

export default combineReducers({
  context,
  ...global,
  ...recon
});
