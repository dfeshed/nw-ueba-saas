import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { connect } from 'ember-redux';
import { hostWithStatus,
  mftDownloadButtonStatusDetails,
  isJsonExportCompleted,
  isSnapshotsAvailable,
  hostName } from 'investigate-hosts/reducers/details/overview/selectors';
import { exportFileContext } from 'investigate-hosts/actions/data-creators/details';
import { downloadMFT } from 'investigate-hosts/actions/data-creators/host';
import { success, failure } from 'investigate-shared/utils/flash-messages';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => ({
  hostDetails: hostWithStatus(state),
  hostName: hostName(state),
  isJsonExportCompleted: isJsonExportCompleted(state),
  isExportDisabled: !isSnapshotsAvailable(state),
  scanTime: state.endpoint.detailsInput.scanTime,
  agentId: state.endpoint.detailsInput.agentId,
  isMFTEnabled: mftDownloadButtonStatusDetails(state)
});

const dispatchToActions = {
  exportFileContext,
  downloadMFT
};

const HostDetailsMoreActions = Component.extend({

  classNames: ['host_more_actions'],

  accessControl: service(),

  @computed('isMFTEnabled')
  moreOptions() {
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
    const mft = [
      {
        panelId: 'panel3',
        divider: true,
        name: 'investigateShared.endpoint.fileActions.downloadMFT',
        buttonId: 'downloadMFT-button'
      }
    ];
    if (this.get('isMFTEnabled').isDisplayed && this.get('accessControl.endpointCanManageFiles')) {
      moreActionOptions.push(...mft);
    }
    return moreActionOptions;
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(HostDetailsMoreActions);

