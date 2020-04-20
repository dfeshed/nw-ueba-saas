import { combineReducers } from 'redux';
import recon from 'recon/reducers/index';
import context from 'context/reducers';

export default combineReducers({
  ...recon,
  context
});
