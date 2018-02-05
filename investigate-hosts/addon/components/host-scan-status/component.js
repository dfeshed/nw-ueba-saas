import Component from 'ember-component';
import computed from 'ember-computed-decorators';
import injectService from 'ember-service/inject';

const statusMapping = {
  scanning: 'Scanning',
  scanPending: 'Starting scan',
  cancelPending: 'Stopping scan',
  idle: 'Idle'
};

export default Component.extend({

  eventBus: injectService(),

  classNames: ['rsa-agent-scan-status'],

  @computed('agent.agentStatus.scanStatus', 'agent.machine.agentVersion')
  status: (scanStatus, agentVersion) => {
    if (agentVersion && agentVersion.startsWith('4.4')) {
      return 'N/A';
    }
    return statusMapping[scanStatus] || 'N/A';
  },
  actions: {
    startScan(id) {
      this.sendAction('onButtonClick', id);
      this.get('eventBus').trigger('rsa-application-modal-open-initiate-scan');
    },

    stopScan(id) {
      this.sendAction('onButtonClick', id);
      this.get('eventBus').trigger('rsa-application-modal-open-cancel-scan');
    }
  }
});
