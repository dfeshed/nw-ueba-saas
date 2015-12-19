import Ember from 'ember';

export default Ember.Route.extend({
  model(params) {
    // jscs:disable requireCamelCaseOrUpperCaseIdentifiers
    return this.store.peekRecord('spec', params.spec_id);
  }
});
