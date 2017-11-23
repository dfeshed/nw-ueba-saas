import Component from 'ember-component';
import { connect } from 'ember-redux';
import PROPERTY_CONFIG from 'investigate-hosts/components/host-detail/base-property-config';
import { filesWithEnrichedData, fileProperty, fileCount } from 'investigate-hosts/reducers/details/files/selectors';
import { getHostFiles, sortBy, setSelectedFile } from 'investigate-hosts/actions/data-creators/files';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './host-files-columns';

const stateToComputed = (state) => ({
  status: state.endpoint.hostFiles.filesLoadingStatus,
  filesLoadMoreStatus: state.endpoint.hostFiles.filesLoadMoreStatus,
  totalItems: state.endpoint.hostFiles.totalItems,
  files: filesWithEnrichedData(state),
  fileProperty: fileProperty(state),
  fileCount: fileCount(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dispatchToActions = {
  getHostFiles,
  sortBy,
  setSelectedFile
};

const Files = Component.extend({

  tagName: 'hbox',

  classNames: ['host-files'],

  filePropertyConfig: PROPERTY_CONFIG,

  actions: {
    /**
     * Handle for the row click action
     * @param item
     * @param index
     * @param e
     * @param table
     * @public
     */
    handleRowClickAction(item, index, e, table) {
      const { checksumSha256 } = item;
      table.set('selectedIndex', index);
      this.send('setSelectedFile', checksumSha256);
    },

    sort(column) {
      const { field: sortField, isDescending: isDescOrder = true } = column;
      this.send('sortBy', { sortField, isDescOrder });
      column.set('isDescending', !isDescOrder);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Files);
