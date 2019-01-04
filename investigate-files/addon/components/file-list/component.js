import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';
import { inject as service } from '@ember/service';
import {
  serviceList,
  isAllSelected,
  files,
  checksums,
  areFilesLoading,
  nextLoadCount
} from 'investigate-files/reducers/file-list/selectors';
import { columns } from 'investigate-files/reducers/schema/selectors';
import computed from 'ember-computed-decorators';
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
  setSelectedIndex
} from 'investigate-files/actions/data-creators';
import { toggleCertificateView } from 'investigate-files/actions/certificate-data-creators';

import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';
import FIXED_COLUMNS from './file-list-fixed-columns';

const stateToComputed = (state) => ({
  areFilesLoading: areFilesLoading(state),
  serviceList: serviceList(state),
  columnConfig: columns(state),
  loadMoreStatus: state.files.fileList.loadMoreStatus,
  files: files(state), // All visible files
  totalItems: state.files.fileList.totalItems,
  sortField: state.files.fileList.sortField, // Currently applied sort on file list
  isSortDescending: state.files.fileList.isSortDescending,
  isAllSelected: isAllSelected(state),
  selections: state.files.fileList.selectedFileList,
  checksums: checksums(state),
  agentCountMapping: state.files.fileList.agentCountMapping,
  fileStatusData: state.files.fileList.fileStatusData,
  remediationStatus: state.files.fileList.isRemediationAllowed,
  restrictedFileList: state.fileStatus.restrictedFileList,
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  isCertificateView: state.certificate.list.isCertificateView,
  selectedIndex: state.files.fileList.selectedIndex,
  serverId: state.endpointQuery.serverId,
  servers: state.endpointServer.serviceData,
  nextLoadCount: nextLoadCount(state)
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
  toggleCertificateView
};

/**
 * File list component for displaying the list of files
 * @public
 */
const FileList = Component.extend({

  tagName: '',

  accessControl: service(),

  timezone: service(),

  pivot: service(),

  CONFIG_FIXED_COLUMNS: ['firstFileName', 'score'],

  showServiceModal: false,

  showFileStatusModal: false,

  showResetScoreModal: false,

  itemList: [],

  selectedFiles: null,

  contextItems: null,

  @computed('isCertificateView')
  showColumnChooser(isCertificateView) {
    return !isCertificateView;
  },

  @computed('columnConfig')
  updatedColumns(columns) {
    const UPDATED_COLUMNS = columns.filter((column) => !this.CONFIG_FIXED_COLUMNS.includes(column.field));
    return FIXED_COLUMNS.concat(UPDATED_COLUMNS);
  },

  @computed('fileStatusData')
  data(fileStatusData) {
    return { ...fileStatusData };
  },

  @computed('selectedFiles')
  isMaxResetRiskScoreLimit(selectedFiles) {
    return selectedFiles.length > 100;
  },

  init() {
    this._super(arguments);
    next(() => {
      if (!this.get('isDestroyed') && !this.get('isDestroying')) {
        this.send('getAllServices');
      }
    });
  },

  isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('checksumSha256', item.checksumSha256);
    }
    return selected;
  },


  actions: {
    toggleSelectedRow(item, index, e, table) {
      const { target: { classList } } = e;
      if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
        const isSameRowClicked = table.get('selectedIndex') === index;
        const openRiskPanel = this.get('openRiskPanel');
        this.send('setSelectedIndex', index);
        if (!isSameRowClicked && openRiskPanel) {
          this.send('deSelectAllFiles');
          this.send('toggleFileSelection', item);
          this.send('onFileSelection', item);
          next(() => {
            this.openRiskPanel();
          });
        } else {
          this.send('toggleFileSelection', item);
          this.closeRiskPanel();
          this.send('setSelectedIndex', null);
        }
      }
    },

    showRiskScoreModal(fileList) {
      this.set('selectedFiles', fileList);
      this.set('showResetScoreModal', true);
    },

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
    },

    toggleItemSelection(item) {
      this.send('toggleFileSelection', item);
    },

    toggleAllSelection() {
      if (!this.get('isAllSelected')) {
        this.send('selectAllFiles');
      } else {
        this.send('deSelectAllFiles');
      }
    },

    showEditFileStatus(item) {
      if (this.get('accessControl.endpointCanManageFiles')) {
        this.set('itemList', [item]);
        this.set('showFileStatusModal', true);
      } else {
        failure('investigateFiles.noManagePermissions');
      }
    },

    pivotToInvestigate(item, category) {
      this.get('pivot').pivotToInvestigate('checksumSha256', item, category);
    },

    onCloseServiceModal() {
      this.set('showServiceModal', false);
    },

    onCloseEditFileStatus() {
      this.set('showFileStatusModal', false);
    },

    onResetScoreModalClose() {
      this.set('showResetScoreModal', false);
    },

    beforeContextMenuShow(menu, event) {
      const { contextSelection: item, contextItems } = menu;
      this.send('setSelectedIndex', null);
      if (!this.get('contextItems')) {
        // Need to store this locally set it back again to menu object
        this.set('contextItems', contextItems);
      }
      // For anchor tag hid the context menu and show browser default right click menu
      if (event.target.tagName.toLowerCase() === 'a') {
        menu.set('contextItems', []);
      } else {
        menu.set('contextItems', this.get('contextItems'));
        this.closeRiskPanel();

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
});

export default connect(stateToComputed, dispatchToActions)(FileList);
