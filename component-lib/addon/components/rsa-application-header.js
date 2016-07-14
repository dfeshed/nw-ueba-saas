import Ember from 'ember';
import layout from '../templates/components/rsa-application-header';

const {
  Component,
  inject: {
    service
  }
} = Ember;

export default Component.extend({

  eventBus: service('event-bus'),

  usernameFormat: service('username-format'),

  layout,

  classNames: ['rsa-application-header'],

  actions: {
    logout() {
      this.get('session').invalidate();
    }
  }

});