import Component from '@ember/component';
import certificatesColumnConfig, { certificatesLoading } from 'configure/reducers/endpoint/certificates/selector';
import { connect } from 'ember-redux';
import { getPageOfCertificates } from 'configure/actions/creators/endpoint/certificates-creator';


const stateToComputed = (state) => ({
  certificatesItems: state.configure.endpoint.certificates.certificatesList,
  loadMoreStatus: state.configure.endpoint.certificates.loadMoreStatus,
  areCertificatesLoading: certificatesLoading(state)
});

const dispatchToActions = {
  getPageOfCertificates
};

const Certificates = Component.extend({
  tagName: '',

  classNames: ['certificates-data-table'],

  certificatesColumns: certificatesColumnConfig.certificatesColumns
});

export default connect(stateToComputed, dispatchToActions)(Certificates);
