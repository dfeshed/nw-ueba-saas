import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  fileCountForDisplay,
  serviceList,
  isAllSelected,
  processedFileList
} from 'investigate-files/reducers/file-list/selectors';
import { columns } from 'investigate-files/reducers/schema/selectors';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import { inject as service } from '@ember/service';
import {
  sortBy,
  getPageOfFiles,
  fetchFileContext,
  toggleRiskPanel,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles
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
  showRiskPanel: state.files.fileList.showRiskPanel,
  isAllSelected: isAllSelected(state)
});

const dispatchToActions = {
  sortBy,
  getPageOfFiles,
  fetchFileContext,
  toggleRiskPanel,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles
};

/**
 * File list component for displaying the list of files
 * @public
 */
const FileList = Component.extend({

  tagName: '',

  features: service(),

  FIXED_COLUMNS: [
    {
      dataType: 'checkbox',
      width: 22,
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

  actions: {
    toggleSelectedRow(item, index, e, table) {
      if (this.get('features.rsaEndpointFusion')) {
        const { target: { classList } } = e;
        if (classList.contains('rsa-icon-expand-6-filled') ||
          classList.contains('rsa-form-checkbox-label') ||
          classList.contains('rsa-form-checkbox')) {
          e.stopPropagation();
        } else {
          const isRiskPanelVisible = this.get('showRiskPanel');
          const isSameRowClicked = table.get('selectedIndex') === index;
          if (isSameRowClicked && isRiskPanelVisible) {
            this.send('toggleRiskPanel', false);
          } else {
            this.send('toggleRiskPanel', true);
            this.send('fetchFileContext', item.firstFileName);
          }
          table.set('selectedIndex', index);
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
