import $ from 'jquery';
import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';
import csrfToken from 'component-lib/mixins/csrf-token';
import Route from 'ember-route';
import RSVP from 'rsvp';
import service from 'ember-service/inject';

const {
  testing
} = Ember;

export default Route.extend(ApplicationRouteMixin, csrfToken, {
  fatalErrors: service(),
  session: service(),
  userActivity: service(),
  userIdle: service(),

  title() {
    return this.get('i18n').t('application.title');
  },

  beforeModel(transition) {
    this._super(...arguments);

    this._saveTransition(transition);
    if (this.get('session.isAuthenticated')) {
      this._setupUserTimeout();
    }
  },

  actions: {
    back() {
      history.back();
    },
    clearFatalErrorQueue() {
      this.get('fatalErrors').clearQueue();
    },
    error(message) {
      this.get('fatalErrors').logError(message);
      this.transitionTo('not-found');
    },
    logout() {
      this._logout();
    },
    willTransition(transition) {
      this._logLastTransition(transition);
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
          this._saveTransition(this.get('lastLoggableTransition'));
          this.get('session').invalidate();
          resolve();
        });
    });
  },

  _logLastTransition(transition) {
    const path = this.controllerFor('application').get('currentPath');
    if ((path !== 'login') && (path !== 'protected.index') && (path !== 'protected')) {
      this.set('lastLoggableTransition', transition);
    }
  },

  _saveTransition(transition) {
    if (transition) {
      const ids = [];
      const path = transition.targetName;

      if ((path !== 'login') && (path !== 'protected.index') && (path !== 'protected')) {
        Object.keys(transition.params).forEach((paramKey) => {
          Object.keys(transition.params[paramKey]).forEach((interiorKey) => {
            ids.push(transition.params[paramKey][interiorKey]);
          });
        });

        localStorage.setItem('rsa-post-auth-redirect-name', transition.targetName);

        if (ids.length >= 1) {
          localStorage.setItem('rsa-post-auth-redirect-ids', JSON.stringify(ids));
        }

        if (Object.keys(transition.queryParams).length >= 1) {
          localStorage.setItem('rsa-post-auth-redirect-params', JSON.stringify(transition.queryParams));
        }
      }
    }
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
        if (isIdle) {
          this._logout();
        }
      });
    }
  },

  sessionAuthenticated() {
    this._setupUserTimeout();
    this._super(...arguments);
  },

  sessionInvalidated() {
    if (!testing) {
      window.location.replace('/login');
    }
  }
});
