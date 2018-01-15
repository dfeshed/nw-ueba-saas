import { combineReducers } from 'redux';
import dictionaries from './dictionaries/reducer';
import users from './users/reducer';
import incidentRules from './incident-rules/reducer';
import incidentRule from './incident-rules/rule/reducer';
import notifications from './notifications/reducer';

export default combineReducers({
  dictionaries,
  incidentRule,
  incidentRules,
  notifications,
  users
});
