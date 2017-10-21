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

  @computed('agent.agentStatus.scanStatus')
  status: (scanStatus) => statusMapping[scanStatus] || '',

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
