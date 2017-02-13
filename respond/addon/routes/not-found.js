import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({
  // Always transition to incidents page directly if the route is unknown
  beforeModel() {
    this.transitionTo('incidents');
  }
});
