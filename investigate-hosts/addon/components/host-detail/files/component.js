import Component from 'ember-component';
import { connect } from 'ember-redux';
import PROPERTY_CONFIG from 'investigate-hosts/components/host-detail/base-property-config';
import { filesWithEnrichedData, fileProperty, isDataLoading } from 'investigate-hosts/reducers/details/files/selectors';
import { getHostFiles, sortBy, setSelectedFile } from 'investigate-hosts/actions/data-creators/files';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './host-files-columns';

const stateToComputed = (state) => ({
  status: isDataLoading(state),
  filesLoadMoreStatus: state.endpoint.hostFiles.filesLoadMoreStatus,
  totalItems: state.endpoint.hostFiles.totalItems,
  files: filesWithEnrichedData(state),
  fileProperty: fileProperty(state),
  columnsConfig: getColumnsConfig(state, columnsConfig)
});

const dispatchToActions = {
  getHostFiles,
  sortBy,
  setSelectedFile
};

const Files = Component.extend({

  tagName: '',

  filePropertyConfig: PROPERTY_CONFIG,

  actions: {
    sort(column) {
      const { field: sortField, isDescending } = column;
      const isDescOrder = (isDescending === undefined) ? true : isDescending;
      this.send('sortBy', { sortField, isDescOrder });
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Files);
