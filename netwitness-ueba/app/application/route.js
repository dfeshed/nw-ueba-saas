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

export default Route.extend(ApplicationRouteMixin, csrfToken, {
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
    return DEFAULT_LOCALES;
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

    const csrfKey = this.get('csrfLocalstorageKey');
    if (csrfKey) {
      const formData = new FormData();
      formData.append('access_token', this.get('session.persistedAccessToken'));
      formData.append('reason', reason);
      fetch('/oauth/logout', {
        credentials: 'same-origin',
        method: 'POST',
        headers: {
          'X-CSRF-TOKEN': localStorage.getItem(csrfKey)
        },
        body: formData
      }).finally(() => {
        localStorage.removeItem(csrfKey);
        this.get('session').invalidate();
      });
    }
  },

  /**
   * Sets up the activity and inactivity timeout events
   * @private
   */
  _setupUserTimeout() {
    if (!testing) {
      // After configured idle timeout period, logout
      this.get('userIdle').on('idleChanged', (isIdle) => {
        // get diff between now and last access
        // compare to now, if diff is greater than idle timeout then logout
        // this comparison ensures accuracy between tabs
        const lastAccess = localStorage.getItem('rsa-nw-last-session-access');
        const idleTimeout = localStorage.getItem('rsa-x-idle-session-timeout');
        const timeSinceLastAccess = Date.now() - lastAccess;
        isIdle = isIdle && timeSinceLastAccess >= idleTimeout;

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
