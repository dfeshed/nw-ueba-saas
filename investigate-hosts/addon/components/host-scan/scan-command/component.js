import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import {
  isScanStartButtonDisabled,
  warningMessages,
  scanCount,
  extractAgentIds } from 'investigate-hosts/reducers/hosts/selectors';

const stateToComputed = (state) => ({
  warningMessages: warningMessages(state),
  isScanStartButtonDisabled: isScanStartButtonDisabled(state),
  agentIds: extractAgentIds(state),
  scanCount: scanCount(state)
});
const ScanCommand = Component.extend({
  tagName: '',

  command: null,

  @computed('command', 'modalTitle', 'scanCount')
  title(command, modalTitle, scanCount) {
    if (modalTitle) {
      return modalTitle;
    }
    let key = 'investigateHosts.hosts.initiateScan.modal.title';

    if (command === 'STOP_SCAN') {
      key = 'investigateHosts.hosts.cancelScan.modal.title';
    }
    return this.get('i18n').t(key, { count: scanCount });
  }
});

export default connect(stateToComputed)(ScanCommand);
