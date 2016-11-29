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

  actions: {
    error(message) {
      this.get('fatalErrors').logError(message);
      this.transitionTo('not-found');
    },

    clearFatalErrorQueue() {
      this.get('fatalErrors').clearQueue();
    }
  },

  sessionAuthenticated() {
    const query = window.location.search;
    if (typeof query !== 'undefined' && query.indexOf('?next=') == 0) {
      // invoke redirect
      window.location = query.substring(6);
    } else {
      this.set('session.isFullyAuthenticated', true);
      this._super(...arguments);
    }
  },

  sessionInvalidated() {
    this.set('session.isFullyAuthenticated', false);
    this._super(...arguments);
  }
});
