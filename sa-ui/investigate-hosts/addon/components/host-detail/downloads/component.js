import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
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
import { toggleHostDetailsFilter } from 'investigate-hosts/actions/ui-state-creators';

const stateToComputed = (state) => ({
  filter: state.endpoint.hostDownloads.filter,
  selectedFilterId: selectedFilterId(state.endpoint.hostDownloads),
  savedFilter: savedFilter(state.endpoint.hostDownloads),
  hostDownloadsFilters: state.endpoint.hostDownloads.filter.savedFilterList,
  selectedFileList: state.endpoint.hostDownloads.downloads.selectedFileList,
  isShowHostDetailsFilterPanel: state.endpoint.visuals.showHostDetailsFilter
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
  saveLocalMFTCopy,
  toggleHostDetailsFilter
};

@classic
@tagName('box')
@classNames('host-downloads')
class HostDownloads extends Component {
  filterTypes = FILTER_TYPES;
  showConfirmationModal = false;

  @service
  accessControl;

  @computed('selectedFileList')
  get disableActions() {
    const isErrorFiles = this.selectedFileList.some((item) => {
      return item.status === 'Processing' || item.status === 'Error';
    });
    const hasManageAccess = this.get('accessControl.endpointCanManageFiles');
    return {
      hasManageAccess,
      deleteFile: !this.selectedFileList.length,
      saveLocalCopy: this.selectedFileList.length !== 1 || !!isErrorFiles
    };
  }

  @action
  onDeleteFilesFromServer() {
    const callbacks = {
      onSuccess: () => success('investigateHosts.downloads.deleteDownloadedFiles.success'),
      onFailure: (message) => failure(message, null, false)
    };
    const selectedFileList = this.get('selectedFileList');
    this.send('deleteSelectedFiles', selectedFileList, callbacks);
    this.set('showConfirmationModal', false);
  }

  @action
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
  }

  @action
  onShowConfirmationModal() {
    this.set('showConfirmationModal', true);
  }

  @action
  hideConfirmationModal() {
    this.set('showConfirmationModal', false);
  }

  @action
  onCloseSidePanel(side) {
    if (side === 'left') {
      this.send('toggleHostDetailsFilter', false);
    }
  }
}

export default connect(stateToComputed, dispatchToActions)(HostDownloads);
