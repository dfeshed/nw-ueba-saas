import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';
import csrfToken from 'component-lib/mixins/csrf-token';
import Route from '@ember/routing/route';
import { computed } from '@ember/object';
import { inject as service } from '@ember/service';
import fetch from 'component-lib/utils/fetch';
import { windowProxy } from 'component-lib/utils/window-proxy';

const {
  testing
} = Ember;

export const DEFAULT_THEME = 'dark';
export const DEFAULT_LOCALE = { id: 'en_US', key: 'en-us', label: 'english' };
export const DEFAULT_LOCALES = [DEFAULT_LOCALE];

export default Route.extend({
  
  model() {
    debugger;
    this.transitionTo('investigate-users');
  },


  actions: {
    clearFatalErrorQueue() {
      this.get('fatalErrors').clearQueue();
    },
    error() {
      this.transitionTo('internal-error');
    },
    logout() {
      this.set('persistStateOnLogout', false);
      this._logout('User Triggered');
    }
  }

});
