/**
 * @file 404 route
 * Defines the catch-all route that covers URL paths which do not match any
 * other routes.  We could show a Not Found error UI, but for now we simply
 * redirect to the default route.
 * @public
 */
import Route from '@ember/routing/route';
import { inject } from '@ember/service';

export default Route.extend({
  session: inject(),
  router: inject(),

  redirect() {
    if (!this.get('session.isAuthenticated')) {
      this.transitionTo('login');
    } else {
      const location = this.get('router.location');
      const url = location && location.formatURL('/not-found');
      if (window.location.pathname !== url) {
        this.transitionTo('/not-found');
      }
    }
  }
});
