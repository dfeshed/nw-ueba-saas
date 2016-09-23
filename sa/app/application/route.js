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
  }
});
