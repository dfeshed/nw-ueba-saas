import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { saveCertificateStatus, getSavedCertificateStatus } from 'investigate-files/actions/certificate-data-creators';

const stateToComputed = (state) => ({
  selections: state.certificate.list.selectedCertificateList,
  statusData: state.certificate.list.statusData
});

const dispatchToActions = {
  saveCertificateStatus,
  getSavedCertificateStatus
};

@classic
@classNames('certificates-status')
class CertificateStatus extends Component {
  @service
  flashMessages;

  showModal = false;

  init() {
    super.init(...arguments);
    this.radioButtons = this.radioButtons || [
      {
        label: 'investigateFiles.editFileStatus.fileStatusOptions.blacklist',
        value: 'Blacklisted'
      },
      {
        label: 'investigateFiles.editFileStatus.fileStatusOptions.whitelist',
        value: 'Whitelisted'
      },
      {
        label: 'investigateFiles.editFileStatus.fileStatusOptions.neutral',
        value: 'Neutral'
      }
    ];
  }

  @computed('selections')
  get isDisabled() {
    return this.selections && !this.selections.length;
  }

  @action
  showStatusModel() {
    const selections = this.get('selections');
    if (selections && selections.length === 1) {
      this.send('getSavedCertificateStatus', selections.mapBy('thumbprint'));
    }
    this.set('showModal', true);
  }

  @action
  closeModal() {
    this.set('showModal', false);
  }
}

export default connect(stateToComputed, dispatchToActions)(CertificateStatus);
