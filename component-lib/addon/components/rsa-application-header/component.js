import Component from 'ember-component';
import service from 'ember-service/inject';
import layout from './template';
import ContextualHelp from '../../mixins/contextual-help';

export default Component.extend(ContextualHelp, {

  eventBus: service(),

  ajax: service(),

  layoutService: service('layout'),

  timezone: service(),

  layout,

  classNames: ['rsa-application-header'],

  displayPreferences: true,

  click(event) {
    this.get('eventBus').trigger('rsa-application-header-click', event.target);
  },

  actions: {
    toggleUserPreferences() {
      this.get('layoutService').toggleUserPreferences();
    }
  }

});
