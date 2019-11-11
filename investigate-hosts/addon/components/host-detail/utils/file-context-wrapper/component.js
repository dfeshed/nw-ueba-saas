import Component from '@ember/component';
import { connect } from 'ember-redux';
import { serviceList, isInsightsAgent } from 'investigate-hosts/reducers/hosts/selectors';
import { inject as service } from '@ember/service';
import computed from 'ember-computed-decorators';
import { next } from '@ember/runloop';
import {
  resetFilters
} from 'investigate-shared/actions/data-creators/filter-creators';
import { selectedFilterId, savedFilter } from 'investigate-shared/selectors/endpoint-filters/selectors';

import {
  fileContextFileProperty,
  fileContextSelections,
  fileStatus,
  selectedFileChecksums,
  selectedFileHostCount,
  isRemediationAllowed,
  fileDownloadButtonStatus,
  focusedRowChecksum,
  selectedFileList,
  isAnyFileFloatingOrMemoryDll,
  hostNameList
} from 'investigate-hosts/reducers/details/file-context/selectors';
import { hostDetailPropertyTabs, isProcessDumpDownloadSupported } from 'investigate-hosts/reducers/details/selectors';
import { hostName, isAgentMigrated } from 'investigate-hosts/reducers/details/overview/selectors';
import { setHostDetailPropertyTab, applyDetailsFilter } from 'investigate-hosts/actions/data-creators/details';
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
import { riskState, getAutorunTabs, selectedAutorunTab } from 'investigate-hosts/reducers/visuals/selectors';
import { componentSelectionForFileType } from 'investigate-shared/utils/file-analysis-view-util';
import { toggleHostDetailsFilter } from 'investigate-hosts/actions/ui-state-creators';
import { FILTER_TYPES } from './filter-types';

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
  selectedFileHostCount: selectedFileHostCount(state, storeName),
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
  isAgentMigrated: isAgentMigrated(state),
  autorunTabs: getAutorunTabs(state),
  selectedAutorunTab: selectedAutorunTab(state),
  isShowOpenFilterButton: !state.endpoint.visuals.showHostDetailsFilter,
  filter: state.endpoint.details.filter,
  selectedFilterId: selectedFilterId(state.endpoint.details),
  savedFilter: savedFilter(state.endpoint.details),
  hostDetailFilters: state.endpoint.details.filter.savedFilterList,
  hostNameList: hostNameList(state, storeName),
  serverId: state.endpointQuery.serverId
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
  setRowSelection,
  toggleHostDetailsFilter,
  resetFilters,
  applyDetailsFilter
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

  filterTypes: FILTER_TYPES,

  pivot: service(),

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
      if (side === 'left') {
        this.send('toggleHostDetailsFilter', false);
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
    },
    openFilterPanel(openFilterPanel) {
      openFilterPanel();
      this.send('toggleHostDetailsFilter', true);
    },

    applyFilters(expressionList, filterType) {
      next(() => {
        this.send('applyDetailsFilter', expressionList, filterType);
      });
    },
    onHostNameClick(target, item) {
      if ('HOST_NAME' === target) {
        const serverId = this.get('serverId');
        window.open(`${window.location.origin}/investigate/hosts/${item.agentId.toUpperCase()}/OVERVIEW?sid=${serverId}`);
      } else if ('PIVOT_ICON' === target) {
        this.get('pivot').pivotToInvestigate('machineIdentity.machineName', { machineIdentity: { machineName: item } });
      }
    }
  }

});

export default connect(stateToComputed, dispatchToActions)(ContextWrapper);
