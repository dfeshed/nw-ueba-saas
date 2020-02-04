import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { observes } from '@ember-decorators/object';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';
import { serviceList, isInsightsAgent } from 'investigate-hosts/reducers/hosts/selectors';
import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';
import { once, next } from '@ember/runloop';
import { saveColumnConfig } from 'investigate-hosts/actions/data-creators/host';
import { savedColumnsConfig, updateConfig } from 'investigate-hosts/reducers/details/selectors';

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
  isRemediationAllowed,
  isAnyFileFloatingOrMemoryDll,
  isSelectedMachineServerId
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

const stateToComputed = (state, { storeName, tabName }) => ({
  agentId: state.endpoint.detailsInput.agentId,
  listOfFiles: listOfFiles(state, storeName),
  fileContextSelections: fileContextSelections(state, storeName),
  isAllSelected: isAllSelected(state, storeName),
  selectedRowId: selectedRowId(state, storeName),
  isDataLoading: isDataLoading(state, storeName) || false,
  serviceList: serviceList(state, storeName),
  fileStatus: fileStatus(state, storeName),
  selectedFileChecksums: selectedFileChecksums(state, storeName),
  totalItems: totalItems(state, storeName),
  contextLoadMoreStatus: contextLoadMoreStatus(state, storeName),
  isRemediationAllowed: isRemediationAllowed(state, storeName),
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  restrictedFileList: state.fileStatus.restrictedFileList,
  sid: isSelectedMachineServerId(state),
  agentCountMapping: state.endpoint[storeName].agentCountMapping,
  sortConfig: state.endpoint[storeName].sortConfig,
  selectedRowIndex: state.endpoint[storeName].selectedRowIndex,
  isFloatingOrMemoryDll: isAnyFileFloatingOrMemoryDll(state, storeName),
  isInsightsAgent: isInsightsAgent(state),
  savedColumns: savedColumnsConfig(state, tabName)
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
  deSelectAllSelection,
  saveColumnConfig
};

@classic
@tagName('box')
@classNames('file-context-table', 'host-detail__datatable')
class FileContextTable extends Component {
  @service
  accessControl;

  @service
  pivot;

  customSort = null;
  showServiceModal = false;
  showFileStatusModal = false;
  showResetScoreModal = false;
  selectedIndex = 0;
  contextItems = null;

  @computed('columnsConfig', 'savedColumns')
  get columns() {
    return updateConfig(this.columnsConfig, this.savedColumns);
  }

  _isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('id', item.id);
    }
    return selected;
  }

  /**
   * We are using observer here because we need to close the property panel when snapshot changes, snapshot is outside
   * of the this component
   */
  @observes('isDataLoading', 'sortConfig', 'listOfFiles.[]')
  _loadingStatus() {
    once(this, 'closePanel');
  }

  closePanel() {
    if (!this.get('isDestroyed') && !this.get('isDestroying')) {
      if (this.closePropertyPanel) {
        this.send('deSelectAllSelection');
        this.closePropertyPanel();
      }
    }
  }

  @action
  sort(column) {
    column.set('isDescending', !column.isDescending);

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
  }

  @action
  onRowClick(item, index, e) {
    const { target: { classList } } = e;
    const { tabName, storeName } = this.getProperties('tabName', 'storeName');
    if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
      if (this.get('selectedRowIndex') !== index) {
        // if clicked row is one among the checkbox selected list, row click will highlight that row keeping others
        // checkbox selected.
        // when a row not in the checkbox selected list is clicked, other checkboxes are cleared.
        if (!this._isAlreadySelected(this.get('fileContextSelections'), item)) {
          this.send('deSelectAllSelection', tabName);
          this.send('toggleRowSelection', tabName, item);
        }
        this.send('onHostFileSelection', tabName, storeName, item, index);
        if (this.openPropertyPanel) {
          next(() => {
            this.openPropertyPanel();
          });
        }
      } else {
        this.send('toggleRowSelection', tabName, item);
        this.send('onHostFileSelection', this.get('tabName'), this.get('storeName'), { id: null }, null);
        if (this.closePropertyPanel) {
          this.closePropertyPanel();
        }
      }
    }
  }

  @action
  beforeContextMenuShow(menu, event) {
    const { contextSelection: item, contextItems } = menu;
    // Highlight is removed and right panel is closed when right clicked on non-highlighted row
    if (this.get('selectedRowId') !== item.id) {
      this.closePropertyPanel();
      this.send('onHostFileSelection', this.get('tabName'), this.get('storeName'), item, null);
    }
    if (contextItems.length) {
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
  }

  @action
  pivotToInvestigate(item, category) {
    this.get('pivot').pivotToInvestigate('checksumSha256', item, category);
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
  onResetScoreModalClose() {
    this.set('showResetScoreModal', false);
  }

  @action
  onColumnConfigChange(changedProperty, columns) {
    this.send('saveColumnConfig', this.get('tabName'), changedProperty, columns);
  }

  @action
  onToggleColumn(column, columns) {
    this.send('saveColumnConfig', this.get('tabName'), 'display', columns);
  }
}

export default connect(stateToComputed, dispatchToActions)(FileContextTable);
