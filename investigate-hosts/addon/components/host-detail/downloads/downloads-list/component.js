import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  isAllSelected,
  files,
  areFilesLoading,
  nextLoadCount
} from 'investigate-hosts/reducers/details/downloads/selectors';

import {
  sortBy,
  getPageOfDownloads,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  saveFileStatus,
  onFileSelection,
  setSelectedIndex
} from 'investigate-hosts/actions/data-creators/downloads';
import { isAlreadySelected } from 'investigate-hosts/util/util';

import FIXED_COLUMNS from './download-table-columns';

const stateToComputed = (state) => ({
  areFilesLoading: areFilesLoading(state),
  loadMoreStatus: state.endpoint.hostDownloads.downloads.loadMoreStatus,
  files: files(state), // All visible files
  totalItems: state.endpoint.hostDownloads.downloads.totalItems,
  sortField: state.endpoint.hostDownloads.downloads.sortField, // Currently applied sort on file list
  isSortDescending: state.endpoint.hostDownloads.downloads.isSortDescending,
  isAllSelected: isAllSelected(state),
  selections: state.endpoint.hostDownloads.downloads.selectedFileList,
  selectedIndex: state.endpoint.hostDownloads.downloads.selectedIndex,
  serverId: state.endpointQuery.serverId,
  nextLoadCount: nextLoadCount(state)
});

const dispatchToActions = {
  sortBy,
  getPageOfDownloads,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  saveFileStatus,
  onFileSelection,
  setSelectedIndex
};

/**
 * File list component for displaying the list of files
 * @public
 */
const DownloadedFileList = Component.extend({

  tagName: '',

  columnsConfig: FIXED_COLUMNS,

  selectedFiles: null,

  contextItems: null,

  currentSort: {
    field: 'downloadedTime',
    direction: 'desc'
  },

  actions: {

    sortData(column) {
      const { isSortDescending } = this.getProperties('isSortDescending');
      const sortDirection = !isSortDescending;
      const field = column.get('field');
      let direction = 'asc';
      if (this.get('currentSort.direction') === 'desc') {
        direction = 'asc';
      } else {
        direction = 'desc';
      }

      this.set('currentSort', { field, direction });
      this.send('sortBy', field, sortDirection);
    },

    toggleSelectedRow(item, index, e, table) {
      const { target: { classList } } = e;
      if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
        const isSameRowClicked = table.get('selectedIndex') === index;
        this.send('setSelectedIndex', index);
        if (!isSameRowClicked) {
          // if clicked row is one among the checkbox selected list, row click will highlight that row keeping others
          // checkbox selected.
          // when a row not in the checkbox selected list is clicked, other checkboxes are cleared.
          if (!isAlreadySelected(this.get('selections'), item)) {
            this.send('deSelectAllFiles');
            this.send('toggleFileSelection', item);
          }
          this.send('onFileSelection', item);
        } else {
          this.send('toggleFileSelection', item);
          this.send('setSelectedIndex', -1);
        }
      }
    },

    toggleAllSelection() {
      if (!this.get('isAllSelected')) {
        this.send('selectAllFiles');
      } else {
        this.send('deSelectAllFiles');
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

        if (!isAlreadySelected(this.get('selections'), item)) {
          this.send('deSelectAllFiles');
          this.send('toggleFileSelection', item);
        }
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DownloadedFileList);
