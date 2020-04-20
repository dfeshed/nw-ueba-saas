import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { hostWithStatus,
  mftDownloadButtonStatusDetails,
  isolationStatus,
  isJsonExportCompleted,
  isSnapshotsAvailable,
  isAgentMigrated,
  manualFileDownloadVisualStatus,
  hostName } from 'investigate-hosts/reducers/details/overview/selectors';
import { exportFileContext } from 'investigate-hosts/actions/data-creators/details';
import { downloadMFT, downloadSystemDump } from 'investigate-hosts/actions/data-creators/host';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { isScanStartButtonDisabled } from 'investigate-hosts/reducers/hosts/selectors';

const stateToComputed = (state) => ({
  hostDetails: hostWithStatus(state),
  hostName: hostName(state),
  isJsonExportCompleted: isJsonExportCompleted(state),
  isExportDisabled: !isSnapshotsAvailable(state),
  scanTime: state.endpoint.detailsInput.scanTime,
  agentId: state.endpoint.detailsInput.agentId,
  isScanStartButtonDisabled: isScanStartButtonDisabled(state),
  isMFTEnabled: mftDownloadButtonStatusDetails(state),
  isAgentMigrated: isAgentMigrated(state),
  isolationStatus: isolationStatus(state),
  manualFileDownloadVisualStatus: manualFileDownloadVisualStatus(state)
});

const dispatchToActions = {
  exportFileContext,
  downloadMFT,
  downloadSystemDump
};

@classic
@classNames('host_more_actions')
class HostDetailsMoreActions extends Component {
  @service
  accessControl;

  showIsolationModal = false;
  downloadConfig = null;
  showDownloadModal = false;
  isManualFileDownloadModalVisible = false;

  @computed('isMFTEnabled')
  get moreOptions() {
    let subNavItem = {};
    const { manualFileDownloadVisualStatus, isMFTEnabled, isolationStatus } = this;

    if (isolationStatus.isIsolated) {
      subNavItem = {
        modalName: 'release',
        name: 'investigateHosts.networkIsolation.menu.releaseFromIsolation',
        buttonId: 'release-isolation-button',
        isDisabled: false
      };
    } else {
      subNavItem = {
        modalName: 'isolate',
        name: 'investigateHosts.networkIsolation.menu.isolate',
        buttonId: 'isolation-button',
        isDisabled: false
      };
    }
    // Isolation is added for Windows only if it has been enabled in policy.
    const networkIsolation = {
      panelId: 'panel3',
      divider: true,
      name: 'investigateHosts.networkIsolation.menu.networkIsolation',
      buttonId: 'isolation-button',
      subItems: [
        subNavItem,
        {
          modalName: 'edit',
          name: 'investigateHosts.networkIsolation.menu.edit',
          buttonId: 'isolation-button',
          isDisabled: !isolationStatus.isIsolated
        }
      ]
    };

    const moreActionOptions = [
      {
        panelId: 'panel1',
        name: 'investigateHosts.hosts.button.resetRiskScore',
        buttonId: 'startScan-button'
      },
      {
        panelId: 'panel2',
        name: 'investigateHosts.hosts.button.delete',
        buttonId: 'export-button'
      }
    ];

    // Windows specific actions, MFT, Isolation and System Dump
    const windowsOsActions = [
      {
        panelId: 'panel4',
        divider: true,
        name: 'investigateShared.endpoint.fileActions.downloadMFT',
        buttonId: 'downloadMFT-button'
      },
      {
        panelId: 'panel5',
        name: 'investigateShared.endpoint.fileActions.downloadSystemDump',
        buttonId: 'downloadSystemDump-button'
      }
    ];

    const manualFileDownload = {
      panelId: 'panel6',
      name: 'investigateHosts.hosts.button.manualFileDownload',
      buttonId: 'manualFileDownload-button'
    };

    if (this.get('accessControl.endpointCanManageFiles')) {
      if (isolationStatus.isIsolationEnabled) {
        moreActionOptions.push(networkIsolation);
      }
      if (isMFTEnabled.isDisplayed) {
        moreActionOptions.push(...windowsOsActions);
      }
      if (manualFileDownloadVisualStatus.isDisplayed) {
        moreActionOptions.push(manualFileDownload);
      }
    }
    return moreActionOptions;
  }

  set moreOptions(value) {
    return value;
  }

  @action
  exportHostDetails() {
    const scanTime = this.get('scanTime');
    const agentId = this.get('agentId');
    this.send('exportFileContext', { agentId, scanTime, categories: ['AUTORUNS'] });
  }

  @action
  requestMFTDownload() {
    const callBackOptions = {
      onSuccess: () => success('investigateHosts.hosts.download.details.success', { label: 'MFT' }),
      onFailure: (message) => failure(message, null, false)
    };
    this.set('downloadConfig', {
      type: 'mftDownload',
      agentId: this.get('agentId'),
      serviceId: this.get('hostDetails').serviceId,
      callBackOptions
    });
    this.send('handleDownloadModal');
  }

  @action
  requestSystemDumpDownload() {
    const callBackOptions = {
      onSuccess: () => success('investigateHosts.hosts.download.details.success', { label: 'System dump' }),
      onFailure: (message) => failure(message, null, false)
    };
    this.set('downloadConfig', {
      type: 'systemDump',
      agentId: this.get('agentId'),
      serviceId: this.get('hostDetails').serviceId,
      callBackOptions
    });
    this.send('handleDownloadModal');
  }

  @action
  onShowManualFileDownloadModal() {
    this.set('isManualFileDownloadModalVisible', true);
  }

  @action
  hideManualFileDownloadModal() {
    this.set('isManualFileDownloadModalVisible', false);
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
  continueDownload() {
    localStorage.setItem('doNotShowDownloadModal', this.get('downloadModalCheckbox'));
    this.set('showDownloadModal', false);
    this.send('dumpDownload');
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

export default connect(stateToComputed, dispatchToActions)(HostDetailsMoreActions);

