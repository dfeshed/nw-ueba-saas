import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileCountForDisplay, serviceList, checksums } from 'investigate-files/reducers/file-list/selectors';
import {
  exportFileAsCSV,
  getAllServices,
  saveFileStatus,
  deleteFilter,
  applySavedFilters
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  // Total number of files in search result
  totalItems: fileCountForDisplay(state),
  downloadId: state.files.fileList.downloadId,
  checksums: checksums(state),
  selectedFileCount: state.files.fileList.selectedFileList.length,
  serviceList: serviceList(state),
  item: state.files.fileList.selectedFileList[0],
  filesFilters: state.files.filter.savedFilterList

});

const dispatchToActions = {
  exportFileAsCSV,
  getAllServices,
  saveFileStatus,
  deleteFilter,
  applySavedFilters
};
/**
 * Toolbar that provides search filtering.
 * @public
 */
const ToolBar = Component.extend({
  tagName: 'hbox',

  actions: {

    applyCustomFilter(filter) {
      this.send('applySavedFilters', filter);
    },

    deleteSelectedFilter(id) {
      const callbackOptions = {
        onSuccess: () => {
          this.get('flashMessage').showFlashMessage('investigateFiles.filter.customFilters.delete.successMessage');
        },
        onFailure: ({ meta: message }) => this.get('flashMessage').showErrorMessage(message.message)
      };
      this.send('deleteFilter', id, callbackOptions);
    }
  }
});
export default connect(stateToComputed, dispatchToActions)(ToolBar);
