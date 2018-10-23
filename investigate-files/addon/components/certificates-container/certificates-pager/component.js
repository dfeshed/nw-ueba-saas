import Component from '@ember/component';
import { connect } from 'ember-redux';
import { certificatesCount, certificatesCountForDisplay } from 'investigate-files/reducers/certificates/selectors';

const stateToComputed = (state) => ({
  certificatesTotal: certificatesCountForDisplay(state), // Total number of certificates in search result
  certificatesIndex: certificatesCount(state)
});

const Pager = Component.extend({
  tagName: 'section',
  classNames: ['certificates-pager']
});
export default connect(stateToComputed)(Pager);
