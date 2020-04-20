/**
 * @file 404 route
 * Defines the catch-all route that covers URL paths which do not match any
 * other routes.  We could show a Not Found error UI, but for now we simply
 * redirect to the default route.
 * @public
 */
import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

export default Route.extend({
  session: service(),

  redirect() {
    if (!this.get('session.isAuthenticated')) {
      this.transitionTo('login');
    } else {
      const url = this._routerMicrolib.location.formatURL('/not-found');
      if (window.location.pathname !== url) {
        this.transitionTo('/not-found');
      }
    }
  }
});
