import Component from 'ember-component';
import { connect } from 'ember-redux';
import { loadMoreStatus, serviceList } from 'investigate-files/reducers/file-list/selectors';
import { columns } from 'investigate-files/reducers/schema/selectors';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import {
  sortBy,
  getPageOfFiles
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  serviceList: serviceList(state),
  columnConfig: columns(state),
  loadMoreStatus: loadMoreStatus(state),
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

  tagName: '',

  @computed('columnConfig')
  updatedColumns(columns) {
    return this._sortList(columns);
  },

  _sortList(columnList) {
    const i18n = this.get('i18n');
    return _.sortBy(columnList, [(column) => {
      return i18n.t(column.title).toString();
    }]);
  }
});

export default connect(stateToComputed, dispatchToActions)(FileList);
