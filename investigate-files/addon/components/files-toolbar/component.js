import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileCountForDisplay, serviceList } from 'investigate-files/reducers/file-list/selectors';
import {
  exportFileAsCSV,
  getAllServices
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  // Total number of files in search result
  totalItems: fileCountForDisplay(state),
  downloadId: state.files.fileList.downloadId,
  selectedFileCount: state.files.fileList.selectedFileList.length,
  serviceList: serviceList(state),
  item: state.files.fileList.selectedFileList[0]
});

const dispatchToActions = {
  exportFileAsCSV,
  getAllServices
};
/**
 * Toolbar that provides search filtering.
 * @public
 */
const ToolBar = Component.extend({
  tagName: 'hbox'
});
export default connect(stateToComputed, dispatchToActions)(ToolBar);
