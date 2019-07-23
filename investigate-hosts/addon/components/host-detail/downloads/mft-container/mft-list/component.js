import Component from '@ember/component';
import { connect } from 'ember-redux';
import { listOfMftFiles, isAllMftSelected, areMftFilesLoading, nextLoadCount, pageStatus } from 'investigate-hosts/reducers/details/mft-directory/selectors';
import {
  getPageOfMftFiles,
  toggleMftFileSelection,
  selectAllMftFiles,
  deSelectAllMftFiles,
  saveFileStatus,
  onFileSelection,
  setSelectedMftIndex,
  sortMftBy
} from 'investigate-hosts/actions/data-creators/downloads';
import { isAlreadySelected } from 'investigate-hosts/util/util';
import computed from 'ember-computed-decorators';
import { success } from 'investigate-shared/utils/flash-messages';

import {
  downloadFilesToServer
} from 'investigate-hosts/actions/data-creators/file-context';

const callBackOptions = (context) => ({
  onSuccess: () => success('investigateHosts.flash.fileDownloadRequestSent'),
  onFailure: (message) => context.get('flashMessage').showErrorMessage(message)
});
import FIXED_COLUMNS from './mft-table-columns';

const stateToComputed = (state) => ({
  areFilesLoading: areMftFilesLoading(state),
  loadMoreStatus: state.endpoint.hostDownloads.mft.mftDirectory.loadMoreStatus,
  files: listOfMftFiles(state),
  totalItems: state.endpoint.hostDownloads.mft.mftDirectory.totalMftItems,
  sortField: state.endpoint.hostDownloads.mft.mftDirectory.sortField, // Currently applied sort on file list
  isSortDescending: state.endpoint.hostDownloads.mft.mftDirectory.isSortDescending,
  isAllMftSelected: isAllMftSelected(state),
  selections: state.endpoint.hostDownloads.mft.mftDirectory.selectedMftFileList,
  selectedIndex: state.endpoint.hostDownloads.downloads.selectedMftIndex,
  serverId: state.endpointQuery.serverId,
  nextLoadCount: nextLoadCount(state),
  servers: state.endpointServer.serviceData,
  pageStatus: pageStatus(state),
  agentId: state.endpoint.detailsInput.agentId
});

const dispatchToActions = {
  toggleMftFileSelection,
  selectAllMftFiles,
  deSelectAllMftFiles,
  saveFileStatus,
  onFileSelection,
  setSelectedMftIndex,
  getPageOfMftFiles,
  sortMftBy,
  downloadFilesToServer
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

  callBackOptions,

  currentSort: {
    field: 'creationTime',
    direction: 'desc'
  },

  @computed('selections')
  disableActions(selections) {
    return { downloadFileToServer: selections.length !== 1 };
  },
  actions: {

    sortData(column) {
      const { isSortDescending } = this.getProperties('isSortDescending');
      const sortDirection = !isSortDescending;
      const field = column.get('field');
      let direction = 'desc';
      if (this.get('currentSort.direction') === 'desc') {
        direction = 'asc';
      }

      this.set('currentSort', { field, direction });
      this.send('sortMftBy', field, sortDirection);
    },

    toggleSelectedRow(item, index, e, table) {
      const { target: { classList } } = e;
      if (!(classList.contains('rsa-form-checkbox-label') || classList.contains('rsa-form-checkbox'))) {
        const isSameRowClicked = table.get('selectedIndex') === index;
        this.send('setSelectedMftIndex', index);
        if (!isSameRowClicked) {
          // if clicked row is one among the checkbox selected list, row click will highlight that row keeping others
          // checkbox selected.
          // when a row not in the checkbox selected list is clicked, other checkboxes are cleared.
          if (!isAlreadySelected(this.get('selections'), item)) {
            this.send('deSelectAllMftFiles');
            this.send('toggleMftFileSelection', item);
          }
          this.send('onFileSelection', item);
        } else {
          this.send('toggleMftFileSelection', item);
          this.send('setSelectedMftIndex', -1);
        }

      }
    },

    toggleAllSelection() {
      if (!this.get('isAllMftSelected')) {
        this.send('selectAllMftFiles');
      } else {
        this.send('deSelectAllMftFiles');
      }
    },

    beforeContextMenuShow(menu, event) {
      const { contextSelection: item, contextItems } = menu;
      const { directory } = item;
      if (!this.get('contextItems')) {
        // Need to store this locally set it back again to menu object
        this.set('contextItems', contextItems);
      }
      // For anchor tag hid the context menu and show browser default right click menu
      if (event.target.tagName.toLowerCase() === 'a' || directory) {
        menu.set('contextItems', []);
      } else {
        menu.set('contextItems', this.get('contextItems'));

        if (!isAlreadySelected(this.get('selections'), item)) {
          this.send('deSelectAllMftFiles');
          this.send('toggleMftFileSelection', item);
        }
      }
    },
    onDownloadFilesToServer() {
      const callBackOptions = this.get('callBackOptions')(this);
      const agentId = this.get('agentId');
      const fileSelections = this.get('selections');

      this.send('downloadFilesToServer', agentId, fileSelections, callBackOptions);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(DownloadedFileList);
