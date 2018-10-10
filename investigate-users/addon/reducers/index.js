import { combineReducers } from 'redux';
import tabs from './tabs/reducer';
import users from './users/reducer';
import alerts from './alerts/reducer';

export default combineReducers({
  tabs,
  users,
  alerts
});