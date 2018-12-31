import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { observer } from '@ember/object';
import TREE_CONFIG from './process-config';
import LIST_CONFIG from './process-list-config';
import { connect } from 'ember-redux';
import { updateRowVisibility } from './utils';
import { processTree, areAllSelected, selectedFileChecksums, processList } from 'investigate-hosts/reducers/details/process/selectors';
import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';
import { inject as service } from '@ember/service';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { machineOsType, hostName } from 'investigate-hosts/reducers/details/overview/selectors';
import { fileStatus, isRemediationAllowed } from 'investigate-hosts/reducers/details/file-context/selectors';

import {
  setRowIndex,
  getProcessDetails,
  onProcessSelection,
  toggleProcessSelection,
  selectAllProcess,
  sortBy,
  deSelectAllProcess
} from 'investigate-hosts/actions/data-creators/process';

import {
  getFileContextFileStatus,
  setFileContextFileStatus,
  retrieveRemediationStatus,
  downloadFilesToServer
} from 'investigate-hosts/actions/data-creators/file-context';


const dispatchToActions = {
  sortBy,
  getProcessDetails,
  onProcessSelection,
  toggleProcessSelection,
  selectAllProcess,
  deSelectAllProcess,
  setRowIndex,
  getFileContextFileStatus,
  setFileContextFileStatus,
  retrieveRemediationStatus,
  resetRiskScore,
  downloadFilesToServer
};


const stateToComputed = (state) => ({
  isTreeView: state.endpoint.visuals.isTreeView,
  isProcessTreeLoading: state.endpoint.process.isProcessTreeLoading,
  selectedRowIndex: state.endpoint.process.selectedRowIndex,
  agentId: state.endpoint.detailsInput.agentId,
  selectedProcessList: state.endpoint.process.selectedProcessList,
  isProcessDetailsView: state.endpoint.process.isProcessDetailsView,
  restrictedFileList: state.fileStatus.restrictedFileList,
  processList: processList(state),
  serviceList: serviceList(state),
  treeAsList: processTree(state),
  osType: machineOsType(state),
  hostName: hostName(state),
  areAllSelected: areAllSelected(state),
  fileStatus: fileStatus(state, 'processes'),
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  isRemediationAllowed: isRemediationAllowed(state, 'processes'),
  selectedFileChecksums: selectedFileChecksums(state),
  agentCountMapping: state.endpoint.process.agentCountMapping
});


const TreeComponent = Component.extend({

  tagName: 'box',

  classNames: ['rsa-process-tree'],

  accessControl: service(),

  pivot: service(),

  showServiceModal: false,

  showResetScoreModal: false,

  showFileStatusModal: false,

  contextItems: null,

  @computed('isTreeView')
  columnsConfig(isTreeView) {
    if (isTreeView) {
      return TREE_CONFIG;
    } else {
      return LIST_CONFIG;
    }
  },

  /**
   * Filtering the the items based on visible property, hiding the virtual child element based the parent expanded or not
   * @param items
   * @public
   */
  @computed('treeAsList.@each.visible', 'isTreeView', 'processList')
  visibleItems(items, isTreeView, processList) {
    if (isTreeView) {
      return this.get('treeAsList').filterBy('visible', true);
    } else {
      return processList;
    }
  },

  /**
   * Observer to dispatch getProcessdetails action when navigate to Process tab using explore
   * This is used to make a web socket call to get the first process details after filtering the process in the selector
   * @public
   */

  loadExploredProcessDetails: observer('treeAsList', function() {
    const treeList = this.get('treeAsList') || [];
    if (treeList.length) {
      this.send('getProcessDetails', treeList[0].pid);
    }
  }),

  _isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('pid', item.pid);
    }
    return selected;
  },


  actions: {

    sort(column) {
      const { field: sortField, isDescending: isDescOrder } = column;
      this.send('sortBy', sortField, !isDescOrder);
      column.set('isDescending', !isDescOrder);
    },


    handleToggleExpand(index, level, item) {
      const rows = this.get('treeAsList');
      const { pid, expanded } = item;
      updateRowVisibility(rows, pid, expanded);
    },

    /**
     * Handle for the row click action
     * @param item
     * @param index
     * @public
     */
    handleRowClickAction(item, index) {
      const { pid, checksumSha256 } = item;
      if (this.get('selectedRowIndex') !== index) {
        this.send('setRowIndex', index);
        if (this.openPropertyPanel) {
          this.openPropertyPanel();
        }
        this.send('onProcessSelection', pid, checksumSha256);
      } else {
        this.send('setRowIndex', null);
        if (this.closePropertyPanel) {
          this.closePropertyPanel();
        }
      }
    },

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

        this.set('itemList', [item]);
        if (!this._isAlreadySelected(this.get('selectedProcessList'), item)) {
          this.send('deSelectAllProcess');
          this.send('toggleProcessSelection', item);
        }
        const selections = this.get('selectedProcessList');
        if (selections && selections.length === 1) {
          this.send('getFileContextFileStatus', 'PROCESS', selections);
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
export default connect(stateToComputed, dispatchToActions)(TreeComponent);
