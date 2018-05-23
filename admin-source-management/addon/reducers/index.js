import { combineReducers } from 'redux';
import usm from './usm/index';
import policy from './policy/reducer';

export default combineReducers({
  usm,
  policy
});
