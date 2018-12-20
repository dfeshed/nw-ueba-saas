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
  retrieveRemediationStatus,
  triggerFileActions
} from 'investigate-files/actions/data-creators';
import { setEndpointServer } from 'investigate-shared/actions/data-creators/endpoint-server-creators';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';
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
  timeRange: timeRange(state),
  certificateLoadStatus: state.certificate.list.certificatesLoadingStatus
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
        onSuccess: (response) => {
          const { data } = response;
          if (data === itemsList.length) {
            success('investigateFiles.riskScore.success');
          } else {
            warning('investigateFiles.riskScore.warning');
          }
        },
        onFailure: () => failure('investigateFiles.riskScore.error')
      };
      this.send('resetRiskScore', itemsList, callBackOptions);
    },

    handleServiceSelection(service) {
      this.send('setEndpointServer', true, service, triggerFileActions);
      if (this.closeRiskPanel) {
        this.closeRiskPanel();
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(ToolBar);
