import { combineReducers } from 'redux';
import { createFilteredReducer } from 'component-lib/utils/reducer-wrapper';
import filters from './filters/filters-reducers';
import groups from './groups-reducers';
import groupWizard from './group-wizard-reducers';
import policies from './policies-reducers';
import policy from './policy-reducers'; // TODO delete
import policyWizard from './policy-wizard/policy-wizard-reducers';

const reducerPredicate = (type) => {
  return (action) => {
    return action.meta && action.meta.belongsTo === type;
  };
};

export default combineReducers({
  groups,
  policies,
  policiesFilter: createFilteredReducer(filters, reducerPredicate('POLICIES')),
  policy,
  policyWizard,
  groupWizard
});
