import $ from 'jquery';
import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';
import csrfToken from 'component-lib/mixins/csrf-token';
import Route from '@ember/routing/route';
import * as ACTION_TYPES from 'sa/actions/types';
import { get, computed } from '@ember/object';
import { Promise } from 'rsvp';
import { inject as service } from '@ember/service';
import fetch from 'component-lib/services/fetch';

const {
  testing
} = Ember;

export default Route.extend(ApplicationRouteMixin, csrfToken, {
  redux: service(),
  fatalErrors: service(),
  session: service(),
  userActivity: service(),
  userIdle: service(),

  persistStateOnLogout: true,

  title() {
    return this.get('i18n').t('appTitle');
  },

  routeAfterAuthentication: computed(function() {
    return 'protected';
  }),

  routeIfAlreadyAuthenticated: computed(function() {
    return 'protected';
  }),

  beforeModel(transition) {
    if (
      !this.get('session.isAuthenticated') &&
      transition.targetName !== 'login' &&
      transition.targetName !== 'protected' &&
      transition.targetName !== 'protected.index'
    ) {
      localStorage.setItem('rsa-post-auth-redirect', window.location.href);
    }
    this._setupUserTimeout();
  },

  getLocales() {
    const redux = get(this, 'redux');
    return fetch('/locales/').then((fetched) => fetched.json()).then((locales) => {
      redux.dispatch({ type: ACTION_TYPES.ADD_PREFERENCES_LOCALES, locales });
    }).catch(() => {
      // eslint-disable-next-line no-console
      console.log('fetching dynamic locales failed');
    });
  },

  model() {
    return this.getLocales();
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
      this._logout();
    }
  },

  /**
   * Logs the user out and invalidates the session
   * @returns {RSVP.Promise}
   * @private
   */
  _logout() {
    return new Promise((resolve) => {
      const csrfKey = this.get('csrfLocalstorageKey');
      $.ajax({
        type: 'POST',
        url: '/oauth/logout',
        timeout: 15000,
        headers: {
          'X-CSRF-TOKEN': localStorage.getItem(csrfKey)
        },
        data: {
          access_token: this.get('session.persistedAccessToken')
        }
      }).always(() => {
        localStorage.removeItem(csrfKey);
        this.get('session').invalidate();
        resolve();
      });
    });
  },

  /**
   * Sets up the activity and inactivity timeout events
   * @private
   */
  _setupUserTimeout() {
    if (!testing) {
      // After configured idle timeout period, logout
      this.get('userIdle').on('idleChanged', (isIdle) => {
        if (isIdle && this.get('session.isAuthenticated')) {
          this._logout();
        }
      });
    }
  },

  sessionInvalidated() {
    const isInIframe = this._isInIframe();

    if (this.get('persistStateOnLogout')) {
      if (isInIframe) { // IF ember app inside iframe then set the parent location
        localStorage.setItem('rsa-post-auth-redirect', window.parent.location.href);
      } else {
        localStorage.setItem('rsa-post-auth-redirect', window.location.href);
      }
    }
    // Need set the parent location if page is inserted in iframe
    if (isInIframe) {
      window.parent.location.replace('/login');
    } else {
      window.location.replace('/login');
    }
  },

  _isInIframe() {
    return !!window.frameElement;
  },

  sessionAuthenticated() {
    this.transitionTo('protected');
  }

});
