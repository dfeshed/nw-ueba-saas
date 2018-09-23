import { connect } from 'ember-redux';
import Component from '@ember/component';

const stateToComputed = (state) => ({
  schemaLoading: state.endpoint.schema.schemaLoading,
  isEndpointServerOnline: !state.endpointServer.isSummaryRetrieveError
});

const Container = Component.extend({

  tagName: '',

  classNames: 'host-list show-more-filter main-zone'

});

export default connect(stateToComputed)(Container);
