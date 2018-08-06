import { combineReducers } from 'redux';
import groups from './groups-reducers';
import group from './group-reducers';
import policies from './policies-reducers';
import policy from './policy-reducers';

export default combineReducers({
  groups,
  group,
  policies,
  policy
});
