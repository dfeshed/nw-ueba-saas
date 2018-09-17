import Component from '@ember/component';
import { connect } from 'ember-redux';
import { saveCertificateStatus, getSavedCertificateStatus } from 'configure/actions/creators/endpoint/certificates-creator';
import computed from 'ember-computed-decorators';
import { isEmpty } from '@ember/utils';
import { inject as service } from '@ember/service';
import { success, failure } from 'configure/utils/flash-messages';

const stateToComputed = (state) => ({
  selections: state.configure.endpoint.certificates.selectedCertificateList,
  statusData: state.configure.endpoint.certificates.statusData
});

const dispatchToActions = {
  saveCertificateStatus,
  getSavedCertificateStatus
};


const CertificateStatus = Component.extend({

  flashMessages: service(),

  classNames: ['certificates-status'],

  showModal: false,

  radioButtons: [
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
  ],

  @computed('selections')
  isDisabled(selections) {
    return selections && !selections.length;
  },

  @computed('data.comment')
  isSaveButtonDisabled(comment) {
    return isEmpty(comment);
  },

  @computed('statusData', 'selections')
  data(statusData, selections) {
    if (statusData && selections.length === 1) {
      return statusData;
    }
    return {
      certificateStatus: null,
      category: null,
      comment: '',
      remediationAction: null
    };
  },

  actions: {
    showStatusModal() {
      const selections = this.get('selections');
      if (selections && selections.length === 1) {
        this.send('getSavedCertificateStatus', selections);
      }
      this.set('showModal', true);
    },
    closeModal() {
      this.set('showModal', false);
    },

    saveStatus() {
      const callbackOptions = {
        onSuccess: () => success('configure.endpoint.certificates.status.success'),
        onFailure: () => failure('configure.endpoint.certificates.status.failure')
      };
      this.set('showModal', false);
      this.send('saveCertificateStatus', this.get('selections').mapBy('thumbprint'), this.get('data'), callbackOptions);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CertificateStatus);
