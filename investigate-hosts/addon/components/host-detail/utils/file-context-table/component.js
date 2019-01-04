import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';
import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';

import {
  listOfFiles,
  fileContextSelections,
  isAllSelected,
  isDataLoading,
  selectedRowId,
  fileStatus,
  selectedFileChecksums,
  totalItems,
  contextLoadMoreStatus,
  isRemediationAllowed
} from 'investigate-hosts/reducers/details/file-context/selectors';

import {
  setFileContextSort,
  toggleRowSelection,
  toggleAllSelection,
  onHostFileSelection,
  getFileContextFileStatus,
  setFileContextFileStatus,
  retrieveRemediationStatus,
  resetSelection,
  deSelectAllSelection
} from 'investigate-hosts/actions/data-creators/file-context';

import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state, { storeName }) => ({
  agentId: state.endpoint.detailsInput.agentId,
  listOfFiles: listOfFiles(state, storeName),
  fileContextSelections: fileContextSelections(state, storeName),
  isAllSelected: isAllSelected(state, storeName),
  selectedRowId: selectedRowId(state, storeName),
  isDataLoading: isDataLoading(state, storeName),
  serviceList: serviceList(state, storeName),
  fileStatus: fileStatus(state, storeName),
  selectedFileChecksums: selectedFileChecksums(state, storeName),
  totalItems: totalItems(state, storeName),
  contextLoadMoreStatus: contextLoadMoreStatus(state, storeName),
  isRemediationAllowed: isRemediationAllowed(state, storeName),
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  restrictedFileList: state.fileStatus.restrictedFileList,
  sid: state.endpointQuery.serverId,
  agentCountMapping: state.endpoint[storeName].agentCountMapping,
  selectedRowIndex: state.endpoint[storeName].selectedRowIndex
});

const dispatchToActions = {
  setFileContextSort,
  toggleRowSelection,
  toggleAllSelection,
  onHostFileSelection,
  getFileContextFileStatus,
  setFileContextFileStatus,
  retrieveRemediationStatus,
  resetSelection,
  resetRiskScore,
  deSelectAllSelection
};

const FileContextTable = Component.extend({

  tagName: 'box',

  accessControl: service(),

  pivot: service(),

  classNames: ['file-context-table', 'host-detail__datatable'],

  customSort: null,

  showServiceModal: false,

  showFileStatusModal: false,

  showResetScoreModal: false,

  selectedIndex: 0,

  contextItems: null,

  _isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('checksumSha256', item.checksumSha256);
    }
    return selected;
  },

  actions: {

    sort(column) {
      column.set('isDescending', !column.isDescending);
      if (this.closePropertyPanel) {
        this.closePropertyPanel();
      }
      // resetting the selection on sort
      const tabName = this.get('tabName');
      this.send('resetSelection', tabName);

      const customSort = this.get('customSort');
      if (customSort) {
        this.customSort(column);
      } else {
        this.send('setFileContextSort', this.get('tabName'), {
          isDescending: column.isDescending,
          field: column.field
        });
      }
    },

    onRowClick(item, index, e) {
      const { target: { classList } } = e;
      const { tabName, storeName } = this.getProperties('tabName', 'storeName');
      if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
        if (this.get('selectedRowIndex') !== index) {
          this.send('deSelectAllSelection', tabName);
          this.send('toggleRowSelection', tabName, item);
          this.send('onHostFileSelection', tabName, storeName, item, index);
          if (this.openPropertyPanel) {
            this.openPropertyPanel();
          }
        } else {
          this.send('toggleRowSelection', tabName, item);
          this.send('onHostFileSelection', this.get('tabName'), this.get('storeName'), { id: null }, null);
          if (this.closePropertyPanel) {
            this.closePropertyPanel();
          }
        }
      }
    },

    beforeContextMenuShow(menu, event) {
      const { contextSelection: item, contextItems } = menu;
      if (!this.get('contextItems')) {
        // Need to store this locally set it back again to menu object
        this.set('contextItems', contextItems);
      }// For anchor tag hide the context menu and show browser default right click menu
      if (event.target.tagName.toLowerCase() === 'a' || event.target.parentElement.tagName.toLowerCase() === 'a') {
        menu.set('contextItems', []);
      } else {
        menu.set('contextItems', this.get('contextItems'));

        this.set('itemList', [item]);
        const tabName = this.get('tabName');
        if (!this._isAlreadySelected(this.get('fileContextSelections'), item)) {
          this.send('resetSelection', tabName);
          this.send('toggleRowSelection', tabName, item);
        }
        const selections = this.get('fileContextSelections');
        if (selections && selections.length === 1) {
          this.send('getFileContextFileStatus', tabName, selections);
        }
      }
    },

    pivotToInvestigate(item, category) {
      this.get('pivot').pivotToInvestigate('checksumSha256', item, category);
    },

    onCloseServiceModal() {
      this.set('showServiceModal', false);
    },


    showEditFileStatus(item) {
      this.set('itemList', [item]);
      if (this.get('accessControl.endpointCanManageFiles')) {
        this.set('showFileStatusModal', true);
      } else {
        failure('investigateFiles.noManagePermissions');
      }
    },

    onCloseEditFileStatus() {
      this.set('showFileStatusModal', false);
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

    onResetScoreModalClose() {
      this.set('showResetScoreModal', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FileContextTable);
