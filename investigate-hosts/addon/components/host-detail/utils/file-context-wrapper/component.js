import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
import { isEmpty } from '@ember/utils';

import {
  fileContextFileProperty,
  fileContextSelections,
  fileStatus,
  selectedFileChecksums,
  isRemediationAllowed,
  fileDownloadButtonStatus,
  focusedRowChecksum,
  selectedFileList
} from 'investigate-hosts/reducers/details/file-context/selectors';
import { hostDetailPropertyTabs } from 'investigate-hosts/reducers/details/selectors';
import { setHostDetailPropertyTab, saveLocalFileCopy } from 'investigate-hosts/actions/data-creators/details';

import {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadFilesToServer
} from 'investigate-hosts/actions/data-creators/file-context';

import { getFileAnalysisData } from 'investigate-shared/actions/data-creators/file-analysis-creators';

import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';
import { resetRiskScore, getUpdatedRiskScoreContext } from 'investigate-shared/actions/data-creators/risk-creators';
import { riskState } from 'investigate-hosts/reducers/visuals/selectors';

const stateToComputed = (state, { storeName }) => ({
  downloadLink: state.endpoint.detailsInput.downloadLink,
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
  risk: riskState(state)
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
  saveLocalFileCopy
};


const ContextWrapper = Component.extend({
  tagName: 'hbox',

  classNames: ['file-context-wrapper'],

  isPaginated: false,

  storeName: '',

  columnsConfig: null,

  propertyConfig: null,

  tabName: '',

  flashMessage: service(),

  @computed('downloadLink')
  iframeSrc(link) {
    let source = null;
    if (!isEmpty(link)) {
      source = `${link}&${Number(new Date())}`;
    }
    return source;
  },

  actions: {
    onDownloadFilesToServer() {
      const callBackOptions = {
        onSuccess: () => this.get('flashMessage').showFlashMessage('investigateHosts.flash.fileDownloadRequestSent'),
        onFailure: (message) => this.get('flashMessage').showErrorMessage(message)
      };

      const agentId = this.get('agentId');
      let fileContextSelections = this.get('fileContextSelections');

      if (fileContextSelections.length > 100) {
        fileContextSelections = fileContextSelections.slice(0, 100);
      }

      this.send('downloadFilesToServer', agentId, fileContextSelections, callBackOptions);
    },
    onSaveLocalCopy() {
      this.send('saveLocalFileCopy', this.get('selectedFileList')[0]);
    },
    onAnalyzeFile() {
      // Open analyze file.
      const fileContextSelections = this.get('fileContextSelections');
      const [{ checksumSha256 }] = fileContextSelections;

      this.send('getFileAnalysisData', checksumSha256);
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
