import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { observes } from '@ember-decorators/object';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { updateRowVisibility } from './utils';
import {
  processTree,
  areAllSelected,
  selectedFileChecksums,
  processList,
  updateProcessColumns,
  schema,
  savedProcessColumns
} from 'investigate-hosts/reducers/details/process/selectors';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { failure } from 'investigate-shared/utils/flash-messages';
import { serviceList, isInsightsAgent } from 'investigate-hosts/reducers/hosts/selectors';
import { machineOsType, hostName, isAgentMigrated } from 'investigate-hosts/reducers/details/overview/selectors';
import { fileStatus, isRemediationAllowed } from 'investigate-hosts/reducers/details/file-context/selectors';
import { buildTimeRange } from 'investigate-shared/utils/time-util';
import { once, next } from '@ember/runloop';
import { saveColumnConfig } from 'investigate-hosts/actions/data-creators/host';
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
  retrieveRemediationStatus
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
  saveColumnConfig
};


const stateToComputed = (state) => ({
  isTreeView: state.endpoint.visuals.isTreeView,
  isProcessTreeLoading: state.endpoint.process.isProcessTreeLoading,
  selectedRowIndex: state.endpoint.process.selectedRowIndex,
  agentId: state.endpoint.detailsInput.agentId,
  selectedProcessList: state.endpoint.process.selectedProcessList,
  selectedProcessId: state.endpoint.process.selectedProcessId,
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
  agentCountMapping: state.endpoint.process.agentCountMapping,
  sortField: state.endpoint.process.sortField,
  serverId: state.endpointQuery.serverId,
  isInsightsAgent: isInsightsAgent(state),
  savedProcessColumns: savedProcessColumns(state),
  defaultSchema: schema(state),
  isAgentMigrated: isAgentMigrated(state)
});


@classic
@tagName('box')
@classNames('rsa-process-tree')
class TreeComponent extends Component {
  @service
  accessControl;

  @service
  pivot;

  @service
  timezone;

  showServiceModal = false;
  showResetScoreModal = false;
  showFileStatusModal = false;
  contextItems = null;

  @computed('treeAsList.@each.visible', 'isTreeView', 'processList')
  get visibleItems() {
    if (this.isTreeView) {
      return this.get('treeAsList').filterBy('visible', true);
    } else {
      return this.processList;
    }
  }

  @computed('defaultSchema', 'savedProcessColumns')
  get columns() {
    return updateProcessColumns(this.savedProcessColumns, this.defaultSchema);
  }

  /**
   * We are using observer here because we need to close the property panel when snapshot changes, snapshot is outside
   * of the this component
   */
  @observes('isProcessTreeLoading', 'sortField', 'visibleItems.[]')
  _loadingStatus() {
    once(this, 'closePanel');
  }

  closePanel() {
    if (!this.get('isDestroyed') && !this.get('isDestroying') && this.closePropertyPanel) {
      this.send('deSelectAllProcess');
      this.closePropertyPanel();
    }
  }

  /**
   * Observer to dispatch getProcessdetails action when navigate to Process tab using explore
   * This is used to make a web socket call to get the first process details after filtering the process in the selector
   * @public
   */

  @observes('treeAsList')
  loadExploredProcessDetails() {
    const treeList = this.get('treeAsList') || [];
    if (treeList.length) {
      this.send('getProcessDetails', treeList[0].pid);
    }
  }

