import $ from 'jquery';
import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';
import csrfToken from 'component-lib/mixins/csrf-token';
import Route from '@ember/routing/route';
import * as ACTION_TYPES from 'sa/actions/types';
import { get, computed } from '@ember/object';
import { Promise } from 'rsvp';
import { inject as service } from '@ember/service';
import fetch from 'component-lib/utils/fetch';
import { windowProxy } from 'component-lib/utils/window-proxy';

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

  ssoEnabled: false,

  queryParams: {
    next: { refreshModel: false }
  },

  next: null,

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
      transition.targetName &&
      transition.targetName !== 'login' &&
      transition.targetName !== 'protected' &&
      transition.targetName !== 'protected.index' &&
      !transition.targetName.startsWith('sso')
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

  model({ next }) {
    if (next) {
      localStorage.setItem('rsa-post-auth-redirect', next);
    }
    this.checkSso();
    return this.getLocales();
  },

  checkSso() {
    return fetch('/saml/sso/is-enabled').then((response) => response.json()).then((result) => {
      this.set('ssoEnabled', result);
    }).catch(() => {
      this.set('ssoEnabled', false);
    });
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
  },

  /**
   * Logs the user out and invalidates the session
   * @returns {RSVP.Promise}
   * @private
   */
  _logout(reason) {

    if (this.get('ssoEnabled')) {
      windowProxy.openInCurrentTab('/saml/logout');
      return;
    }

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
          access_token: this.get('session.persistedAccessToken'),
          reason
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
        if (isIdle && this.get('session.persistedAccessToken')) {
          this._logout('Session Expired');
        }
      });
    }
  },

  sessionInvalidated() {

    if (!this.get('ssoEnabled')) { // do not change location if SSO is enabled

      const isInIframe = this._isInIframe();

      if (isInIframe) { // IF ember app inside iframe then set the parent location
        localStorage.setItem('rsa-post-auth-redirect', window.parent.location.href);
      } else {
        localStorage.setItem('rsa-post-auth-redirect', window.location.href);
      }

      // Need set the parent location if page is inserted in iframe
      if (isInIframe) {
        window.parent.location.replace('/login');
      } else {
        window.location.replace('/login');
      }

    }
  },

  _isInIframe() {
    return !!window.frameElement;
  },

  sessionAuthenticated() {
    this.transitionTo('protected');
  }

});
