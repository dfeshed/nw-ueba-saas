import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { fileCountForDisplay, serviceList, checksums } from 'investigate-files/reducers/file-list/selectors';
import {
  exportFileAsCSV,
  getAllServices,
  saveFileStatus,
  getSavedFileStatus,
  retrieveRemediationStatus
} from 'investigate-files/actions/data-creators';
import { setEndpointServer } from 'investigate-files/actions/endpoint-server-creators';

const stateToComputed = (state) => ({
  // Total number of files in search result
  totalItems: fileCountForDisplay(state),
  downloadId: state.files.fileList.downloadId,
  checksums: checksums(state),
  selectedFileCount: state.files.fileList.selectedFileList.length,
  serviceList: serviceList(state),
  itemList: state.files.fileList.selectedFileList,
  servers: state.endpointServer,
  serverId: state.endpointQuery.serverId,
  fileStatusData: state.files.fileList.fileStatusData,
  remediationStatus: state.files.fileList.isRemediationAllowed
});

const dispatchToActions = {
  exportFileAsCSV,
  getAllServices,
  saveFileStatus,
  setEndpointServer,
  getSavedFileStatus,
  retrieveRemediationStatus
};
/**
 * Toolbar that provides search filtering.
 * @public
 */
const ToolBar = Component.extend({
  tagName: 'hbox',

  flashMessage: service(),

  @computed('fileStatusData')
  data(fileStatusData) {
    return { ...fileStatusData };
  }
});

export default connect(stateToComputed, dispatchToActions)(ToolBar);
