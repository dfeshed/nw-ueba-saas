import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';
import { navigateToInvestigateEventsAnalysis } from 'investigate-shared/utils/pivot-util';
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
  resetSelection
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
  sid: state.endpointQuery.serverId
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
  resetRiskScore
};

const FileContextTable = Component.extend({

  tagName: 'box',

  accessControl: service(),

  timezone: service(),

  classNames: ['file-context-table', 'host-detail__datatable'],

  customSort: null,

  showServiceModal: false,

  showFileStatusModal: false,

  showResetScoreModal: false,

  selectedIndex: 0,

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

    onRowClick(item, index, e, table) {
      const { target: { classList } } = e;
      if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
        if (this.get('selectedRowId') !== index) {
          table.set('selectedIndex', index);
          this.send('onHostFileSelection', this.get('tabName'), this.get('storeName'), item);
        } else {
          table.set('selectedIndex', -1);
          this.send('onHostFileSelection', this.get('tabName'), this.get('storeName'), { id: null });
        }
      }
    },

    beforeContextMenuShow({ contextSelection: item }) {
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
    },

    showServiceList(item, category) {
      const serviceId = this.get('serviceId');
      this.set('itemList', [item]);
      if (serviceId && serviceId !== '-1') {
        const {
          timeRange,
          agentId
        } = this.getProperties('timeRange', 'agentId');

        const { zoneId } = this.get('timezone.selected');
        const additionalFilter = `agent.id="${agentId}" && category="${category}"`;
        navigateToInvestigateEventsAnalysis({ metaName: 'checksumSha256', metaValue: null, itemList: [item], additionalFilter }, serviceId, timeRange, zoneId);
      } else {
        this.set('showServiceModal', true);
      }
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
      const callBackOptions = {
        onSuccess: () => {
          success('investigateFiles.riskScore.success');
        },
        onFailure: () => failure('investigateFiles.riskScore.error')
      };
      this.set('showResetScoreModal', false);
      this.send('resetRiskScore', this.get('selectedFiles'), callBackOptions);
      this.set('selectedFiles', null);
    },

    onResetScoreModalClose() {
      this.set('showResetScoreModal', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FileContextTable);
