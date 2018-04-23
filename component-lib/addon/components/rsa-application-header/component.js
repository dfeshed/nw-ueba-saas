import Component from '@ember/component';
import { inject as service } from '@ember/service';
import layout from './template';
import ContextualHelp from '../../mixins/contextual-help';
import getOwner from 'ember-owner/get';
import { jwt_decode as jwtDecode } from 'ember-cli-jwt-decode';

export default Component.extend(ContextualHelp, {

  eventBus: service(),

  ajax: service(),

  layoutService: service('layout'),

  layout,

  classNames: ['rsa-application-header'],

  displayPreferences: true,

  username: null,

  click(event) {
    this.get('eventBus').trigger('rsa-application-header-click', event.target);
  },

  didInsertElement() {
    this._super(...arguments);

    const config = getOwner(this).resolveRegistration('config:environment');
    const authConfig = config['ember-simple-auth'];

    if (authConfig) {
      const tokenKey = authConfig.accessTokenKey;
      const token = localStorage.getItem(tokenKey);

      if (token) {
        const decodedToken = jwtDecode(token);
        this.set('username', decodedToken.user_name);
      }
    }
  },

  actions: {
    toggleUserPreferences() {
      this.get('layoutService').toggleUserPreferences();
    }
  }

});
