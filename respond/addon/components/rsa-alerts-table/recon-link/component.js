import { computed } from '@ember/object';
import _ from 'lodash';
import { connect } from 'ember-redux';
import Component from '@ember/component';
import { alias } from '@ember/object/computed';
import { inject as service } from '@ember/service';
import { lookupCoreDevice } from 'respond-shared/utils/event-analysis';
import { coreServiceNotUpdated } from 'component-lib/utils/core-services';
import { getServices } from 'respond/reducers/respond/recon/selectors';
import { EVENT_TYPES, EVENT_TYPES_LABELS } from 'component-lib/constants/event-types';

const stateToComputed = (state) => ({
  services: getServices(state)
});

const ReconLink = Component.extend({
  appVersion: service(),
  accessControl: service(),
  testId: 'respondReconLink',
  attributeBindings: ['testId:test-id'],

  hasPermissions: computed('accessControl.hasReconAccess', function() {
    return this.accessControl?.hasReconAccess;
  }),

  eventId: alias('item.event_source_id'),

  endpointId: computed('services', 'item.event_source', function() {
    // eslint-disable-next-line camelcase
    return lookupCoreDevice(this.services, this.item?.event_source);
  }),

  mixedMode: computed('services', 'endpointId', 'appVersion.minServiceVersion', function() {
    return _.filter(this.services, ({ id, version }) => coreServiceNotUpdated(version, this.appVersion?.minServiceVersion) && id === this.endpointId).length > 0;
  }),

  eventType: computed('item.type', 'item.device_type', function() {
    // eslint-disable-next-line camelcase
    if (this.item?.device_type === 'nwendpoint') {
      return EVENT_TYPES.ENDPOINT;
    } else if (this.item?.type === 'Network') {
      return EVENT_TYPES.NETWORK;
    } else {
      return EVENT_TYPES.LOG;
    }
  }),

  /*
   * Displays the event type label as the recon link.
   */
  eventTypeLabel: computed('eventType', function() {
    return EVENT_TYPES_LABELS[this.eventType];
  }),

  show: computed('eventId', 'endpointId', 'hasPermissions', 'mixedMode', function() {
    return this.eventId && this.endpointId && this.hasPermissions && !this.mixedMode;
  })
});

export default connect(stateToComputed)(ReconLink);
