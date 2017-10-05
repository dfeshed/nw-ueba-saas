import { combineReducers } from 'redux';

import data from './data-reducer';
import dictionaries from './dictionaries/reducer';
import meta from './meta/reducer';
import queryNode from './queryNode/reducer';
import services from './services/reducer';
import recon from 'recon/reducers';

export default combineReducers({
  data,
  dictionaries,
  meta,
  queryNode,
  services,
  ...recon
});
