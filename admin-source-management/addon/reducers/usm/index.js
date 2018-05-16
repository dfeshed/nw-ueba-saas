import { combineReducers } from 'redux';
import groups from './groups-reducers';
import group from './group-reducers';

export default combineReducers({
  groups,
  group
});
