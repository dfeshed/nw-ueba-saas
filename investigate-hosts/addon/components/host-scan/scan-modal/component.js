import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';

@classic
@classNames('scan-modal')
export default class ScanModal extends Component {
  @service
  eventBus;

  modalTitle = '';
  warningMessages = null;
  eventId = 'host-scan';
  onModalClose = null;
  primaryAction = null;

  @computed('modalStyle')
  get style() {
    return `standard scan-modal ${this.modalStyle}`;
  }

  @computed('command')
  get decorator() {
    const i18n = this.get('i18n');
    if (this.command === 'START_SCAN') {
      return {
        bodyMessage: i18n.t('investigateHosts.hosts.initiateScan.modal.quickScan.description'),
        primaryButtonText: i18n.t('investigateHosts.hosts.button.initiateScan')
      };
    }
    return {
      bodyMessage: i18n.t('investigateHosts.hosts.cancelScan.modal.description'),
      primaryButtonText: i18n.t('investigateHosts.hosts.button.cancelScan')
    };
  }

  @action
  handleClick() {
    this.send('closeModal');
    this.primaryAction(this.get('command'));
  }

  @action
  closeModal() {
    this.onModalClose();
  }
}
