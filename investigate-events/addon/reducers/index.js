import { combineReducers } from 'redux';

import data from './data-reducer';
import dictionaries from './dictionaries/reducer';
import services from './services/reducer';
import recon from 'recon/reducers';

export default combineReducers({
  data,
  dictionaries,
  services,
  ...recon
});
