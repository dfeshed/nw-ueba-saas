import _ from 'lodash';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { getServices } from 'respond/reducers/respond/recon/selectors';

const stateToComputed = (state) => ({
  services: getServices(state)
});

const ReconLink = Component.extend({
  accessControl: service(),
  testId: 'respondReconLink',
  attributeBindings: ['testId:test-id'],

  @computed('accessControl.hasReconAccess')
  hasPermissions(hasReconAccess) {
    return hasReconAccess;
  },

  @alias('item.event_source_id') eventId: null,
  @computed('services', 'item.event_source')
  endpointId(services, eventSource) {
    if (!eventSource) {
      return null;
    }
    const pattern = new RegExp('(.*):(.*)');
    const patternMatch = pattern.exec(eventSource);
    if (!patternMatch) {
      return null;
    }
    return this.lookupCoreDevice(services, patternMatch);
  },

  lookupCoreDevice(services, patternMatch) {
    const [ , eventHost, eventPort ] = patternMatch;
    const lookup = _.filter(services, (service) => {
      const { host, port } = service;
      const hostFound = host && host == eventHost;
      const portFound = port && port == eventPort;
      return hostFound && portFound;
    });
    return lookup && lookup.length === 1 && lookup[0].id;
  },

  @computed('eventId', 'endpointId', 'hasPermissions')
  show(eventId, endpointId, hasPermissions) {
    return eventId && endpointId && hasPermissions;
  }

});

export default connect(stateToComputed)(ReconLink);
