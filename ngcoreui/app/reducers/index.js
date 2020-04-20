import { combineReducers } from 'redux';
import shared from './shared-reducers';
import logcollector from './logcollector/index';

export default combineReducers({
  shared,
  logcollector
});
