/**
 * @file 404 route
 * Defines the catch-all route that covers URL paths which do not match any other routes.  We could show a
 * Not Found error UI, but for now we simply redirect to the default route.
 * @public
 */
import Ember from 'ember';
import UnauthenticatedRouteMixin from 'ember-simple-auth/mixins/unauthenticated-route-mixin';

const { Route } = Ember;

export default Route.extend(UnauthenticatedRouteMixin, {
  redirect() {
    const url = this.router.location.formatURL('/404');
    if (window.location.pathname !== url) {
      this.transitionTo('/404');
    }
  }
});
