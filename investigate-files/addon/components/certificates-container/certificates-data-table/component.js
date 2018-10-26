import Component from '@ember/component';
import { certificatesLoading, columns } from 'investigate-files/reducers/certificates/selectors';
import { connect } from 'ember-redux';
import {
  getPageOfCertificates,
  toggleCertificateSelection
 } from 'investigate-files/actions/certificate-data-creators';


const stateToComputed = (state) => ({
  certificatesItems: state.certificate.list.certificatesList,
  loadMoreStatus: state.certificate.list.loadMoreStatus,
  selections: state.certificate.list.selectedCertificateList,
  areCertificatesLoading: certificatesLoading(state),
  isCertificateView: state.certificate.list.isCertificateView,
  certificatesColumns: columns(state)
});

const dispatchToActions = {
  getPageOfCertificates,
  toggleCertificateSelection
};

const Certificates = Component.extend({
  tagName: '',

  classNames: ['certificates-data-table'],

  isAllSelected: true,

  actions: {
    toggleSelectedRow(item, index, e, table) {
      table.set('selectedIndex', index);
      this.send('toggleCertificateSelection', item);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Certificates);
