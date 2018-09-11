import Component from '@ember/component';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import PROPERTY_CONFIG from 'investigate-hosts/components/host-detail/base-property-config';
import {
  filesWithEnrichedData,
  fileProperty,
  isDataLoading,
  isAllSelected,
  selectedFileCount,
  checksums
} from 'investigate-hosts/reducers/details/files/selectors';
import {
  getHostFiles,
  sortBy,
  setSelectedFile,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  saveFileStatus,
  getSavedFileStatus
} from 'investigate-hosts/actions/data-creators/files';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';
import columnsConfig from './host-files-columns';
import { failure } from 'investigate-shared/utils/flash-messages';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  status: isDataLoading(state),
  filesLoadMoreStatus: state.endpoint.hostFiles.filesLoadMoreStatus,
  totalItems: state.endpoint.hostFiles.totalItems,
  files: filesWithEnrichedData(state),
  fileProperty: fileProperty(state),
  columnsConfig: getColumnsConfig(state, columnsConfig),
  isAllSelected: isAllSelected(state),
  selectedFileCount: selectedFileCount(state),
  selectedFileList: state.endpoint.hostFiles.selectedFileList,
  fileStatusData: state.endpoint.hostFiles.fileStatusData,
  checksums: checksums(state)
});

const dispatchToActions = {
  getHostFiles,
  sortBy,
  setSelectedFile,
  toggleFileSelection,
  selectAllFiles,
  deSelectAllFiles,
  saveFileStatus,
  getSavedFileStatus
};

const Files = Component.extend({

  tagName: '',

  accessControl: service(),

  filePropertyConfig: PROPERTY_CONFIG,

  selections: [],

  showFileStatusModal: false,

  rowItem: null,


  isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('checksumSha256', item.checksumSha256);
    }
    return selected;
  },

  @computed('fileStatusData')
  statusData(fileStatusData) {
    return fileStatusData ? fileStatusData.asMutable() : {};
  },
  actions: {
    sort(column) {
      const { field: sortField, isDescending } = column;
      const isDescOrder = (isDescending === undefined) ? true : isDescending;
      this.send('sortBy', { sortField, isDescOrder });
    },
    toggleAllSelection() {
      if (!this.get('isAllSelected')) {
        this.send('selectAllFiles');
      } else {
        this.send('deSelectAllFiles');
      }
    },
    beforeContextMenuShow(item) {
      if (!this.isAlreadySelected(this.get('selections'), item)) {
        this.send('toggleFileSelection', item);
      }
    },
    showEditFileStatus(item) {
      if (this.get('accessControl.endpointCanManageFiles')) {
        this.set('rowItem', item);
        this.set('showFileStatusModal', true);
      } else {
        failure('investigateFiles.noManagePermissions');
      }
    },
    onCloseEditFileStatus() {
      this.set('showFileStatusModal', false);
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(Files);