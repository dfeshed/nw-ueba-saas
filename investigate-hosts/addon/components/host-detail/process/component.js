import Component from '@ember/component';
import { connect } from 'ember-redux';
import CONFIG from './process-property-config';
import {
  getProcessData,
  isNavigatedFromExplore,
  isProcessLoading,
  noProcessData,
  selectedFileChecksums,
  selectedProcessName } from 'investigate-hosts/reducers/details/process/selectors';
import computed from 'ember-computed-decorators';
import { toggleProcessView, setRowIndex } from 'investigate-hosts/actions/data-creators/process';
import { getColumnsConfig } from 'investigate-hosts/reducers/details/selectors';

import summaryItems from './summary-item-config';
import { machineOsType, hostName, isMachineWindows } from 'investigate-hosts/reducers/details/overview/selectors';
import { serviceList } from 'investigate-hosts/reducers/hosts/selectors';
import {
  fileStatus,
  isRemediationAllowed
} from 'investigate-hosts/reducers/details/file-context/selectors';
import {
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadFilesToServer
} from 'investigate-hosts/actions/data-creators/file-context';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { getFileAnalysisData } from 'investigate-shared/actions/data-creators/file-analysis-creators';

const stateToComputed = (state) => ({
  isTreeView: state.endpoint.visuals.isTreeView,
  agentId: state.endpoint.detailsInput.agentId,
  process: getProcessData(state),
  isNavigatedFromExplore: isNavigatedFromExplore(state),
  summaryConfig: getColumnsConfig(state, summaryItems),
  isProcessLoading: isProcessLoading(state),
  isProcessDataEmpty: noProcessData(state),
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
  timeRange: timeRange(state)
});

const dispatchToActions = {
  toggleProcessView,
  setRowIndex,
  setFileContextFileStatus,
  getFileContextFileStatus,
  retrieveRemediationStatus,
  downloadFilesToServer,
  getFileAnalysisData
};

const Container = Component.extend({

  tagName: 'box',

  classNames: ['host-process-info', 'host-process-wrapper'],

  propertyConfig: CONFIG,

  tabName: 'PROCESS',

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

  @computed('selectedProcessList')
  fileDownloadButtonStatus(fileContextSelections = []) {
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

    onPropertyPanelClose() {
      this.send('setRowIndex', null);
    },

    resetRiskScoreAction() {
      // Placeholder for the next PR.
    },

    onDownloadFilesToServer() {
      const callBackOptions = {
        onSuccess: () => success('investigateHosts.flash.fileDownloadRequestSent'),
        onFailure: (message) => failure(message)
      };
      const { agentId, selectedProcessList } = this.getProperties('agentId', 'selectedProcessList');

      this.send('downloadFilesToServer', agentId, selectedProcessList, callBackOptions);
    },

    onSaveLocalCopy() {
      // Placeholder for the next PR.
    },

    onAnalyzeFile() {
      // Open analyze file.
      const selectedProcessList = this.get('selectedProcessList');
      const { checksumSha256 } = selectedProcessList;
      this.send('getFileAnalysisData', checksumSha256);
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(Container);
