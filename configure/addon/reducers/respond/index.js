import { combineReducers } from 'redux';
import dictionaries from './dictionaries/reducer';
import users from './users/reducer';
import incidentRules from './incident-rules/reducer';
import incidentRule from './incident-rules/rule/reducer';
import notifications from './notifications/reducer';
import riskScoring from './risk-scoring/reducer';

export default combineReducers({
  dictionaries,
  incidentRule,
  incidentRules,
  notifications,
  riskScoring,
  users
});
