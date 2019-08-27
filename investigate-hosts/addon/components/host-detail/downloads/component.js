import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters
} from 'investigate-shared/actions/data-creators/filter-creators';
import { saveLocalFileCopy } from 'investigate-shared/actions/data-creators/file-analysis-creators';
import { getFirstPageOfDownloads, deleteSelectedFiles, saveLocalMFTCopy } from 'investigate-hosts/actions/data-creators/downloads';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import { FILTER_TYPES } from './filter-types';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  filter: state.endpoint.hostDownloads.filter,
  selectedFilterId: selectedFilterId(state.endpoint.hostDownloads),
  savedFilter: savedFilter(state.endpoint.hostDownloads),
  hostDownloadsFilters: state.endpoint.hostDownloads.filter.savedFilterList,
  selectedFileList: state.endpoint.hostDownloads.downloads.selectedFileList
});

const dispatchToActions = {
  applyFilters,
  createCustomSearch,
  applySavedFilters,
  deleteFilter,
  resetFilters,
  getFirstPageOfDownloads,
  deleteSelectedFiles,
  saveLocalFileCopy,
  saveLocalMFTCopy
};

const HostDownloads = Component.extend({
  tagName: 'box',
  classNames: ['host-downloads'],

  filterTypes: FILTER_TYPES,

  showConfirmationModal: false,

  accessControl: service(),

  @computed('selectedFileList')
  disableActions(selectedFileList) {
    const isErrorFiles = selectedFileList.some((item) => {
      return item.status === 'Processing' || item.status === 'Error';
    });
    const hasManageAccess = this.get('accessControl.endpointCanManageFiles');
    return {
      hasManageAccess,
      deleteFile: !selectedFileList.length,
      saveLocalCopy: selectedFileList.length !== 1 || !!isErrorFiles
    };
  },

  actions: {
    onDeleteFilesFromServer() {
      const callbacks = {
        onSuccess: () => success('investigateHosts.downloads.deleteDownloadedFiles.success'),
        onFailure: (message) => failure(message, null, false)
      };
      const selectedFileList = this.get('selectedFileList');
      this.send('deleteSelectedFiles', selectedFileList, callbacks);
      this.set('showConfirmationModal', false);
    },

    onSaveLocalCopy() {
      const callbacks = {
        onFailure: (message) => failure(message, null, false)
      };
      const selectedFileList = this.get('selectedFileList') || [];
      const [selectedFile] = selectedFileList;
      const { serviceId, fileType } = selectedFile;
      if (fileType === 'File') {
        this.send('saveLocalFileCopy', selectedFile, callbacks);
      } else {
        this.send('saveLocalMFTCopy', selectedFile, callbacks, serviceId);
      }
    },

    showConfirmationModal() {
      this.set('showConfirmationModal', true);
    },

    hideConfirmationModal() {
      this.set('showConfirmationModal', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(HostDownloads);
