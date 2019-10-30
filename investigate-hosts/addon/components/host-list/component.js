import { connect } from 'ember-redux';
import Component from '@ember/component';
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';

import { startScanCommand, stopScanCommand } from 'investigate-hosts/util/scan-command';
import { inject as service } from '@ember/service';
import {
  isScanStartButtonDisabled,
  warningMessages,
  extractAgentIds,
  mftDownloadButtonStatus,
  isAgentMigrated,
  selectedHostDetails } from 'investigate-hosts/reducers/hosts/selectors';
import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';
import { deleteHosts, downloadMFT, downloadSystemDump } from 'investigate-hosts/actions/data-creators/host';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  selectedServiceData: selectedServiceWithStatus(state),
  selections: state.endpoint.machines.selectedHostList || [],
  warningMessages: warningMessages(state),
  isScanStartButtonDisabled: isScanStartButtonDisabled(state),
  agentIds: extractAgentIds(state),
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  isMFTEnabled: mftDownloadButtonStatus(state),
  isAgentMigrated: isAgentMigrated(state),
  serverId: state.endpointQuery.serverId,
  selectedHostList: state.endpoint.machines.selectedHostList,
  hostDetails: selectedHostDetails(state)
});

const dispatchToActions = {
  deleteHosts,
  resetRiskScore,
  downloadMFT,
  downloadSystemDump
};


const Container = Component.extend({

  tagName: '',

  classNames: 'host-list show-more-filter main-zone',

  scanCommand: null,

  showCommandModal: false,

  showConfirmationModal: false,

  showResetScoreModal: false,

  timezone: service(),

  pivot: service(),

  downloadConfig: null,

  showDownloadModal: false,

  @computed('selections')
  isMaxResetRiskScoreLimit(selectedList) {
    return selectedList.length > 100;
  },

  @computed('scanCommand')
  modalTitle(command) {
    const i18n = this.get('i18n');
    return command === 'START_SCAN' ?
      i18n.t('investigateShared.endpoint.hostActions.startScan') :
      i18n.t('investigateShared.endpoint.hostActions.stopScan');
  },
  actions: {
    pivotToInvestigate(item, category) {
      this.get('pivot').pivotToInvestigate('machineIdentity.machineName', item, category);
    },

    initiateScanCommand(command) {
      const hosts = this.get('selections');
      const [host] = hosts.length && hosts.length === 1 ? hosts : [{}];
      if (command === 'START_SCAN') {
        startScanCommand(this.get('agentIds'), host.serviceId);
      } else {
        stopScanCommand(this.get('agentIds'), host.serviceId);
      }
      this.set('showCommandModal', false);
    },

    handleDeleteHosts() {
      const callBackOptions = {
        onSuccess: () => success('investigateHosts.hosts.deleteHosts.success'),
        onFailure: () => failure('investigateHosts.hosts.deleteHosts.failure')
      };
      this.closeProperties();
      this.send('deleteHosts', callBackOptions);
      this.set('showConfirmationModal', false);
    },

    handleResetHostsRiskScore() {
      const limitedFiles = this.get('selections').slice(0, 100);
      const callBackOptions = {
        onSuccess: (response) => {
          const { data } = response;
          if (data === limitedFiles.length) {
            success('investigateHosts.hosts.resetHosts.success');
          } else {
            warning('investigateHosts.hosts.resetHosts.warning');
          }
        },
        onFailure: () => failure('investigateHosts.hosts.resetHosts.error')
      };
      this.send('resetRiskScore', limitedFiles, 'HOST', callBackOptions);
      this.set('showResetScoreModal', false);
    },

    showScanModal(command) {
      this.set('scanCommand', command);
      this.set('showCommandModal', true);
    },

    hideScanModal() {
      this.set('showCommandModal', false);
    },

    showConfirmationModal() {
      this.set('showConfirmationModal', true);
    },

    hideConfirmationModal() {
      this.set('showConfirmationModal', false);
    },

    showRiskScoreModal() {
      this.set('showResetScoreModal', true);
    },

    onResetScoreModalClose() {
      this.set('showResetScoreModal', false);
    },

    requestMFTDownload() {
      const callBackOptions = {
        onSuccess: () => success('investigateHosts.hosts.download.success', { label: 'MFT' }),
        onFailure: (message) => failure(message, null, false)
      };
      const hosts = this.get('selections');
      const [host] = hosts.length && hosts.length === 1 ? hosts : [{}];
      this.set('downloadConfig', {
        type: 'mftDownload',
        agentId: this.get('agentIds')[0],
        serviceId: host.serviceId,
        callBackOptions
      });
      this.send('handleDownloadModal');
    },

    requestSystemDumpDownload() {
      const callBackOptions = {
        onSuccess: () => success('investigateHosts.hosts.download.success', { label: 'System dump' }),
        onFailure: (message) => failure(message, null, false)
      };
      const hosts = this.get('selections');
      const [host] = hosts.length && hosts.length === 1 ? hosts : [{}];
      this.set('downloadConfig', {
        type: 'systemDump',
        agentId: this.get('agentIds')[0],
        serviceId: host.serviceId,
        callBackOptions
      });
      this.send('handleDownloadModal');
    },

    continueDownload() {
      localStorage.setItem('doNotShowDownloadModal', this.get('downloadModalCheckbox'));
      this.set('showDownloadModal', false);
      this.send('dumpDownload');
    },

    handleDownloadModal() {
      const doNotShowDownloadModal = localStorage.getItem('doNotShowDownloadModal');
      if (doNotShowDownloadModal && doNotShowDownloadModal === 'true') {
        this.send('dumpDownload');
      } else {
        this.set('downloadModalCheckbox', false);
        this.set('showDownloadModal', true);
      }

    },

    hideDownloadMsgModal() {
      this.set('showDownloadModal', false);
    },

    toggleShowDownloadMsg() {
      this.toggleProperty('downloadModalCheckbox');
    },

    dumpDownload() {
      const downloadConfig = this.get('downloadConfig');
      const { type, agentId, serviceId, callBackOptions } = downloadConfig;
      if (type === 'systemDump') {
        this.send('downloadSystemDump', agentId, serviceId, callBackOptions);
      } else {
        this.send('downloadMFT', agentId, serviceId, callBackOptions);
      }
    },
    showIsolationModal(item) {
      this.set('showIsolationModal', true);
      this.set('selectedModal', item);
    },

    hideIsolationModal() {
      this.set('showIsolationModal', false);
    }

  }
});

export default connect(stateToComputed, dispatchToActions)(Container);
