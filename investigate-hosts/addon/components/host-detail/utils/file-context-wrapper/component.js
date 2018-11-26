import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

import {
  fileContextFileProperty,
  fileContextSelections,
  fileStatus,
  selectedFileChecksums,
  isRemediationAllowed
} from 'investigate-hosts/reducers/details/file-context/selectors';

import {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadFilesToServer
} from 'investigate-hosts/actions/data-creators/file-context';

import { openAndFetchFileAnalyzerData } from 'investigate-hosts/actions/data-creators/file-analysis';

import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';

const stateToComputed = (state, { storeName }) => ({
  fileProperty: fileContextFileProperty(state, storeName),
  fileContextSelections: fileContextSelections(state, storeName),
  serviceList: serviceList(state, storeName),
  fileStatus: fileStatus(state, storeName),
  selectedFileChecksums: selectedFileChecksums(state, storeName),
  isRemediationAllowed: isRemediationAllowed(state, storeName),
  agentId: state.endpoint.detailsInput.agentId,
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  restrictedFileList: state.fileStatus.restrictedFileList
});

const dispatchToActions = {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadFilesToServer,
  openAndFetchFileAnalyzerData,
  resetRiskScore
};


const ContextWrapper = Component.extend({
  tagName: 'hbox',

  classNames: ['file-context-wrapper'],

  isPaginated: false,

  storeName: '',

  columnsConfig: null,

  propertyConfig: null,

  tabName: '',

  @computed('fileContextSelections')
  fileDownloadButtonStatus(fileContextSelections) {
    // if selectedFilesLength be more than 1 and file download status be true then isDownloadToServerDisabled should return true
    const selectedFilesLength = fileContextSelections.length;
    const areAllFilesNotDownloadedToServer = fileContextSelections.some((item) => {
      if (item.downloadInfo) {
        return item.downloadInfo.status !== 'Downloaded';
      }
      return true;
    });

    return {
      isDownloadToServerDisabled: ((selectedFilesLength > 0) && (!areAllFilesNotDownloadedToServer)), // and file's downloaded status is true
      isSaveLocalAndFileAnalysisDisabled: ((selectedFilesLength !== 1) || areAllFilesNotDownloadedToServer) // or file's downloaded status is true
    };
  },

  flashMessage: service(),

  actions: {
    onDownloadFilesToServer() {
      const callBackOptions = {
        onSuccess: () => this.get('flashMessage').showFlashMessage('investigateHosts.flash.fileDownloadRequestSent'),
        onFailure: (message) => this.get('flashMessage').showErrorMessage(message)
      };
      const { agentId, fileContextSelections } = this.getProperties('agentId', 'fileContextSelections');

      this.send('downloadFilesToServer', agentId, fileContextSelections, callBackOptions);
    },
    onSaveLocalCopy() {
      // Placeholder for the next PR.
    },
    onAnalyzeFile() {
      // Open analyze file.
      const fileContextSelections = this.get('fileContextSelections');
      const { checksumSha256 } = fileContextSelections;
      this.send('openAndFetchFileAnalyzerData', checksumSha256);
    },

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

export default connect(stateToComputed, dispatchToActions)(ContextWrapper);
