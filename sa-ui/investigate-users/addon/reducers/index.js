import { combineReducers } from 'redux';
import tabs from './tabs/reducer';
import users from './users/reducer';
import alerts from './alerts/reducer';
import user from './user/reducer';

export default combineReducers({
  tabs,
  users,
  alerts,
  user
});