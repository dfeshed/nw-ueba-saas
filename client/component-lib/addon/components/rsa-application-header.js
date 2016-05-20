import Ember from 'ember';
import layout from '../templates/components/rsa-application-header';

export default Ember.Component.extend({

  eventBus: Ember.inject.service('event-bus'),

  usernameFormat: Ember.inject.service('username-format'),

  layout,

  classNames: ['rsa-application-header'],

  actions: {
    logout() {
      this.get('session').invalidate();
    }
  }

});