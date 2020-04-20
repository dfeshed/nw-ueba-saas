import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import { computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';

const statusMapping = {
  scanning: 'Scanning',
  scanPending: 'Pending',
  cancelPending: 'Cancelling',
  idle: 'Idle'
};

@classic
@classNames('rsa-agent-scan-status')
export default class HostScanStatus extends Component {
  @service
  eventBus;

  @computed('agent.agentStatus.scanStatus', 'agent.machineIdentity.agentVersion')
  get status() {
    if (this.agent?.machineIdentity?.agentVersion && this.agent?.machineIdentity?.agentVersion.startsWith('4.4')) {
      return 'N/A';
    }
    return statusMapping[this.agent?.agentStatus?.scanStatus] || 'N/A';
  }
}
