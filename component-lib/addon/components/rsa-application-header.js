import Ember from 'ember';
import layout from '../templates/components/rsa-application-header';
import ContextualHelp from '../mixins/contextual-help';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend(ContextualHelp, {

  eventBus: service(),

  ajax: service(),

  layoutService: service('layout'),

  usernameFormat: service(),

  timezone: service(),

  layout,

  classNames: ['rsa-application-header'],

  actions: {
    toggleUserPreferences() {
      this.get('layoutService').toggleUserPreferences();
    }
  }

});
