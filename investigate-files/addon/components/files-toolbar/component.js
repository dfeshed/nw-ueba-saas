import Component from '@ember/component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { serviceList, checksums } from 'investigate-files/reducers/file-list/selectors';
import {
  exportFileAsCSV,
  getAllServices,
  saveFileStatus,
  getSavedFileStatus,
  retrieveRemediationStatus
} from 'investigate-files/actions/data-creators';
import { setEndpointServer } from 'investigate-files/actions/endpoint-server-creators';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';
import { toggleCertificateView } from 'investigate-files/actions/certificate-data-creators';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state) => ({
  // Total number of files in search result
  totalItems: state.files.fileList.totalItems,
  downloadId: state.files.fileList.downloadId,
  checksums: checksums(state),
  selectedFileCount: state.files.fileList.selectedFileList.length,
  serviceList: serviceList(state),
  itemList: state.files.fileList.selectedFileList,
  servers: state.endpointServer,
  serverId: state.endpointQuery.serverId,
  fileStatusData: state.files.fileList.fileStatusData,
  remediationStatus: state.files.fileList.isRemediationAllowed,
  restrictedFileList: state.fileStatus.restrictedFileList,
  serviceId: serviceId(state),
  timeRange: timeRange(state)
});

const dispatchToActions = {
  exportFileAsCSV,
  getAllServices,
  saveFileStatus,
  setEndpointServer,
  getSavedFileStatus,
  retrieveRemediationStatus,
  resetRiskScore,
  toggleCertificateView
};
/**
 * Toolbar that provides search filtering.
 * @public
 */
const ToolBar = Component.extend({
  tagName: 'hbox',

  classNames: 'files-toolbar',

  flashMessage: service(),

  @computed('fileStatusData')
  data(fileStatusData) {
    return { ...fileStatusData };
  },
  actions: {
    resetRiskScoreAction(itemsList) {
      const callBackOptions = {
        onSuccess: () => {
          success('investigateFiles.riskScore.success');
        },
        onFailure: () => failure('investigateFiles.riskScore.error')
      };
      this.send('resetRiskScore', itemsList, callBackOptions);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ToolBar);
