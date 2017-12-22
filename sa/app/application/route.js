import $ from 'jquery';
import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';
import csrfToken from 'component-lib/mixins/csrf-token';
import Route from 'ember-route';
import RSVP from 'rsvp';
import service from 'ember-service/inject';
import config from 'ember-get-config';

const {
  testing
} = Ember;

export default Route.extend(ApplicationRouteMixin, csrfToken, {
  fatalErrors: service(),
  session: service(),
  userActivity: service(),
  userIdle: service(),

  persistStateOnLogout: true,

  title() {
    return this.get('i18n').t('application.title');
  },

  beforeModel(transition) {
    const locale = localStorage.getItem('rsa-i18n-default-locale');

    if ((config.i18n.includedLocales.length <= 1) || !locale) {
      localStorage.setItem('rsa-i18n-default-locale', config.i18n.defaultLocale);
      this.set('i18n.locale', config.i18n.defaultLocale);
    } else {
      this.set('i18n.locale', locale);
    }

    if (
      !this.get('session.isAuthenticated') &&
      transition.targetName !== 'login' &&
      transition.targetName !== 'protected' &&
      transition.targetName !== 'protected.index'
    ) {
      localStorage.setItem('rsa-post-auth-redirect', window.location.href);
    }
    this._setupUserTimeout();
    this._super(...arguments);
  },

  actions: {
    back() {
      history.back();
    },
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
    return new RSVP.Promise((resolve) => {
      const csrfKey = this.get('csrfLocalstorageKey');
      $.ajax({
        type: 'POST',
        url: '/oauth/logout',
        timeout: 15000,
        headers: {
          'X-CSRF-TOKEN': localStorage.getItem(csrfKey)
        },
        data: {
          access_token: this.get('session').get('data.authenticated.access_token')
        }
      })
      .always(() => {
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
      // When the user performs an action, update last session access
      this.get('userActivity').on('userActive', this, () => {
        localStorage.setItem('rsa-nw-last-session-access', new Date().getTime());
      });

      // After configured idle timeout period, logout
      this.get('userIdle').on('idleChanged', (isIdle) => {
        if (isIdle && this.get('session.isAuthenticated')) {
          this._logout();
        }
      });
    }
  },

  sessionInvalidated() {
    if (this.get('persistStateOnLogout')) {
      if (this._isInIframe()) { // IF ember app inside iframe then set the parent location
        localStorage.setItem('rsa-post-auth-redirect', window.parent.location.href);
      } else {
        localStorage.setItem('rsa-post-auth-redirect', window.location.href);
      }
    }
    window.location.replace('/login');
  },

  _isInIframe() {
    return !!window.frameElement;
  },

  sessionAuthenticated() {
    this.transitionTo('protected');
  }

});
