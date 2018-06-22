import { combineReducers } from 'redux';
import recon from 'recon/reducers';
import context from 'context/reducers';

export default combineReducers({
  ...recon,
  context
});