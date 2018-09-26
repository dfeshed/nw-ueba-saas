import Component from '@ember/component';
import { connect } from 'ember-redux';
import { failure } from 'investigate-shared/utils/flash-messages';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';

import {
  listOfFiles,
  fileContextSelections,
  isAllSelected,
  isDataLoading,
  selectedRowId,
  fileStatus,
  selectedFileChecksums,
  totalItems,
  contextLoadMoreStatus
} from 'investigate-hosts/reducers/details/file-context/selectors';

import {
  setFileContextSort,
  toggleRowSelection,
  toggleAllSelection,
  setRowSelection,
  getFileContextFileStatus,
  setFileContextFileStatus,
  getPaginatedFileContext,
  resetSelection
} from 'investigate-hosts/actions/data-creators/file-context';


const stateToComputed = (state, { storeName }) => ({
  listOfFiles: listOfFiles(state, storeName),
  fileContextSelections: fileContextSelections(state, storeName),
  isAllSelected: isAllSelected(state, storeName),
  selectedRowId: selectedRowId(state, storeName),
  isDataLoading: isDataLoading(state, storeName),
  serviceList: serviceList(state, storeName),
  fileStatus: fileStatus(state, storeName),
  selectedFileChecksums: selectedFileChecksums(state, storeName),
  totalItems: totalItems(state, storeName),
  contextLoadMoreStatus: contextLoadMoreStatus(state, storeName)
});

const dispatchToActions = {
  setFileContextSort,
  toggleRowSelection,
  toggleAllSelection,
  setRowSelection,
  getFileContextFileStatus,
  setFileContextFileStatus,
  getPaginatedFileContext,
  resetSelection
};

const FileContextTable = Component.extend({

  tagName: 'box',

  accessControl: service(),

  classNames: ['file-context-table', 'host-detail__datatable'],

  customSort: null,

  showServiceModal: false,

  showFileStatusModal: false,

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
          this.send('setRowSelection', this.get('tabName'), item);
        } else {
          table.set('selectedIndex', -1);
          this.send('setRowSelection', this.get('tabName'), { id: null });
        }
      }
    },

    beforeContextMenuShow(item) {
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

    showServiceList(item) {
      this.set('itemList', [item]);
      this.set('showServiceModal', true);
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FileContextTable);
