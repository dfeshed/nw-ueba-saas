import Component from '@ember/component';
import { connect } from 'ember-redux';
import { saveCertificateStatus, getSavedCertificateStatus } from 'investigate-files/actions/certificate-data-creators';
import computed from 'ember-computed-decorators';
import { inject as service } from '@ember/service';

const stateToComputed = (state) => ({
  selections: state.certificate.list.selectedCertificateList,
  statusData: state.certificate.list.statusData
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

  actions: {
    showStatusModel() {
      const selections = this.get('selections');
      if (selections && selections.length === 1) {
        this.send('getSavedCertificateStatus', selections.mapBy('thumbprint'));
      }
      this.set('showModal', true);
    },
    closeModal() {
      this.set('showModal', false);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(CertificateStatus);
