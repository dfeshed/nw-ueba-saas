/**
 * @file not-found route
 * Defines the catch-all route that covers URL sub-paths of 'protected' which do not match any other routes.
 * We could show a Not Found error UI, but for now we simply redirect to the default protected sub-route.
 * @public
 */
import Ember from 'ember';

export default Ember.Route.extend({
  beforeModel() {

    // By default, try to redirect to the default sub-route under 'protected'.
    this.transitionTo('protected.index');
  }
});
