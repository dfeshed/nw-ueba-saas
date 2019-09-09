import Component from '@ember/component';
import { connect } from 'ember-redux';
import CONFIG from './process-property-config';
import { inject as service } from '@ember/service';
import {
  getProcessData,
  isNavigatedFromExplore,
  isProcessLoading,
  selectedFileChecksums,
  selectedProcessName } from 'investigate-hosts/reducers/details/process/selectors';
import computed from 'ember-computed-decorators';
import { toggleProcessView, setRowIndex } from 'investigate-hosts/actions/data-creators/process';
import { setHostDetailPropertyTab } from 'investigate-hosts/actions/data-creators/details';
import { getUpdatedRiskScoreContext } from 'investigate-shared/actions/data-creators/risk-creators';
import { getColumnsConfig, hostDetailPropertyTabs, isProcessDumpDownloadSupported } from 'investigate-hosts/reducers/details/selectors';
import { riskState } from 'investigate-hosts/reducers/visuals/selectors';

import summaryItems from './summary-item-config';
import { machineOsType, hostName, isMachineWindows } from 'investigate-hosts/reducers/details/overview/selectors';
import { serviceList, isInsightsAgent, isAgentMigrated } from 'investigate-hosts/reducers/hosts/selectors';
import {
  fileStatus,
  isRemediationAllowed,
  fileDownloadButtonStatus,
  isAnyFileFloatingOrMemoryDll
} from 'investigate-hosts/reducers/details/file-context/selectors';
import {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadProcessDump,
  downloadFilesToServer
} from 'investigate-hosts/actions/data-creators/file-context';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success } from 'investigate-shared/utils/flash-messages';
import { saveLocalFileCopy } from 'investigate-shared/actions/data-creators/file-analysis-creators';
import { componentSelectionForFileType } from 'investigate-shared/utils/file-analysis-view-util';

const callBackOptions = (context) => ({
  onSuccess: () => success('investigateHosts.flash.fileDownloadRequestSent'),
  onFailure: (message) => context.get('flashMessage').showErrorMessage(message)
});

const processDumpCallBackOptions = (context) => ({
  onSuccess: () => success('investigateHosts.flash.genericFileDownloadRequestSent'),
  onFailure: (message) => context.get('flashMessage').showErrorMessage(message)
});

const stateToComputed = (state) => ({
  isTreeView: state.endpoint.visuals.isTreeView,
  hostDetailPropertyTabs: hostDetailPropertyTabs(state),
  activeHostDetailPropertyTab: state.endpoint.detailsInput.activeHostDetailPropertyTab,
  agentId: state.endpoint.detailsInput.agentId,
  isProcessDumpDownloadSupported: isProcessDumpDownloadSupported(state),
  process: getProcessData(state),
  isNavigatedFromExplore: isNavigatedFromExplore(state),
  summaryConfig: getColumnsConfig(state, summaryItems),
  isProcessLoading: isProcessLoading(state),
  isMachineWindows: isMachineWindows(state),
  selectedProcessList: state.endpoint.process.selectedProcessList,
  selectedProcessName: selectedProcessName(state),
  selectedFileChecksums: selectedFileChecksums(state),
  fileStatus: fileStatus(state, 'processes'),
  osType: machineOsType(state),
  hostName: hostName(state),
  serviceList: serviceList(state),
  restrictedFileList: state.fileStatus.restrictedFileList,
  isRemediationAllowed: isRemediationAllowed(state, 'processes'),
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  risk: riskState(state),
  fileDownloadButtonStatus: fileDownloadButtonStatus(state, 'process'),
  isFloatingOrMemoryDll: isAnyFileFloatingOrMemoryDll(state, 'process'),
  isInsightsAgent: isInsightsAgent(state),
  isAgentMigrated: isAgentMigrated(state)
});

const dispatchToActions = {
  toggleProcessView,
  setRowIndex,
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadProcessDump,
  downloadFilesToServer,
  setHostDetailPropertyTab,
  getUpdatedRiskScoreContext,
  saveLocalFileCopy
};

const Container = Component.extend({

  tagName: 'box',

  classNames: ['host-process-info', 'host-process-wrapper'],

  accessControl: service(),

  propertyConfig: CONFIG,

  tabName: 'PROCESS',

  flashMessage: service(),

  callBackOptions,

  processDumpCallBackOptions,

  @computed('isProcessDumpDownloadSupported')
  showDownloadProcessDump(isProcessDumpDownloadSupported) {
    return (this.get('accessControl.endpointCanManageFiles') && isProcessDumpDownloadSupported);
  },

  @computed('process')
  loadedDLLNote({ machineOsType }) {
    if (machineOsType && machineOsType !== 'linux') {
      const i18n = this.get('i18n');
      return i18n.t(`investigateHosts.process.dll.note.${machineOsType}`);
    } else {
      return '';
    }
  },

  @computed('selectedProcessList')
  selectedFileCount(selectedProcessList) {
    return selectedProcessList && selectedProcessList.length ? selectedProcessList.length : 0;
  },

  @computed('isTreeView')
  treeIconTooltip(isTreeView) {
    const toolTipLabel = isTreeView ? 'listView' : 'treeView';
    const i18n = this.get('i18n');
    return i18n.t(`investigateHosts.process.toolTip.${toolTipLabel}`);
  },

  actions: {
    toggleView(closePanel) {
      closePanel();
      this.send('toggleProcessView');
    },

    onPropertyPanelClose(side) {
      if (side === 'right') {
        this.send('setRowIndex', null);
      }
    },

    onDownloadProcessDump() {
      const selectedProcessList = this.get('selectedProcessList');
      const callBackOptions = this.get('processDumpCallBackOptions')(this);
      const agentId = this.get('agentId');
      this.send('downloadProcessDump', agentId, selectedProcessList, callBackOptions);
    },

    onDownloadFilesToServer() {
      const callBackOptions = this.get('callBackOptions')(this);
      const agentId = this.get('agentId');
      const selectedProcessList = this.get('selectedProcessList');

      this.send('downloadFilesToServer', agentId, selectedProcessList, callBackOptions);
    },

    onSaveLocalCopy() {
      const callBackOptions = this.get('callBackOptions')(this);
      this.send('saveLocalFileCopy', this.get('selectedProcessList')[0], callBackOptions);
    },

    onAnalyzeFile() {
      const callBackOptions = this.get('callBackOptions')(this);
      // Open analyze file.
      const selectedProcessList = this.get('selectedProcessList');
      const [{ checksumSha256, format = '', downloadInfo: { serviceId } }] = selectedProcessList;
      const fileFormat = componentSelectionForFileType(format).format;

      this.analyzeFile(checksumSha256, fileFormat, serviceId, callBackOptions);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(Container);
