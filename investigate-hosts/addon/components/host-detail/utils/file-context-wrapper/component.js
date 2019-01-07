import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';

import {
  fileContextFileProperty,
  fileContextSelections,
  fileStatus,
  selectedFileChecksums,
  isRemediationAllowed,
  fileDownloadButtonStatus,
  focusedRowChecksum,
  selectedFileList,
  isAnyFileFloatingOrMemoryDll
} from 'investigate-hosts/reducers/details/file-context/selectors';
import { hostDetailPropertyTabs, downloadLink } from 'investigate-hosts/reducers/details/selectors';
import { setHostDetailPropertyTab, saveLocalFileCopy } from 'investigate-hosts/actions/data-creators/details';

import {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadFilesToServer,
  setRowSelection
} from 'investigate-hosts/actions/data-creators/file-context';

import { getFileAnalysisData } from 'investigate-shared/actions/data-creators/file-analysis-creators';

import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';
import { resetRiskScore, getUpdatedRiskScoreContext } from 'investigate-shared/actions/data-creators/risk-creators';
import { riskState } from 'investigate-hosts/reducers/visuals/selectors';
import { componentSelectionForFileType } from 'investigate-shared/utils/file-analysis-view-util';

const callBackOptions = (context) => ({
  onSuccess: () => success('investigateHosts.flash.fileDownloadRequestSent'),
  onFailure: (message) => context.get('flashMessage').showErrorMessage(message)
});

const stateToComputed = (state, { storeName }) => ({
  downloadLink: downloadLink(state),
  selectedFileList: selectedFileList(state, storeName),
  fileProperty: fileContextFileProperty(state, storeName),
  hostDetailPropertyTabs: hostDetailPropertyTabs(state, storeName),
  focusedRowChecksum: focusedRowChecksum(state, storeName),
  fileContextSelections: fileContextSelections(state, storeName),
  serviceList: serviceList(state, storeName),
  fileStatus: fileStatus(state, storeName),
  selectedFileChecksums: selectedFileChecksums(state, storeName),
  isRemediationAllowed: isRemediationAllowed(state, storeName),
  agentId: state.endpoint.detailsInput.agentId,
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  restrictedFileList: state.fileStatus.restrictedFileList,
  fileDownloadButtonStatus: fileDownloadButtonStatus(state, storeName),
  activeHostDetailPropertyTab: state.endpoint.detailsInput.activeHostDetailPropertyTab,
  risk: riskState(state),
  isFloatingOrMemoryDll: isAnyFileFloatingOrMemoryDll(state, storeName)
});

const dispatchToActions = {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadFilesToServer,
  getFileAnalysisData,
  resetRiskScore,
  setHostDetailPropertyTab,
  getUpdatedRiskScoreContext,
  saveLocalFileCopy,
  setRowSelection
};


const ContextWrapper = Component.extend({
  tagName: 'box',

  classNames: ['file-context-wrapper'],

  isPaginated: false,

  storeName: '',

  columnsConfig: null,

  propertyConfig: null,

  tabName: '',

  flashMessage: service(),

  callBackOptions,

  actions: {

    onPropertyPanelClose() {
      this.send('setRowSelection', this.get('tabName'), null, null);
    },

    onDownloadFilesToServer() {
      const callBackOptions = this.get('callBackOptions')(this);
      const agentId = this.get('agentId');
      let fileContextSelections = this.get('fileContextSelections');

      if (fileContextSelections.length > 100) {
        fileContextSelections = fileContextSelections.slice(0, 100);
      }

      this.send('downloadFilesToServer', agentId, fileContextSelections, callBackOptions);
    },
    onSaveLocalCopy() {
      const callBackOptions = this.get('callBackOptions')(this);
      this.send('saveLocalFileCopy', this.get('selectedFileList')[0], callBackOptions);
    },
    onAnalyzeFile() {
      const callBackOptions = this.get('callBackOptions')(this);
      // Open analyze file.
      const fileContextSelections = this.get('fileContextSelections');
      const [{ checksumSha256, format = '' }] = fileContextSelections;
      const fileFormat = componentSelectionForFileType(format).format;

      this.send('getFileAnalysisData', checksumSha256, fileFormat, callBackOptions);
    },

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
      this.send('resetRiskScore', itemsList, 'FILE', callBackOptions);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ContextWrapper);
