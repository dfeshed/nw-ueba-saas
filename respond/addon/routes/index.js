import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({
  // Always transition to incidents page directly if the user hits the base /response/ path
  beforeModel() {
    this.transitionTo('incidents');
  }
});
