import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { stopScan } from 'investigate-hosts/actions/data-creators/host';

@classic
@classNames('stop-scan-button')
export default class StopScanButton extends Component {
  @service
  flashMessage;

  buttonText = '';
  isDisabled = false;
  isIconOnly = false;
  _showStopScanModal = false;
  modalTitle = '';
  warningMessage = null;
  agentIds = null;
  serverId = null;

  @action
  handleStopScan() {
    const callBackOptions = {
      onSuccess: () => this.get('flashMessage').showFlashMessage('investigateHosts.hosts.cancelScan.success'),
      onFailure: (message) => this.get('flashMessage').showErrorMessage(message)
    };
    stopScan(this.get('agentIds'), callBackOptions, this.get('serverId'));
  }

  @action
  toggleStopScanModal() {
    this.set('_showStopScanModal', true);
  }

  @action
  onModalClose() {
    this.set('_showStopScanModal', false);
  }
}
