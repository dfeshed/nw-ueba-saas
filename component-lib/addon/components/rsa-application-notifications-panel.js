import Ember from 'ember';
import layout from '../templates/components/rsa-application-notifications-panel';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({
  layout,

  eventBus: service('event-bus'),

  classNames: ['rsa-application-notifications-panel'],

  classNameBindings: ['isExpanded'],

  isExpanded: false,

  init() {
    this._super(arguments);

    this.get('eventBus').on('rsa-application-notifications-panel-will-toggle', () => {
      this.toggleProperty('isExpanded');
    });
  }

});
