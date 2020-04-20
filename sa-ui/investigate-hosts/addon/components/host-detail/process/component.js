import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import CONFIG from './process-property-config';
import { next } from '@ember/runloop';
import {
  getProcessData,
  isNavigatedFromExplore,
  isProcessLoading,
  selectedFileChecksums,
  selectedFileHostCount,
  selectedProcessName } from 'investigate-hosts/reducers/details/process/selectors';
import { toggleProcessView, setRowIndex } from 'investigate-hosts/actions/data-creators/process';
import { setHostDetailPropertyTab, applyDetailsFilter } from 'investigate-hosts/actions/data-creators/details';
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
  isAnyFileFloatingOrMemoryDll,
  hostNameList
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
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';
import { toggleHostDetailsFilter } from 'investigate-hosts/actions/ui-state-creators';
import {
  resetFilters
} from 'investigate-shared/actions/data-creators/filter-creators';

import { FILTER_TYPES } from './filter-types';

const callBackOptions = (context) => ({
  onSuccess: () => success('investigateHosts.flash.fileDownloadRequestSent'),
  onFailure: (message) => context.get('flashMessage').showErrorMessage(message)
});

const processDumpCallBackOptions = (context) => ({
  onSuccess: () => success('investigateHosts.hosts.download.details.success', { label: 'Process dump' }),
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
  selectedFileHostCount: selectedFileHostCount(state),
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
  isAgentMigrated: isAgentMigrated(state),
  isShowOpenFilterButton: !state.endpoint.visuals.showHostDetailsFilter,
  filter: state.endpoint.details.filter,
  selectedFilterId: selectedFilterId(state.endpoint.details),
  savedFilter: savedFilter(state.endpoint.details),
  hostDetailFilters: state.endpoint.details.filter.savedFilterList,
  hostNameList: hostNameList(state, 'processes'),
  selectedMachineServerId: state.endpointQuery.selectedMachineServerId,
  serverId: state.endpointQuery.serverId
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
  saveLocalFileCopy,
  toggleHostDetailsFilter,
  resetFilters,
  applyDetailsFilter
};

@classic
@tagName('box')
@classNames('host-process-info', 'host-process-wrapper')
class Container extends Component {
  @service
  accessControl;

  propertyConfig = CONFIG;
  tabName = 'PROCESS';

  @service
  flashMessage;

  @service
  pivot;

  callBackOptions = callBackOptions;
  processDumpCallBackOptions = processDumpCallBackOptions;
  filterTypes = FILTER_TYPES;

  @computed('isProcessDumpDownloadSupported')
  get showDownloadProcessDump() {
    return this.get('accessControl.endpointCanManageFiles') && this.isProcessDumpDownloadSupported;
  }

  @computed('process')
  get loadedDLLNote() {
    if (this.process.machineOsType && this.process.machineOsType !== 'linux') {
      const i18n = this.get('i18n');
      return i18n.t(`investigateHosts.process.dll.note.${this.process.machineOsType}`);
    } else {
      return '';
    }
  }

  @computed('selectedProcessList')
  get selectedFileCount() {
    return this.selectedProcessList && this.selectedProcessList.length ? this.selectedProcessList.length : 0;
  }

  @computed('isTreeView')
  get treeIconTooltip() {
    const toolTipLabel = this.isTreeView ? 'listView' : 'treeView';
    const i18n = this.get('i18n');
    return i18n.t(`investigateHosts.process.toolTip.${toolTipLabel}`);
  }

  @action
  toggleView(closePanel) {
    closePanel();
    this.send('toggleProcessView');
  }

  @action
  onPropertyPanelClose(side) {
    if (side === 'right') {
      this.send('setRowIndex', null);
    }
    if (side === 'left') {
      this.send('toggleHostDetailsFilter', false);
    }
  }

  @action
  onDownloadProcessDump() {
    const selectedProcessList = this.get('selectedProcessList');
    const callBackOptions = this.get('processDumpCallBackOptions')(this);
    const agentId = this.get('agentId');
    this.send('downloadProcessDump', agentId, selectedProcessList, callBackOptions);
  }

  @action
  onDownloadFilesToServer() {
    const callBackOptions = this.get('callBackOptions')(this);
    const { selectedMachineServerId, agentId, selectedProcessList } = this;

    this.send('downloadFilesToServer', agentId, selectedProcessList, selectedMachineServerId, callBackOptions);
  }

  @action
  onSaveLocalCopy() {
    const callBackOptions = this.get('callBackOptions')(this);
    this.send('saveLocalFileCopy', this.get('selectedProcessList')[0], callBackOptions);
  }

  @action
  onAnalyzeFile() {
    const callBackOptions = this.get('callBackOptions')(this);
    // Open analyze file.
    const selectedProcessList = this.get('selectedProcessList');
    const [{ checksumSha256, format = '', downloadInfo: { serviceId } }] = selectedProcessList;
    const fileFormat = componentSelectionForFileType(format).format;

    this.analyzeFile(checksumSha256, fileFormat, serviceId, callBackOptions);
  }

  @action
  openFilterPanel(closeFilterPanel) {
    closeFilterPanel();
    this.send('toggleHostDetailsFilter', true);
  }

  @action
  applyFilters(expressionList, filterType) {
    next(() => {
      this.send('applyDetailsFilter', expressionList, filterType);
    });
  }

  @action
  onHostNameClick(target, item) {
    if ('HOST_NAME' === target) {
      const serverId = this.get('serverId');
      window.open(`${window.location.origin}/investigate/hosts/${item.agentId.toUpperCase()}/OVERVIEW?sid=${serverId}`);
    } else if ('PIVOT_ICON' === target) {
      this.get('pivot').pivotToInvestigate('machineIdentity.machineName', { machineIdentity: { machineName: item } });
    }
  }
}

export default connect(stateToComputed, dispatchToActions)(Container);
