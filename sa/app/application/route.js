import Ember from 'ember';
import ApplicationRouteMixin from 'ember-simple-auth/mixins/application-route-mixin';

const {
  Route,
  inject: {
    service
  }
} = Ember;

export default Route.extend(ApplicationRouteMixin, {
  fatalErrors: service(),

  _initialize: function() {
    const session = this.get('session').get('session');
    if (session) {
      session.addObserver('content.authenticated', this, this._checkFully);
    }
  }.on('init'),

  actions: {
    error(message) {
      this.get('fatalErrors').logError(message);
      this.transitionTo('not-found');
    },

    clearFatalErrorQueue() {
      this.get('fatalErrors').clearQueue();
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
    this._super(...arguments);
  }
});
