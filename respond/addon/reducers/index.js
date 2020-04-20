import { combineReducers } from 'redux';
import respond from './respond/index';
import recon from 'recon/reducers';
import respondShared from 'respond-shared/reducers';

export default combineReducers({
  respond,
  ...recon,
  ...respondShared
});
