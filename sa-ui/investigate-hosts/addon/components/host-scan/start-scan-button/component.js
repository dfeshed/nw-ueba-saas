import classic from 'ember-classic-decorator';
import { action } from '@ember/object';
import { classNames } from '@ember-decorators/component';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { startScan } from 'investigate-hosts/actions/data-creators/host';
import { success, failure } from 'investigate-shared/utils/flash-messages';

@classic
@classNames('host-start-scan-button')
export default class StartScanButton extends Component {
  @service
  flashMessage;

  isDisabled = false;
  isIconOnly = false;
  _showStartScanModal = false;
  buttonText = null;
  modalTitle = '';
  warningMessages = null;
  agentIds = null;
  title = null;
  serverId = null;
  isStartScanIconDisplayed = true;

  @action
  handleInitiateScan() {
    const callBackOptions = {
      onSuccess: () => {
        success('investigateHosts.hosts.initiateScan.success');
      },
      onFailure: (response) => {
        if (response.includes('Scan is already in progress or request is pending')) {
          failure(response, null, false);
        } else {
          failure('investigateHosts.hosts.initiateScan.failure');
        }
      }
    };
    startScan(this.get('agentIds'), callBackOptions, this.get('serverId'));
  }

  @action
  onModalClose() {
    this.set('_showStartScanModal', false);
  }

  @action
  toggleScanStartModal() {
    this.set('_showStartScanModal', true);
  }
}