  _isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('pid', item.pid);
    }
    return selected;
  }

  @action
  navigateToProcessAnalysis(item) {
    const { zoneId } = this.get('timezone.selected');
    const {
      agentId,
      osType,
      hostName,
      timeRange,
      serviceId,
      serverId
    } = this.getProperties('agentId', 'osType', 'hostName', 'timeRange', 'serviceId', 'serverId');
    const { name, checksumSha256, vpid } = item;
    const { value, unit } = timeRange;
    const range = buildTimeRange(value, unit, zoneId);
    const timeStr = `st=${range.startTime}&et=${range.endTime}`;
    const osTypeParam = `osType=${osType}&vid=${vpid}`;
    const queryParams = `checksum=${checksumSha256}&sid=${serviceId}&aid=${agentId}&hn=${hostName}&pn=${name}&${timeStr}&${osTypeParam}&serverId=${serverId}`;
    window.open(`${window.location.origin}/investigate/process-analysis?${queryParams}`, '_blank', 'width=1440,height=900');
  }

  @action
  sort(column) {
    const { field: sortField, isDescending: isDescOrder } = column;
    this.send('sortBy', sortField, !isDescOrder);
    column.set('isDescending', !isDescOrder);
  }

  @action
  handleToggleExpand(index, level, item) {
    const rows = this.get('treeAsList');
    const { pid, expanded } = item;
    updateRowVisibility(rows, pid, expanded);
  }

  /**
   * Handle for the row click action
   * @param item
   * @param index
   * @public
   */
  @action
  handleRowClickAction(item, index, e) {
    const { pid, checksumSha256 } = item;
    const { target: { classList } } = e;
    const machineName = this.get('hostName');
    // If it's machine name click don't select the row
    if (e.target.tagName.toLowerCase() === 'a' || e.target.parentElement.tagName.toLowerCase() === 'a') {
      return;
    }
    // do not select row when checkbox is clicked
    if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
      if (this.get('selectedRowIndex') !== index) {
        this.send('setRowIndex', index);
        // if clicked row is one among the checkbox selected list, row click will highlight that row keeping others
        // checkbox selected.
        // when a row not in the checkbox selected list is clicked, other checkboxes are cleared.
        if (!this._isAlreadySelected(this.get('selectedProcessList'), item)) {
          this.send('deSelectAllProcess');
          this.send('toggleProcessSelection', { ...item, machineName });
        }

        if (this.openPropertyPanel) {
          next(() => {
            this.openPropertyPanel();
          });
        }
        this.send('onProcessSelection', pid, checksumSha256);
      } else {
        this.send('toggleProcessSelection', { ...item, machineName }); // Adding machine name to item, as it's not there
        this.send('setRowIndex', null);
        if (this.closePropertyPanel) {
          this.closePropertyPanel();
        }
      }
    }
  }

  @action
  beforeContextMenuShow(menu, event) {
    const { contextSelection: item, contextItems } = menu;
    const machineName = this.get('hostName');

    if (contextItems.length) {
      // Need to store this locally set it back again to menu object
      this.set('contextItems', contextItems);
    }
    // For anchor tag hide the context menu and show browser default right click menu
    if (event.target.tagName.toLowerCase() === 'a' || event.target.parentElement.tagName.toLowerCase() === 'a') {
      menu.set('contextItems', []);
    } else {
      menu.set('contextItems', this.get('contextItems'));

      // Highlight is removed and right panel is closed when right clicked on non-highlighted row
      if (this.get('selectedProcessId') !== item.pid) {
        this.closePropertyPanel();
        this.send('setRowIndex', null);
      }
      this.set('itemList', [item]);
      if (!this._isAlreadySelected(this.get('selectedProcessList'), item)) {
        this.send('deSelectAllProcess');
        this.send('toggleProcessSelection', { ...item, machineName });
      }
      const selections = this.get('selectedProcessList');
      if (selections && selections.length === 1) {
        this.send('getFileContextFileStatus', 'PROCESS', selections);
      }
    }
  }

  @action
  pivotToInvestigate(item, category) {
    const machineName = this.get('hostName');
    this.get('pivot').pivotToInvestigate('checksumSha256', { ...item, machineName }, category);
  }

  @action
  onCloseServiceModal() {
    this.set('showServiceModal', false);
  }

  @action
  showEditFileStatus(item) {
    this.set('itemList', [item]);
    if (this.get('accessControl.endpointCanManageFiles')) {
      this.set('showFileStatusModal', true);
    } else {
      failure('investigateFiles.noManagePermissions');
    }
  }

  @action
  onCloseEditFileStatus() {
    this.set('showFileStatusModal', false);
  }

  @action
  showRiskScoreModal(fileList) {
    this.set('selectedFiles', fileList);
    this.set('showResetScoreModal', true);
  }

  @action
  onResetScoreModalClose() {
    this.set('showResetScoreModal', false);
  }

  /**
   * Abort the action if dragged column is machine name, risk score and checkbox also abort if column in dropped to
   * machine name, risk score and checkbox.
   *
   */
  @action
  onReorderColumns(columns, newColumns, column, fromIndex, toIndex) {
    return !(column.dataType === 'checkbox' ||
      column.field === 'name' ||
      column.field === 'machineFileScore' ||
      column.field === 'fileProperties.score' ||
      toIndex === 0 ||
      toIndex === 1 ||
      toIndex === 2);

  }

  @action
  onColumnConfigChange(changedProperty, changedColumns) {
    this.send('saveColumnConfig', 'hosts-process-tree', changedProperty, changedColumns);
  }
}

export default connect(stateToComputed, dispatchToActions)(TreeComponent);
