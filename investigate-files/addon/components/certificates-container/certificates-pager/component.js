import Component from '@ember/component';
import { connect } from 'ember-redux';
import { certificatesCount, certificatesCountForDisplay } from 'investigate-files/reducers/certificates/selectors';
import computed from 'ember-computed-decorators';

const stateToComputed = (state) => ({
  certificatesTotal: certificatesCountForDisplay(state), // Total number of certificates in search result
  certificatesIndex: certificatesCount(state),
  selections: state.certificate.list.selectedCertificateList || []
});

const Pager = Component.extend({
  tagName: 'section',
  classNames: ['certificates-pager'],

  @computed('selections')
  count(selections) {
    return selections.length;
  }
});
export default connect(stateToComputed)(Pager);
