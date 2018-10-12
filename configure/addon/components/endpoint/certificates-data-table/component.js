import Component from '@ember/component';
import certificatesColumnConfig, { certificatesLoading } from 'configure/reducers/endpoint/certificates/selector';
import { connect } from 'ember-redux';
import {
  getPageOfCertificates,
  toggleCertificateSelection
 } from 'configure/actions/creators/endpoint/certificates-creator';


const stateToComputed = (state) => ({
  certificatesItems: state.configure.endpoint.certificates.list.certificatesList,
  loadMoreStatus: state.configure.endpoint.certificates.list.loadMoreStatus,
  areCertificatesLoading: certificatesLoading(state)
});

const dispatchToActions = {
  getPageOfCertificates,
  toggleCertificateSelection
};

const Certificates = Component.extend({
  tagName: '',

  classNames: ['certificates-data-table'],

  certificatesColumns: certificatesColumnConfig.certificatesColumns,

  isAllSelected: true,

  actions: {
    toggleSelectedRow(item, index, e, table) {
      table.set('selectedIndex', index);
      this.send('toggleCertificateSelection', item);
    }
  }
});

export default connect(stateToComputed, dispatchToActions)(Certificates);
