import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({
  // Always transition to files page directly if the user hits the base /files/ path
  beforeModel() {
    this.transitionTo('files');
  }
});
