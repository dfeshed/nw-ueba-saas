import Route from 'ember-route';
import service from 'ember-service/inject';

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
