import Component from '@ember/component';
import { connect } from 'ember-redux';

import { setEndpointServer } from 'configure/actions/creators/endpoint/server-creator';

const stateToComputed = (state) => ({
  servers: state.configure.endpoint.server,
  serverId: state.configure.endpoint.query.serverId,
  isEndpointServerOnline: !state.configure.endpoint.server.isSummaryRetrieveError
});

const dispatchToActions = {
  setEndpointServer
};

const Certificate = Component.extend({
  tagName: 'vbox',

  classNames: ['certificates-container', 'main-zone']

});

export default connect(stateToComputed, dispatchToActions)(Certificate);

