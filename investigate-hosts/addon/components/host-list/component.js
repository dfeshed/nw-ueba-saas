import { connect } from 'ember-redux';
import Component from '@ember/component';

import {
  resetFilters
} from 'investigate-hosts/actions/data-creators/filter';

import { setEndpointServer } from 'investigate-hosts/actions/data-creators/endpoint-server';

const stateToComputed = (state) => ({
  schemaLoading: state.endpoint.schema.schemaLoading,
  servers: state.endpointServer,
  serverId: state.endpointQuery.serverId,
  isEndpointServerOnline: !state.endpointServer.isSummaryRetrieveError
});

const dispatchToActions = {
  resetFilters,
  setEndpointServer
};
const Container = Component.extend({

  tagName: '',

  classNames: 'host-list show-more-filter main-zone'

});

export default connect(stateToComputed, dispatchToActions)(Container);
