import Ember from 'ember';
import layout from '../templates/components/rsa-application-header';
import ContextualHelp from '../mixins/contextual-help';

const {
  Component,
  inject: {
    service
  },
  RSVP,
  $
} = Ember;

export default Component.extend(ContextualHelp, {

  eventBus: service(),

  ajax: service(),

  layoutService: service('layout'),

  usernameFormat: service('username-format'),

  layout,

  classNames: ['rsa-application-header'],

  actions: {
    logout() {
      return new RSVP.Promise((resolve) => {
        $.ajax({
          type: 'POST',
          url: '/oauth/logout',
          timeout: 3000,
          data: {
            access_token: this.get('session').get('data.authenticated.access_token')
          }
        })
          .done(() => {
            this.get('session').invalidate();
            resolve();
          });
      });
    },

    toggleNotifications() {
      this.get('layoutService').toggleNotifications();
    }
  }

});
