import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';
import csrfToken from 'component-lib/mixins/csrf-token';

const {
  $,
  Route,
  RSVP,
  inject: {
    service
  },
  testing
} = Ember;

export default Route.extend(ApplicationRouteMixin, csrfToken, {
  fatalErrors: service(),
  session: service(),
  userIdle: service(),
  i18n: service(),

  title() {
    return this.get('i18n').t('application.title');
  },

  init() {
    if (!testing) {
      // After 10 idle minutes, logout
      this.get('userIdle').on('idleChanged', (isIdle) => {
        if (isIdle) {
          this._logout();
        }
      });
    }

    this._super(...arguments);
  },

  _initialize: function() {
    const session = this.get('session').get('session');
    if (session) {
      session.addObserver('content.authenticated', this, this._checkFully);
    }
  }.on('init'),

  actions: {
    back() {
      history.back();
    },
    clearFatalErrorQueue() {
      this.get('fatalErrors').clearQueue();
    },
    error(message) {
      this.get('fatalErrors').logError(message);
      this.transitionTo('404');
    },
    logout() {
      this._logout();
    }
  },

  _checkFully() {
    const isRedirecting = localStorage.getItem('_redirecting');
    const query = window.location.search;
    if (!this.get('session.isAuthenticated') && typeof query !== 'undefined' && query.indexOf('?next=') == 0 && isRedirecting == null) {
      localStorage.setItem('_redirecting', 'true');
    } else {
      localStorage.removeItem('_redirecting');
    }
  },

  _logout() {
    return new RSVP.Promise((resolve) => {
      const csrfKey = this.get('csrfLocalstorageKey');
      $.ajax({
        type: 'POST',
        url: '/oauth/logout',
        timeout: 3000,
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

  sessionAuthenticated() {
    const isRedirecting = localStorage.getItem('_redirecting');
    if (typeof isRedirecting !== 'undefined') {
      // invoke redirect
      localStorage.removeItem('_redirecting');
      const query = window.location.search;
      window.location = query.substring(6);
    } else {
      this._super(...arguments);
    }
  },

  sessionInvalidated() {
    if (!testing) {
      window.location.replace('/login');
    }
  }
});
