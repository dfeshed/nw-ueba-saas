import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { hostWithStatus,
  mftDownloadButtonStatusDetails,
  isJsonExportCompleted,
  isSnapshotsAvailable,
  isAgentMigrated,
  hostName } from 'investigate-hosts/reducers/details/overview/selectors';
import { exportFileContext } from 'investigate-hosts/actions/data-creators/details';
import { downloadMFT, downloadSystemDump } from 'investigate-hosts/actions/data-creators/host';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => ({
  hostDetails: hostWithStatus(state),
  hostName: hostName(state),
  isJsonExportCompleted: isJsonExportCompleted(state),
  isExportDisabled: !isSnapshotsAvailable(state),
  scanTime: state.endpoint.detailsInput.scanTime,
  agentId: state.endpoint.detailsInput.agentId,
  isMFTEnabled: mftDownloadButtonStatusDetails(state),
  isAgentMigrated: isAgentMigrated(state)
});

const dispatchToActions = {
  exportFileContext,
  downloadMFT,
  downloadSystemDump
};

const HostDetailsMoreActions = Component.extend({

  classNames: ['host_more_actions'],

  accessControl: service(),

  showIsolationModal: false,

  downloadConfig: null,

  showDownloadModal: false,

  @computed('isMFTEnabled')
  moreOptions: {
    get() {
      let subNavItem = {};
      const isMFTEnabled = this.get('isMFTEnabled');

      if (isMFTEnabled.isIsolated) {
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
      // Separating network isolation as an additional check will be added for this in future.
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
            isDisabled: !isMFTEnabled.isIsolated
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
        networkIsolation,
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

      if (isMFTEnabled.isDisplayed && this.get('accessControl.endpointCanManageFiles')) {
        moreActionOptions.push(...windowsOsActions);
      }
      return moreActionOptions;
    },
    set(value) {
      return value;
    }
  },

  actions: {
    exportHostDetails() {
      const scanTime = this.get('scanTime');
      const agentId = this.get('agentId');
      this.send('exportFileContext', { agentId, scanTime, categories: ['AUTORUNS'] });
    },

    requestMFTDownload() {
      const callBackOptions = {
        onSuccess: () => success('investigateHosts.hosts.downloadMFT.success'),
        onFailure: (message) => failure(message, null, false)
      };
      this.set('downloadConfig', {
        type: 'mftDownload',
        agentId: this.get('agentId'),
        serviceId: this.get('hostDetails').serviceId,
        callBackOptions
      });
      this.send('handleDownloadModal');
    },

    requestSystemDumpDownload() {
      const callBackOptions = {
        onSuccess: () => success('investigateHosts.hosts.downloadSystemDump.details.success'),
        onFailure: (message) => failure(message, null, false)
      };
      this.set('downloadConfig', {
        type: 'systemDump',
        agentId: this.get('agentId'),
        serviceId: this.get('hostDetails').serviceId,
        callBackOptions
      });
      this.send('handleDownloadModal');
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

    continueDownload() {
      localStorage.setItem('doNotShowDownloadModal', this.get('downloadModalCheckbox'));
      this.set('showDownloadModal', false);
      this.send('dumpDownload');
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

export default connect(stateToComputed, dispatchToActions)(HostDetailsMoreActions);

