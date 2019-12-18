import Component from '@ember/component';
import { connect } from 'ember-redux';
import { saveCertificateStatus, getSavedCertificateStatus } from 'investigate-files/actions/certificate-data-creators';
import computed from 'ember-computed-decorators';
import { isEmpty } from '@ember/utils';
import { inject as service } from '@ember/service';
import { success, failure } from 'investigate-shared/utils/flash-messages';

const stateToComputed = (state) => ({
  selections: state.certificate.list.selectedCertificateList,
  statusData: state.certificate.list.certificateStatusData
});

const dispatchToActions = {
  saveCertificateStatus,
  getSavedCertificateStatus
};

const CertificateStatus = Component.extend({

  flashMessages: service(),

  classNames: ['certificates-status'],

  showModal: false,

  isModelChange: false,
  init() {
    this._super(...arguments);
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
  },

  @computed('data.comment', 'data.certificateStatus', 'statusData')
  isSaveButtonDisabled(comment, fileStatus, currentStatusData) {
    return isEmpty(comment) || isEmpty(fileStatus) || (comment === currentStatusData.comment && fileStatus === currentStatusData.certificateStatus);
  },
  @computed('data.comment')
  isCharectarLimitReached(comment) {
    return comment && comment.length >= 900;
  },

  @computed('statusData', 'selections')
  data(statusData, selections) {
    const statusDataObject = {
      certificateStatus: null,
      category: null,
      comment: '',
      remediationAction: null
    };
    if (statusData && selections.length === 1) {
      return { ...statusDataObject, ...statusData };
    }
    return statusDataObject;
  },

  actions: {
    closeModal() {
      this.closeCertificateModal();
    },
    saveStatus() {
      const callbackOptions = {
        onSuccess: () => success('configure.endpoint.certificates.status.success'),
        onFailure: () => failure('configure.endpoint.certificates.status.error')
      };
      this.send('saveCertificateStatus', this.get('selections').mapBy('thumbprint'), this.get('data'), callbackOptions);
      this.closeCertificateModal();
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CertificateStatus);
