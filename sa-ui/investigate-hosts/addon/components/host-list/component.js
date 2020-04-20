import classic from 'ember-classic-decorator';
import { classNames, tagName } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';

import { startScanCommand, stopScanCommand } from 'investigate-hosts/util/scan-command';
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


@classic
@tagName('')
@classNames('host-list show-more-filter main-zone')
class Container extends Component {
  scanCommand = null;
  showCommandModal = false;
  showConfirmationModal = false;
  showResetScoreModal = false;

  @service
  timezone;

  @service
  pivot;

  downloadConfig = null;
  showDownloadModal = false;

  @computed('selections')
  get isMaxResetRiskScoreLimit() {
    return this.selections.length > 100;
  }

  @computed('scanCommand')
  get modalTitle() {
    const i18n = this.get('i18n');
    return this.scanCommand === 'START_SCAN' ?
      i18n.t('investigateShared.endpoint.hostActions.startScan') :
      i18n.t('investigateShared.endpoint.hostActions.stopScan');
  }

  @action
  pivotToInvestigate(item, category) {
    this.get('pivot').pivotToInvestigate('machineIdentity.machineName', item, category);
  }

  @action
  initiateScanCommand(command) {
    const hosts = this.get('selections');
    const [host] = hosts.length && hosts.length === 1 ? hosts : [{}];
    if (command === 'START_SCAN') {
      startScanCommand(this.get('agentIds'), host.serviceId);
    } else {
      stopScanCommand(this.get('agentIds'), host.serviceId);
    }
    this.set('showCommandModal', false);
  }

  @action
  handleDeleteHosts() {
    const callBackOptions = {
      onSuccess: () => success('investigateHosts.hosts.deleteHosts.success'),
      onFailure: () => failure('investigateHosts.hosts.deleteHosts.failure')
    };

    this.closeProperties();
    this.send('deleteHosts', callBackOptions);
    this.set('showConfirmationModal', false);
  }

  @action
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
  }

  @action
  showScanModal(command) {
    this.set('scanCommand', command);
    this.set('showCommandModal', true);
  }

  @action
  hideScanModal() {
    this.set('showCommandModal', false);
  }

  @action
  onshowConfirmationModal() {
    this.set('showConfirmationModal', true);
  }

  @action
  hideConfirmationModal() {
    this.set('showConfirmationModal', false);
  }

  @action
  showRiskScoreModal() {
    this.set('showResetScoreModal', true);
  }

  @action
  onResetScoreModalClose() {
    this.set('showResetScoreModal', false);
  }

  @action
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
  }

  @action
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
  }

  @action
  continueDownload() {
    localStorage.setItem('doNotShowDownloadModal', this.get('downloadModalCheckbox'));
    this.set('showDownloadModal', false);
    this.send('dumpDownload');
  }

  @action
  handleDownloadModal() {
    const doNotShowDownloadModal = localStorage.getItem('doNotShowDownloadModal');
    if (doNotShowDownloadModal && doNotShowDownloadModal === 'true') {
      this.send('dumpDownload');
    } else {
      this.set('downloadModalCheckbox', false);
      this.set('showDownloadModal', true);
    }

  }

  @action
  hideDownloadMsgModal() {
    this.set('showDownloadModal', false);
  }

  @action
  toggleShowDownloadMsg() {
    this.toggleProperty('downloadModalCheckbox');
  }

  @action
  dumpDownload() {
    const downloadConfig = this.get('downloadConfig');
    const { type, agentId, serviceId, callBackOptions } = downloadConfig;
    if (type === 'systemDump') {
      this.send('downloadSystemDump', agentId, serviceId, callBackOptions);
    } else {
      this.send('downloadMFT', agentId, serviceId, callBackOptions);
    }
  }

  @action
  onshowIsolationModal(item) {
    this.set('showIsolationModal', true);
    this.set('selectedModal', item);
  }

  @action
  hideIsolationModal() {
    this.set('showIsolationModal', false);
  }
}

export default connect(stateToComputed, dispatchToActions)(Container);
