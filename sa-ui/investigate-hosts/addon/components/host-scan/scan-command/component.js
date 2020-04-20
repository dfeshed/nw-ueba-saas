import classic from 'ember-classic-decorator';
import { tagName } from '@ember-decorators/component';
import { computed } from '@ember/object';
import Component from '@ember/component';
import { connect } from 'ember-redux';

import {
  isScanStartButtonDisabled,
  isScanStopButtonDisabled,
  warningMessages,
  scanCount,
  extractAgentIds,
  actionsDisableMessage } from 'investigate-hosts/reducers/hosts/selectors';

import { hostOverviewServerId } from 'investigate-hosts/reducers/details/overview/selectors';

const stateToComputed = (state) => ({
  warningMessages: warningMessages(state),
  isScanStartButtonDisabled: isScanStartButtonDisabled(state),
  isScanStopButtonDisabled: isScanStopButtonDisabled(state),
  agentIds: extractAgentIds(state),
  scanCount: scanCount(state),
  actionsDisableMessage: actionsDisableMessage(state),
  serverIdForScanCommand: hostOverviewServerId(state)
});

@classic
@tagName('')
class ScanCommand extends Component {
  command = null;
  isStartScanIconDisplayed = true;

  @computed('command', 'modalTitle', 'scanCount')
  get title() {
    if (this.modalTitle) {
      return this.modalTitle;
    }
    let key = 'investigateHosts.hosts.initiateScan.modal.title';

    if (this.command === 'STOP_SCAN') {
      key = 'investigateHosts.hosts.cancelScan.modal.title';
    }
    return this.get('i18n').t(key, { count: this.scanCount });
  }
}

export default connect(stateToComputed)(ScanCommand);
