import { connect } from 'ember-redux';
import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { lookupCoreDevice } from 'respond-shared/utils/event-analysis';
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
    return lookupCoreDevice(services, eventSource);
  },

  @computed('eventId', 'endpointId', 'hasPermissions')
  show(eventId, endpointId, hasPermissions) {
    return eventId && endpointId && hasPermissions;
  }
});

export default connect(stateToComputed)(ReconLink);
