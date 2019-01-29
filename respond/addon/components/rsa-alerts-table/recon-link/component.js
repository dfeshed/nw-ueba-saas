import { connect } from 'ember-redux';
import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { lookupCoreDevice } from 'respond-shared/utils/event-analysis';
import { getServices } from 'respond/reducers/respond/recon/selectors';
import { EVENT_TYPES } from 'component-lib/constants/event-types';

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

  @computed('item.type', 'item.device_type')
  eventType(type, deviceType) {
    if (deviceType === 'nwendpoint') {
      return EVENT_TYPES.ENDPOINT;
    } else if (type === 'Network') {
      return EVENT_TYPES.NETWORK;
    } else {
      return EVENT_TYPES.LOG;
    }
  },

  @computed('eventId', 'endpointId', 'hasPermissions')
  show(eventId, endpointId, hasPermissions) {
    return eventId && endpointId && hasPermissions;
  }
});

export default connect(stateToComputed)(ReconLink);
