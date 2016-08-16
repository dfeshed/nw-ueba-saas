import Ember from 'ember';
import layout from '../templates/components/rsa-application-incident-queue-panel';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  layout,

  eventBus: service('event-bus'),

  classNames: ['rsa-application-incident-queue-panel'],

  classNameBindings: ['isExpanded'],

  isExpanded: false,

  init() {
    this._super(arguments);

    this.get('eventBus').on('rsa-application-incident-queue-panel-will-toggle', () => {
      this.toggleProperty('isExpanded');
    });
  }

});
