import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';
import {
  serviceList,
  isAllSelected,
  files,
  checksums,
  areFilesLoading,
  nextLoadCount,
  isAnyFileFloatingOrMemoryDll,
  fileDownloadButtonStatus,
  downloadLink,
  isCertificateViewDisabled
} from 'investigate-files/reducers/file-list/selectors';
import { columns } from 'investigate-files/reducers/schema/selectors';
import {
  sortBy,
  getPageOfFiles,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  getAllServices,
  saveFileStatus,
  getSavedFileStatus,
  retrieveRemediationStatus,
  onFileSelection,
  setSelectedIndex,
  saveColumnConfig
} from 'investigate-files/actions/data-creators';
import { toggleCertificateView } from 'investigate-files/actions/certificate-data-creators';

import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';
import { timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';

const stateToComputed = (state) => ({
  areFilesLoading: areFilesLoading(state),
  serviceList: serviceList(state),
  loadMoreStatus: state.files.fileList.loadMoreStatus,
  files: files(state), // All visible files
  totalItems: state.files.fileList.totalItems,
  sortField: state.files.fileList.sortField, // Currently applied sort on file list
  isSortDescending: state.files.fileList.isSortDescending,
  isAllSelected: isAllSelected(state),
  selections: state.files.fileList.selectedFileList,
  selectedFile: state.files.fileList.selectedFile,
  checksums: checksums(state),
  agentCountMapping: state.files.fileList.agentCountMapping,
  fileStatusData: state.files.fileList.fileStatusData,
  remediationStatus: state.files.fileList.isRemediationAllowed,
  restrictedFileList: state.fileStatus.restrictedFileList,
  timeRange: timeRange(state),
  isCertificateView: state.certificate.list.isCertificateView,
  selectedIndex: state.files.fileList.selectedIndex,
  serverId: state.endpointQuery.serverId,
  servers: state.endpointServer.serviceData,
  nextLoadCount: nextLoadCount(state),
  isFloatingOrMemoryDll: isAnyFileFloatingOrMemoryDll(state),
  fileDownloadButtonStatus: fileDownloadButtonStatus(state),
  downloadLink: downloadLink(state),
  isCertificateViewDisabled: isCertificateViewDisabled(state),
  columns: columns(state)
});

const dispatchToActions = {
  sortBy,
  getPageOfFiles,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  getAllServices,
  saveFileStatus,
  getSavedFileStatus,
  retrieveRemediationStatus,
  resetRiskScore,
  onFileSelection,
  setSelectedIndex,
  toggleCertificateView,
  saveColumnConfig
};

/**
 * File list component for displaying the list of files
 * @public
 */
@classic
@tagName('')
class FileList extends Component {
  @service
  accessControl;

  @service
  timezone;

  @service
  pivot;

  showServiceModal = false;
  showFileStatusModal = false;
  showResetScoreModal = false;
  selectedFiles = null;
  contextItems = null;

  @computed('isCertificateView')
  get showColumnChooser() {
    return !this.isCertificateView;
  }

  @computed('fileStatusData')
  get data() {
    return { ...this.fileStatusData };
  }

  @computed('selectedFiles')
  get isMaxResetRiskScoreLimit() {
    return this.selectedFiles.length > 100;
  }

  init() {
    super.init(...arguments);
    this.CONFIG_FIXED_COLUMNS = this.CONFIG_FIXED_COLUMNS || ['firstFileName', 'score'];
    this.itemList = this.itemList || [];
    next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('getAllServices');

      }
    });
  }

  isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('checksumSha256', item.checksumSha256);
    }
    return selected;
  }

  @action
  sortData(field, sortDirection) {
    if (this.closeRiskPanel) {
      this.closeRiskPanel();
    }
    next(() => {
      this.send('sortBy', field, sortDirection);
    });
  }

  @action
  toggleSelectedRow(item, index, e, table) {
    const { target: { classList } } = e;
    if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
      const isSameRowClicked = table.get('selectedIndex') === index;
      const openRiskPanel = this.get('openRiskPanel');
      this.send('setSelectedIndex', index);
      if (!isSameRowClicked && openRiskPanel) {
        // if clicked row is one among the checkbox selected list, row click will highlight that row keeping others
        // checkbox selected.
        // when a row not in the checkbox selected list is clicked, other checkboxes are cleared.
        if (!this.isAlreadySelected(this.get('selections'), item)) {
          this.send('deSelectAllFiles');
          this.send('toggleFileSelection', item);
        }
        this.send('onFileSelection', item);
        next(() => {
          this.openRiskPanel();
        });
      } else {
        this.send('toggleFileSelection', item);
        this.closeRiskPanel();
        this.send('setSelectedIndex', -1);
      }
    }
  }

  /**
   * Abort the action if dragged column is file name, risk score and checkbox also abort if column in dropped to
   * file name, risk score and checkbox.
   *
   */
  @action
  onReorderColumns(columns, newColumns, column, fromIndex, toIndex) {
    return !(column.dataType === 'checkbox' ||
      column.field === 'firstFileName' ||
      column.field === 'score' ||
      toIndex === 0 ||
      toIndex === 1 ||
      toIndex === 2);
  }

  @action
  onColumnConfigChange(changedProperty, changedColumns) {
    this.send('saveColumnConfig', changedProperty, changedColumns, 'files');
  }

  @action
  showRiskScoreModal(fileList) {
    this.set('selectedFiles', fileList);
    this.set('showResetScoreModal', true);
  }

  @action
  resetRiskScoreAction() {
    const limitedFiles = this.get('selectedFiles').slice(0, 100);
    const callBackOptions = {
      onSuccess: (response) => {
        const { data } = response;
        if (data === limitedFiles.length) {
          success('investigateFiles.riskScore.success');
        } else {
          warning('investigateFiles.riskScore.warning');
        }
      },
      onFailure: () => failure('investigateFiles.riskScore.error')
    };
    this.set('showResetScoreModal', false);
    this.send('resetRiskScore', limitedFiles, 'FILE', callBackOptions);
    this.set('selectedFiles', null);
  }

  @action
  toggleItemSelection(item) {
    this.send('toggleFileSelection', item);
  }

  @action
  toggleAllSelection() {
    if (!this.get('isAllSelected')) {
      this.send('selectAllFiles');
    } else {
      this.send('deSelectAllFiles');
    }
  }

  @action
  showEditFileStatus(item) {
    if (this.get('accessControl.endpointCanManageFiles')) {
      this.set('itemList', [item]);
      this.set('showFileStatusModal', true);
    } else {
      failure('investigateFiles.noManagePermissions');
    }
  }

  @action
  pivotToInvestigate(item, category) {
    this.get('pivot').pivotToInvestigate('checksumSha256', item, category);
  }

  @action
  certificateView(selections) {
    let selectedThumbprint = '';
    if (selections.length > 0) {
      const [{ signature: { thumbprint } }] = selections;
      selectedThumbprint = thumbprint;
    }
    this.get('navigateToCertificateView')(selectedThumbprint);
  }

  @action
  onCloseServiceModal() {
    this.set('showServiceModal', false);
  }

  @action
  onCloseEditFileStatus() {
    this.set('showFileStatusModal', false);
  }

  @action
  onResetScoreModalClose() {
    this.set('showResetScoreModal', false);
  }

  @action
  beforeContextMenuShow(menu, event) {
    const { contextSelection: item, contextItems } = menu;
    if (!this.get('contextItems')) {
      // Need to store this locally set it back again to menu object
      this.set('contextItems', contextItems);
    }
    // For anchor tag hid the context menu and show browser default right click menu
    if (event.target.tagName.toLowerCase() === 'a') {
      menu.set('contextItems', []);
    } else {
      menu.set('contextItems', this.get('contextItems'));
      // Highlight is removed and right panel is closed when right clicked on non-highlighted row
      if (this.get('selectedFile').id !== item.id) {
        this.send('setSelectedIndex', -1);
        this.closeRiskPanel();
      }
      this.set('itemList', [item]);
      if (!this.isAlreadySelected(this.get('selections'), item)) {
        this.send('deSelectAllFiles');
        this.send('toggleFileSelection', item);
      }
      const selections = this.get('selections');
      if (selections && selections.length === 1) {
        this.send('getSavedFileStatus', selections);
      }
    }

  }
}

export default connect(stateToComputed, dispatchToActions)(FileList);
