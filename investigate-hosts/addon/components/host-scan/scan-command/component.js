import Component from 'ember-component';
import { connect } from 'ember-redux';
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

  command: null

});

export default connect(stateToComputed)(ScanCommand);
