import Component from '@ember/component';
import { connect } from 'ember-redux';
import { next } from '@ember/runloop';
import {
  fileCountForDisplay,
  serviceList,
  isAllSelected,
  processedFileList,
  checksums
} from 'investigate-files/reducers/file-list/selectors';
import { columns } from 'investigate-files/reducers/schema/selectors';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import {
  sortBy,
  getPageOfFiles,
  fetchFileContext,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  getAllServices,
  saveFileStatus
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  serviceList: serviceList(state),
  columnConfig: columns(state),
  loadMoreStatus: state.files.fileList.loadMoreStatus,
  areFilesLoading: state.files.fileList.areFilesLoading,
  files: processedFileList(state), // All visible files
  totalItems: fileCountForDisplay(state),
  sortField: state.files.fileList.sortField, // Currently applied sort on file list
  isSortDescending: state.files.fileList.isSortDescending,
  isAllSelected: isAllSelected(state),
  selections: state.files.fileList.selectedFileList,
  checksums: checksums(state)
});

const dispatchToActions = {
  sortBy,
  getPageOfFiles,
  fetchFileContext,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  getAllServices,
  saveFileStatus
};

/**
 * File list component for displaying the list of files
 * @public
 */
const FileList = Component.extend({

  tagName: '',

  FIXED_COLUMNS: [
    {
      dataType: 'checkbox',
      width: 20,
      class: 'rsa-form-row-checkbox',
      componentClass: 'rsa-form-checkbox',
      visible: true,
      disableSort: true,
      headerComponentClass: 'rsa-form-checkbox'
    },
    {
      dataType: 'string',
      width: 200,
      visible: true,
      field: 'firstFileName',
      searchable: true,
      title: 'investigateFiles.fields.firstFileName'
    }
  ],

  @computed('columnConfig')
  updatedColumns(columns) {
    const UPDATED_COLUMNS = columns.filter((column) => column.field !== 'firstFileName');
    return this._sortList(UPDATED_COLUMNS);
  },

  _sortList(columnList) {
    const i18n = this.get('i18n');
    const sortList = _.sortBy(columnList, [(column) => {
      return i18n.t(column.title).toString();
    }]);
    return this.FIXED_COLUMNS.concat(sortList);
  },

  init() {
    this._super(arguments);
    this.send('getAllServices');
  },
  actions: {
    toggleSelectedRow(item, index, e, table) {
      const { target: { classList } } = e;
      if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
        const isSameRowClicked = table.get('selectedIndex') === index;
        const openRiskPanel = this.get('openRiskPanel');
        table.set('selectedIndex', index);

        if (!isSameRowClicked && openRiskPanel) {
          this.send('fetchFileContext', item.firstFileName);
          next(() => {
            this.openRiskPanel();
          });
        } else {
          this.closeRiskPanel();
          table.set('selectedIndex', null);
        }
      }
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(FileList);
