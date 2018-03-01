import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileCountForDisplay } from 'investigate-files/reducers/file-list/selectors';
import {
  exportFileAsCSV
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  // Total number of files in search result
  totalItems: fileCountForDisplay(state),
  downloadId: state.files.fileList.downloadId
});

const dispatchToActions = {
  exportFileAsCSV
};
/**
 * Toolbar that provides search filtering.
 * @public
 */
const ToolBar = Component.extend({
  tagName: ''
});
export default connect(stateToComputed, dispatchToActions)(ToolBar);
