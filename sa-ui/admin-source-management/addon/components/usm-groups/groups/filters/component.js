import Component from '@ember/component';
import { connect } from 'ember-redux';
import { filterTypesConfig } from 'admin-source-management/reducers/usm/groups-selectors';
import { fetchGroups } from 'admin-source-management/actions/creators/groups-creators';
// *** savedFilter will not work as is since USM needs more than one filter instance...
//     if/when we start saving filters we'll have to have policy/group/somethingElse specific selectors,
//     OR maybe second savedFilter that pulls directly from Redux state instead of component state
// import { savedFilter } from 'admin-source-management/reducers/usm/filters/filters-selectors';
import {
  applyFilters,
  resetFilters
} from 'admin-source-management/actions/creators/filters-creators';

const stateToComputed = (state) => ({
  filterState: state.usm.groupsFilter,
  filterTypes: filterTypesConfig(state)
});

const dispatchToActions = {
  fetchGroups,
  applyFilters,
  resetFilters
};

const UsmGroupsFilter = Component.extend({
  tagName: 'box',
  classNames: ['usm-groups-filter']
});

export default connect(stateToComputed, dispatchToActions)(UsmGroupsFilter);