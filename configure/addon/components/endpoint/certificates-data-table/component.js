import Component from '@ember/component';
import certificatesColumnConfig, { certificatesLoading, isAllSelected } from 'configure/reducers/endpoint/certificates/selector';
import { connect } from 'ember-redux';
import {
  getPageOfCertificates,
  toggleAllCertificateSelection,
  toggleCertificateSelection

 } from 'configure/actions/creators/endpoint/certificates-creator';


const stateToComputed = (state) => ({
  certificatesItems: state.configure.endpoint.certificates.list.certificatesList,
  loadMoreStatus: state.configure.endpoint.certificates.list.loadMoreStatus,
  areCertificatesLoading: certificatesLoading(state),
  selectedCertificateList: state.configure.endpoint.certificates.list.selectedCertificateList,
  isAllSelected: isAllSelected(state)
});

const dispatchToActions = {
  getPageOfCertificates,
  toggleAllCertificateSelection,
  toggleCertificateSelection
};

const Certificates = Component.extend({
  tagName: '',

  classNames: ['certificates-data-table'],

  certificatesColumns: certificatesColumnConfig.certificatesColumns,

  isAllSelected: true
});

export default connect(stateToComputed, dispatchToActions)(Certificates);
