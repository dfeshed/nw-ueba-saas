import { combineReducers } from 'redux';
import investigate from './investigate';
import recon from 'recon/reducers';
import preferences from 'preferences/reducers';
import context from 'context/reducers';
import respondShared from 'respond-shared/reducers';

export default combineReducers({
  investigate,
  context,
  ...recon,
  preferences,
  ...respondShared
});
