import Route from '@ember/routing/route';
import { inject as service } from '@ember/service';

/**
  Responsible for making the login route available to parent application.
  @public
*/
export default Route.extend({
  session: service(),

  beforeModel() {
    if (this.get('session.isAuthenticated')) {
      this.transitionTo('protected');
    }
  }
});
