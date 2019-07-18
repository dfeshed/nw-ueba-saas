import Component from '@ember/component';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters
} from 'investigate-shared/actions/data-creators/filter-creators';
import { getSubDirectories } from 'investigate-hosts/actions/data-creators/downloads';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import { FILTER_TYPES } from './filter-types';


const stateToComputed = (state) => ({
  selectedMftName: state.endpoint.hostDownloads.downloads.selectedMftName,
  isShowMFTView: state.endpoint.hostDownloads.downloads.isShowMFTView,
  filter: state.endpoint.hostDownloads.mft.filter,
  selectedFilterId: selectedFilterId(state.endpoint.hostDownloads.mft),
  savedFilter: savedFilter(state.endpoint.hostDownloads.mft),
  mftFilters: state.endpoint.hostDownloads.mft.filter.savedFilterList
});

const dispatchToActions = {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters,
  getSubDirectories
};

const mftContainer = Component.extend({
  tagName: 'box',
  classNames: ['mft-container'],
  accessControl: service(),
  filterTypes: FILTER_TYPES
});

export default connect(stateToComputed, dispatchToActions)(mftContainer);