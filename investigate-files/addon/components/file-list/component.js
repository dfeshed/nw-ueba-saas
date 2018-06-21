import Component from '@ember/component';
import { connect } from 'ember-redux';
import { fileCountForDisplay, serviceList } from 'investigate-files/reducers/file-list/selectors';
import { columns } from 'investigate-files/reducers/schema/selectors';
import computed from 'ember-computed-decorators';
import _ from 'lodash';
import { inject as service } from '@ember/service';
import {
  sortBy,
  getPageOfFiles,
  fetchFileContext,
  toggleRiskPanel
} from 'investigate-files/actions/data-creators';

const stateToComputed = (state) => ({
  serviceList: serviceList(state),
  columnConfig: columns(state),
  loadMoreStatus: state.files.fileList.loadMoreStatus,
  areFilesLoading: state.files.fileList.areFilesLoading,
  files: state.files.fileList.files, // All visible files
  totalItems: fileCountForDisplay(state),
  sortField: state.files.fileList.sortField, // Currently applied sort on file list
  isSortDescending: state.files.fileList.isSortDescending,
  showRiskPanel: state.files.fileList.showRiskPanel
});

const dispatchToActions = {
  sortBy,
  getPageOfFiles,
  fetchFileContext,
  toggleRiskPanel
};

/**
 * File list component for displaying the list of files
 * @public
 */
const FileList = Component.extend({

  tagName: '',

  features: service(),

  @computed('columnConfig')
  updatedColumns(columns) {
    return this._sortList(columns);
  },

  _sortList(columnList) {
    const i18n = this.get('i18n');
    return _.sortBy(columnList, [(column) => {
      return i18n.t(column.title).toString();
    }]);
  },

  actions: {
    toggleSelectedRow(item, index, e, table) {
      if (this.get('features.rsaEndpointFusion')) {
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
  }
});

export default connect(stateToComputed, dispatchToActions)(FileList);
