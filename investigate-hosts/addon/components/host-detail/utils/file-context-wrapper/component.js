import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList, isInsightsAgent } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';

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
import { hostDetailPropertyTabs, isProcessDumpDownloadSupported } from 'investigate-hosts/reducers/details/selectors';
import { hostName, isAgentMigrated } from 'investigate-hosts/reducers/details/overview/selectors';
import { setHostDetailPropertyTab } from 'investigate-hosts/actions/data-creators/details';

import {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadProcessDump,
  downloadFilesToServer,
  setRowSelection
} from 'investigate-hosts/actions/data-creators/file-context';

import { getFileAnalysisData, saveLocalFileCopy } from 'investigate-shared/actions/data-creators/file-analysis-creators';

import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success } from 'investigate-shared/utils/flash-messages';
import { getUpdatedRiskScoreContext } from 'investigate-shared/actions/data-creators/risk-creators';
import { riskState } from 'investigate-hosts/reducers/visuals/selectors';
import { componentSelectionForFileType } from 'investigate-shared/utils/file-analysis-view-util';

const callBackOptions = (context) => ({
  onSuccess: () => success('investigateHosts.flash.fileDownloadRequestSent'),
  onFailure: (message) => context.get('flashMessage').showErrorMessage(message)
});

const processDumpCallBackOptions = (context) => ({
  onSuccess: () => success('investigateHosts.flash.genericFileDownloadRequestSent'),
  onFailure: (message) => context.get('flashMessage').showErrorMessage(message)
});

const stateToComputed = (state, { storeName }) => ({
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
  isProcessDumpDownloadSupported: isProcessDumpDownloadSupported(state),
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  restrictedFileList: state.fileStatus.restrictedFileList,
  fileDownloadButtonStatus: fileDownloadButtonStatus(state, storeName),
  activeHostDetailPropertyTab: state.endpoint.detailsInput.activeHostDetailPropertyTab,
  risk: riskState(state),
  isFloatingOrMemoryDll: isAnyFileFloatingOrMemoryDll(state, storeName),
  hostName: hostName(state),
  isInsightsAgent: isInsightsAgent(state),
  isAgentMigrated: isAgentMigrated(state)
});

const dispatchToActions = {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadProcessDump,
  downloadFilesToServer,
  getFileAnalysisData,
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

  accessControl: service(),

  flashMessage: service(),

  callBackOptions,

  processDumpCallBackOptions,

  @computed('tabName')
  isDisplayTabLabel(tabName) {
    const tabsToDisplayLabels = ['FILE', 'DRIVER', 'LIBRARY'];
    return tabsToDisplayLabels.some((tab) => {
      return tab === tabName;
    });
  },

  @computed('tabName', 'isProcessDumpDownloadSupported')
  showDownloadProcessDump(tabName, isProcessDumpDownloadSupported) {
    const tabsWithDownloadProcessDump = ['LIBRARY', 'IMAGEHOOK', 'THREAD'];
    return this.get('accessControl.endpointCanManageFiles') && tabsWithDownloadProcessDump.includes(tabName) && isProcessDumpDownloadSupported;
  },

  actions: {

    onPropertyPanelClose(side) {
      if (side === 'right') {
        this.send('setRowSelection', this.get('tabName'), null, null);
      }
    },

    onDownloadProcessDump() {
      const fileContextSelections = this.get('fileContextSelections');
      const callBackOptions = this.get('processDumpCallBackOptions')(this);
      const agentId = this.get('agentId');
      this.send('downloadProcessDump', agentId, fileContextSelections, callBackOptions);
    },

    onDownloadFilesToServer() {
      const callBackOptions = this.get('callBackOptions')(this);
      const agentId = this.get('agentId');
      const fileContextSelections = this.get('fileContextSelections');

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
      const [{ checksumSha256, format = '', downloadInfo: { serviceId } }] = fileContextSelections;
      const fileFormat = componentSelectionForFileType(format).format;

      this.analyzeFile(checksumSha256, fileFormat, serviceId, callBackOptions);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ContextWrapper);
