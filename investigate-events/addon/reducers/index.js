import { combineReducers } from 'redux';

import data from './data-reducer';
import dictionaries from './dictionaries/reducer';
import services from './services/reducer';

export default combineReducers({
  data,
  dictionaries,
  services
});
