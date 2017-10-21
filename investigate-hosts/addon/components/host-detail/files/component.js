import Component from 'ember-component';
import columnsConfig from './host-files-columns';
import { connect } from 'ember-redux';
import PROPERTY_CONFIG from 'investigate-hosts/components/host-detail/base-property-config';
import { filesWithEnrichedData, fileProperty, fileCount } from 'investigate-hosts/reducers/details/files/selectors';
import { getHostFiles, sortBy, setSelectedFile } from 'investigate-hosts/actions/data-creators/files';
import { machineOsType } from 'investigate-hosts/reducers/details/overview/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  status: state.endpoint.hostFiles.filesLoadingStatus,
  filesLoadMoreStatus: state.endpoint.hostFiles.filesLoadMoreStatus,
  totalItems: state.endpoint.hostFiles.totalItems,
  files: filesWithEnrichedData(state),
  fileProperty: fileProperty(state),
  machineOsType: machineOsType(state),
  fileCount: fileCount(state)
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

  @computed('machineOsType')
  columnsConfig(machineOsType) {
    return columnsConfig[machineOsType];
  },

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
