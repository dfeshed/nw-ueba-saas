import Component from 'ember-component';
import { connect } from 'ember-redux';

import {
  exportFileAsCSV
} from 'investigate-files/actions/data-creators';

const stateToComputed = ({ files }) => ({
  // Total number of files in search result
  totalItems: files.fileList.totalItems,
  downloadId: files.fileList.downloadId
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
