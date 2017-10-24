import Component from 'ember-component';
import { connect } from 'ember-redux';

import { columns } from 'investigate-files/reducers/schema/selectors';
import {
  sortBy,
  getPageOfFiles
} from 'investigate-files/actions/data-creators';

const stateToComputed = ({ files }) => ({
  columnConfig: columns(files),
  loadMoreStatus: files.fileList.loadMoreStatus,
  areFilesLoading: files.fileList.areFilesLoading,
  files: files.fileList.files, // All visible files
  totalItems: files.fileList.totalItems,
  sortField: files.fileList.sortField, // Currently applied sort on file list
  isSortDescending: files.fileList.isSortDescending
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