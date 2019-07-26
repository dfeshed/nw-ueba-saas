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
import { next } from '@ember/runloop';

const stateToComputed = (state) => ({
  selectedMftName: state.endpoint.hostDownloads.downloads.selectedMftName,
  isShowMFTView: state.endpoint.hostDownloads.downloads.isShowMFTView,
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
  actions: {
    onOpen(side) {
      if (side === 'left') {
        this.send('mftFilterVisible', false);
      }
    },
    onResetFilters(filterData) {
      const { fileSource, selectedDirectoryForDetails, inUse } = this.getProperties('fileSource', 'selectedDirectoryForDetails', 'inUse', 'isDirectories');

      this.send('setSelectDirectoryForDetails', {
        selectedDirectoryForDetails,
        fileSource,
        pageSize: 65000,
        isDirectories: true,
        inUse: true
      });
      this.send('resetFilters', filterData);
      next(() => {
        this.send('setSelectDirectoryForDetails', {
          selectedDirectoryForDetails,
          fileSource,
          pageSize: 100,
          isDirectories: false,
          inUse
        });
        this.send('getSubDirectories');
      });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(mftContainer);