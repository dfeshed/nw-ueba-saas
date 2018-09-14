import { combineReducers } from 'redux';
import groups from './groups-reducers';
import policies from './policies-reducers';
import policy from './policy-reducers';
import policyWizard from './policy-wizard-reducers';
import groupWizard from './group-wizard-reducers';

export default combineReducers({
  groups,
  policies,
  policy,
  policyWizard,
  groupWizard
});
