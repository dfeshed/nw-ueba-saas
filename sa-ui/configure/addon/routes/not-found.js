import Route from '@ember/routing/route';

export default Route.extend({
  // Always transition to index route if the route is unknown
  beforeModel() {
    this.transitionTo('respond.incident-rules');
  }
});
