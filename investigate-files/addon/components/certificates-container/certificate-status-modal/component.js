import classic from 'ember-classic-decorator';
import { classNames } from '@ember-decorators/component';
import { action, computed } from '@ember/object';
import { inject as service } from '@ember/service';
import Component from '@ember/component';
import { connect } from 'ember-redux';
import { saveCertificateStatus, getSavedCertificateStatus } from 'investigate-files/actions/certificate-data-creators';
import { isEmpty } from '@ember/utils';
import { success, failure } from 'investigate-shared/utils/flash-messages';

const stateToComputed = (state) => ({
  selections: state.certificate.list.selectedCertificateList,
  statusData: state.certificate.list.certificateStatusData
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
  isModelChange = false;

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

  @computed('data.comment', 'data.certificateStatus', 'statusData')
  get isSaveButtonDisabled() {
    return isEmpty(this.data?.comment) || isEmpty(this.data?.certificateStatus) || (this.data?.comment === this.statusData?.comment && this.data?.certificateStatus === this.statusData.certificateStatus);
  }

  @computed('data.comment')
  get isCharectarLimitReached() {
    return this.data?.comment && this.data?.comment.length >= 900;
  }

  @computed('statusData', 'selections')
  get data() {
    const statusDataObject = {
      certificateStatus: null,
      category: null,
      comment: '',
      remediationAction: null
    };
    if (this.statusData && this.selections.length === 1) {
      return { ...statusDataObject, ...this.statusData };
    }
    return statusDataObject;
  }

  @action
  closeModal() {
    this.closeCertificateModal();
  }

  @action
  saveStatus() {
    const callbackOptions = {
      onSuccess: () => success('configure.endpoint.certificates.status.success'),
      onFailure: () => failure('configure.endpoint.certificates.status.error')
    };
    this.send('saveCertificateStatus', this.get('selections').mapBy('thumbprint'), this.get('data'), callbackOptions);
    this.closeCertificateModal();
  }
}

export default connect(stateToComputed, dispatchToActions)(CertificateStatus);
