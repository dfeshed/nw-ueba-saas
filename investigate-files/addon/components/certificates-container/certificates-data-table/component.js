import Component from '@ember/component';
import { certificatesLoading, columns, nextLoadCount } from 'investigate-files/reducers/certificates/selectors';
import { connect } from 'ember-redux';
import { inject as service } from '@ember/service';
import {
  getPageOfCertificates,
  toggleCertificateSelection,
  saveCertificateStatus,
  getSavedCertificateStatus
} from 'investigate-files/actions/certificate-data-creators';
import { serviceId, timeRange } from 'investigate-shared/selectors/investigate/selectors';

const stateToComputed = (state) => ({
  certificatesItems: state.certificate.list.certificatesList,
  loadMoreStatus: state.certificate.list.loadMoreStatus,
  selections: state.certificate.list.selectedCertificateList || [],
  areCertificatesLoading: certificatesLoading(state),
  isCertificateView: state.certificate.list.isCertificateView,
  certificatesColumns: columns(state),
  serviceId: serviceId(state),
  timeRange: timeRange(state),
  nextLoadCount: nextLoadCount(state)
});

const dispatchToActions = {
  getPageOfCertificates,
  toggleCertificateSelection,
  saveCertificateStatus,
  getSavedCertificateStatus
};

const Certificates = Component.extend({
  tagName: '',

  classNames: ['certificates-data-table'],

  isAllSelected: true,

  showModal: false,

  timezone: service(),

  isAlreadySelected(selections, item) {
    let selected = false;
    if (selections && selections.length) {
      selected = selections.findBy('thumbprint', item.thumbprint);
    }
    return selected;
  },
  actions: {
    toggleSelectedRow(item, index, e, table) {
      table.set('selectedIndex', index);
      this.send('toggleCertificateSelection', item);
      e.preventDefault();
    },
    showStatusWindow() {
      const selections = this.get('selections');
      if (selections && selections.length === 1) {
        this.send('getSavedCertificateStatus', selections.mapBy('thumbprint'));
      }
      this.set('showModal', true);
    },
    closeModal() {
      this.set('showModal', false);
    },
    beforeContextMenuShow({ contextSelection: item }) {

      if (!this.isAlreadySelected(this.get('selections'), item)) {
        this.send('toggleCertificateSelection', item);
      }
      const selections = this.get('selections');
      if (selections && selections.length === 1) {
        this.send('getSavedCertificateStatus', selections);
      }
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Certificates);
