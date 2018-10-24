import { combineReducers } from 'redux';
import groups from './groups-reducers';
import groupWizard from './group-wizard-reducers';
import policies from './policies-reducers';
import policy from './policy-reducers'; // TODO delete
import policyWizard from './policy-wizard/policy-wizard-reducers';

export default combineReducers({
  groups,
  policies,
  policy,
  policyWizard,
  groupWizard
});
