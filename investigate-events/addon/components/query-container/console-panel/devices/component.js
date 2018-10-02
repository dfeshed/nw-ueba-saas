import Component from '@ember/component';
import { connect } from 'ember-redux';
import {
  hasWarning,
  hasError,
  offlineServices,
  decoratedDevices,
  warningsWithServiceName,
  slowestInQuery,
  offlineServicesPath,
  warningsPath
} from 'investigate-events/reducers/investigate/query-stats/selectors';
import { run } from '@ember/runloop';

const stateToComputed = (state) => ({
  warningsPath: warningsPath(state),
  offlineServicesPath: offlineServicesPath(state),
  warnings: warningsWithServiceName(state),
  slowestInQuery: slowestInQuery(state),
  offlineServices: offlineServices(state),
  hasError: hasError(state),
  hasWarning: hasWarning(state),
  devices: decoratedDevices(state),
  eventCount: state.investigate.eventCount.data
});

const DevicesStatus = Component.extend({
  classNames: ['devices-status'],
  classNameBindings: ['hasError', 'hasWarning'],
  isExpanded: false,
  height: 0,

  updateHeight() {
    run.schedule('afterRender', () => {
      const thisHeight = this.$('.device-hierarchy:first-of-type > li > .device-hierarchy').first().height();
      const lastChildHeight = this.$('.device-hierarchy:first-of-type > li > .device-hierarchy > li:last-of-type').first().height();

      this.set('height', (thisHeight - lastChildHeight + 16));
    });
  },

  actions: {
    expandDevices() {
      this.toggleProperty('isExpanded');
      this.updateHeight();
    },

    devicesExpanded() {
      this.updateHeight();
    }

  }
});

export default connect(stateToComputed)(DevicesStatus);
