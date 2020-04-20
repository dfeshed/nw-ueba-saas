import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fetchPolicies } from 'admin-source-management/actions/creators/policies-creators';
// *** savedFilter will not work as is since USM needs more than one filter instance...
//     if/when we start saving filters we'll have to have policy/group/somethingElse specific selectors,
//     OR maybe second savedFilter that pulls directly from Redux state instead of component state
// import { savedFilter } from 'admin-source-management/reducers/usm/filters/filters-selectors';
import {
  applyFilters,
  resetFilters
} from 'admin-source-management/actions/creators/filters-creators';
import { filterTypesConfig } from 'admin-source-management/reducers/usm/policies-selectors';

const stateToComputed = (state) => ({
  filterState: state.usm.policiesFilter,
  // TODO move to a selector next sprint when filtersTypes need to be dynamic
  filterTypes: filterTypesConfig(state)
});

const dispatchToActions = {
  fetchPolicies,
  applyFilters,
  resetFilters
};

const UsmPoliciesFilter = Component.extend({
  tagName: 'box',
  classNames: ['usm-policies-filter']
});

export default connect(stateToComputed, dispatchToActions)(UsmPoliciesFilter);