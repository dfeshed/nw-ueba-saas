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
import { getSubDirectories, mftFilterVisible, setSelectDirectoryForDetails } from 'investigate-hosts/actions/data-creators/downloads';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import { FILTER_TYPES } from './filter-types';

import { listOfMftFiles } from 'investigate-hosts/reducers/details/mft-directory/selectors';

const stateToComputed = (state) => ({
  selectedMftName: state.endpoint.hostDownloads.downloads.selectedMftName,
  filter: state.endpoint.hostDownloads.mft.filter,
  selectedFilterId: selectedFilterId(state.endpoint.hostDownloads.mft),
  savedFilter: savedFilter(state.endpoint.hostDownloads.mft),
  mftFilters: state.endpoint.hostDownloads.mft.filter.savedFilterList,
  isShowMftHelp: !state.endpoint.hostDownloads.mft.mftDirectory.selectedDirectoryForDetails,
  mftFiles: listOfMftFiles(state),
  fileSource: state.endpoint.hostDownloads.mft.mftDirectory.fileSource,
  selectedDirectoryForDetails: state.endpoint.hostDownloads.mft.mftDirectory.selectedDirectoryForDetails,
  inUse: state.endpoint.hostDownloads.mft.mftDirectory.inUse
});

const dispatchToActions = {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters,
  getSubDirectories,
  mftFilterVisible,
  setSelectDirectoryForDetails
};

const mftContainer = Component.extend({
  tagName: 'box',
  classNames: ['mft-container'],
  accessControl: service(),
  filterTypes: FILTER_TYPES,

  init() {
    this._super(...arguments);
    this.send('resetFilters', 'MFTDIRECTORY');
  },

  actions: {
    onOpen(side) {
      if (side === 'left') {
        this.send('mftFilterVisible', false);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(mftContainer);
