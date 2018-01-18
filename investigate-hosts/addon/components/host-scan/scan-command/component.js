import Component from 'ember-component';
import { connect } from 'ember-redux';
import computed from 'ember-computed-decorators';

import {
  isScanStartButtonDisabled,
  warningMessages,
  extractAgentIds } from 'investigate-hosts/reducers/hosts/selectors';

const stateToComputed = (state) => ({
  warningMessages: warningMessages(state),
  isScanStartButtonDisabled: isScanStartButtonDisabled(state),
  agentIds: extractAgentIds(state)
});
const ScanCommand = Component.extend({
  tagName: '',

  command: null,

  @computed('modalTitle', 'agentIds')
  title(modalTitle, agentIds) {
    if (modalTitle) {
      return modalTitle;
    }
    return this.get('i18n').t('investigateHosts.hosts.initiateScan.modal.title', { count: agentIds.length });
  }
});

export default connect(stateToComputed)(ScanCommand);
