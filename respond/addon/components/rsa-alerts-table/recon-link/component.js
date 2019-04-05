import _ from 'lodash';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import computed, { alias } from 'ember-computed-decorators';
import { inject as service } from '@ember/service';
import { lookupCoreDevice } from 'respond-shared/utils/event-analysis';
import { coreServiceNotUpdated } from 'component-lib/utils/core-services';
import { getServices } from 'respond/reducers/respond/recon/selectors';
import { EVENT_TYPES } from 'component-lib/constants/event-types';

const stateToComputed = (state) => ({
  services: getServices(state)
});

const ReconLink = Component.extend({
  appVersion: service(),
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

  @computed('services', 'endpointId', 'appVersion.minServiceVersion')
  mixedMode(services, eventSource, minVersion) {
    return _.filter(services, ({ id, version }) => coreServiceNotUpdated(version, minVersion) && id === eventSource).length > 0;
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

  @computed('eventId', 'endpointId', 'hasPermissions', 'mixedMode')
  show(eventId, endpointId, hasPermissions, mixedMode) {
    return eventId && endpointId && hasPermissions && !mixedMode;
  }
});

export default connect(stateToComputed)(ReconLink);
