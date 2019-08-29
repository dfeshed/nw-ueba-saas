import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { hostWithStatus,
  mftDownloadButtonStatusDetails,
  isJsonExportCompleted,
  isSnapshotsAvailable,
  agentMigrated,
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
  serverId: state.endpointQuery.selectedMachineServerId,
  isMFTEnabled: mftDownloadButtonStatusDetails(state),
  isAgentMigrated: agentMigrated(state)
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

  @computed('isMFTEnabled')
  moreOptions: {
    get() {
      let subNavItem = {};
      const isMFTEnabled = this.get('isMFTEnabled');

      if (isMFTEnabled.editExclusionList) {
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
      const mftAndIsolation = [
        {
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
              isDisabled: !isMFTEnabled.editExclusionList
            }
          ]
        },
        {
          panelId: 'panel4',
          divider: true,
          name: 'investigateShared.endpoint.fileActions.downloadMFT',
          buttonId: 'downloadMFT-button'
        }
      ];

      const systemMemoryDump = [
        {
          panelId: 'panel5',
          name: 'investigateShared.endpoint.fileActions.downloadSystemDump',
          buttonId: 'downloadSystemDump-button'
        }
      ];

      if (isMFTEnabled.isDisplayed && this.get('accessControl.endpointCanManageFiles')) {
        moreActionOptions.push(...mftAndIsolation, ...systemMemoryDump);
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
      this.send('downloadMFT', this.get('agentId'), this.get('hostDetails').serviceId, callBackOptions);
    },

    requestSystemDumpDownload() {
      const callBackOptions = {
        onSuccess: () => success('investigateHosts.hosts.downloadSystemDump.details.success'),
        onFailure: (message) => failure(message, null, false)
      };
      this.send('downloadSystemDump', this.get('agentId'), this.get('hostDetails').serviceId, callBackOptions);
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

