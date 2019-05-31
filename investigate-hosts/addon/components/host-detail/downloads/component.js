import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters
} from 'investigate-shared/actions/data-creators/filter-creators';
import { getFirstPageOfDownloads } from 'investigate-hosts/actions/data-creators/downloads';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import { FILTER_TYPES } from './filter-types';

const stateToComputed = (state) => ({
  filter: state.endpoint.hostDownloads.filter,
  selectedFilterId: selectedFilterId(state.endpoint.hostDownloads),
  savedFilter: savedFilter(state.endpoint.hostDownloads),
  hostDownloadsFilters: state.endpoint.hostDownloads.filter.savedFilterList
});

const dispatchToActions = {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters,
  getFirstPageOfDownloads
};

const HostDownloads = Component.extend({
  tagName: 'box',
  classNames: ['host-downloads'],

  filterTypes: FILTER_TYPES,

  actions: {
    onDeleteFilesFromServer() {
      // Placeholder
    },

    onSaveLocalCopy() {
      // placeholder
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(HostDownloads);
