import { connect } from 'ember-redux';
import Component from '@ember/component';
import { selectedServiceWithStatus } from 'investigate-shared/selectors/endpoint-server/selectors';

import { startScanCommand, stopScanCommand } from 'investigate-hosts/util/scan-command';
import { inject as service } from '@ember/service';
import { isScanStartButtonDisabled, warningMessages, extractAgentIds } from 'investigate-hosts/reducers/hosts/selectors';
import { resetRiskScore } from 'investigate-shared/actions/data-creators/risk-creators';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';
import { success, failure, warning } from 'investigate-shared/utils/flash-messages';
import { deleteHosts } from 'investigate-hosts/actions/data-creators/host';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  selectedServiceData: selectedServiceWithStatus(state),
  selections: state.endpoint.machines.selectedHostList || [],
  warningMessages: warningMessages(state),
  isScanStartButtonDisabled: isScanStartButtonDisabled(state),
  agentIds: extractAgentIds(state),
  serviceId: serviceId(state),
  timeRange: timeRange(state)
});

const dispatchToActions = {
  deleteHosts,
  resetRiskScore
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
      if (command === 'START_SCAN') {
        startScanCommand(this.get('agentIds'));
      } else {
        stopScanCommand(this.get('agentIds'));
      }
      this.set('showCommandModal', false);
    },

    handleDeleteHosts() {
      const callBackOptions = {
        onSuccess: () => success('investigateHosts.hosts.deleteHosts.success'),
        onFailure: ({ meta: message }) => failure(message.message)
      };
      this.send('deleteHosts', callBackOptions);
      this.set('showConfirmationModal', false);
    },

    handleResetHostsRiskScore() {
      const limitedFiles = this.get('selections').slice(0, 100);
      const callBackOptions = {
        onSuccess: (response) => {
          const { data } = response;
          if (data === limitedFiles.length) {
            success('investigateFiles.riskScore.success');
          } else {
            warning('investigateFiles.riskScore.warning');
          }
        },
        onFailure: () => failure('investigateFiles.riskScore.error')
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
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Container);
