import Component from 'ember-component';
import { connect } from 'ember-redux';

import { columns } from 'investigate-files/reducers/schema/selectors';
import {
  sortBy,
  getPageOfFiles
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  columnConfig: columns(state),
  loadMoreStatus: state.files.fileList.loadMoreStatus,
  areFilesLoading: state.files.fileList.areFilesLoading,
  files: state.files.fileList.files, // All visible files
  totalItems: state.files.fileList.totalItems,
  sortField: state.files.fileList.sortField, // Currently applied sort on file list
  isSortDescending: state.files.fileList.isSortDescending
});

const dispatchToActions = {
  sortBy,
  getPageOfFiles
};

/**
 * File list component for displaying the list of files
 * @public
 */
const FileList = Component.extend({
  classNames: ['file-list']
});

export default connect(stateToComputed, dispatchToActions)(FileList);