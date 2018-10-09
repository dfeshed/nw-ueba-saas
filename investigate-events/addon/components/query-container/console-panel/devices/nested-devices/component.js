import Component from '@ember/component';
import computed from 'ember-computed-decorators';
import { run } from '@ember/runloop';

export default Component.extend({
  classNames: ['nested-devices'],
  isExpanded: false,
  device: null,
  height: 0,
  tagName: 'li',

  updateHeight() {
    run.schedule('afterRender', () => {
      const thisHeight = this.$().height();
      const lastChildHeight = this.$('li:last-of-type').height();

      this.set('height', (thisHeight - lastChildHeight) + 5);

      if (this.devicesExpanded) {
        this.devicesExpanded();
      }
    });
  },

  @computed('device.serviceId', 'warnings', 'warnings.length')
  warning: (serviceId, warnings) => {
    if (!serviceId || !warnings) {
      return;
    } else {
      return warnings.findBy('serviceId', serviceId);
    }
  },

  @computed('device.serviceId', 'warningsPath', 'warningsPath.length')
  inWarningPath: (serviceId, warningsPath) => {
    if (!serviceId || !warningsPath) {
      return;
    } else {
      return warningsPath.includes(serviceId);
    }
  },

  @computed('device.serviceId', 'offlineServicesPath', 'offlineServicesPath.length')
  inOfflinePath: (serviceId, offlineServicesPath) => {
    if (!serviceId || !offlineServicesPath) {
      return;
    } else {
      return offlineServicesPath.includes(serviceId);
    }
  },

  @computed('device.serviceId', 'offlineServices', 'offlineServices.length')
  hasOffline: (serviceId, offlineServices) => {
    if (!serviceId || !offlineServices) {
      return;
    } else {
      return offlineServices.includes(serviceId);
    }
  },

  @computed('device.serviceId', 'slowestInQuery')
  isSlowest: (serviceId, slowestInQuery) => {
    if (!slowestInQuery || !serviceId) {
      return;
    } else {
      return slowestInQuery === serviceId;
    }
  },

  actions: {
    expandDevices() {
      this.toggleProperty('isExpanded');
      this.updateHeight();
    }
  }
});
