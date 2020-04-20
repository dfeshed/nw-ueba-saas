import { combineReducers } from 'redux';
import { createFilteredReducer } from 'component-lib/utils/reducer-wrapper';
import filters from './filters/filters-reducers';
import groups from './groups-reducers';
import groupWizard from './group-wizard-reducers';
import policies from './policies-reducers';
import policyWizard from './policy-wizard/policy-wizard-reducers';
import sources from './sources-reducers';
import sourceWizard from './source-wizard/source-wizard-reducers';

const reducerPredicate = (type) => {
  return (action) => {
    return action.meta && action.meta.belongsTo === type;
  };
};

export default combineReducers({
  groups,
  groupsFilter: createFilteredReducer(filters, reducerPredicate('GROUPS')),
  policies,
  policiesFilter: createFilteredReducer(filters, reducerPredicate('POLICIES')),
  policyWizard,
  groupWizard,
  sources,
  sourcesFilter: createFilteredReducer(filters, reducerPredicate('SOURCES')),
  sourceWizard
});
