import Ember from 'ember';

export default Ember.Route.extend({
  model(params) {
    // jscs:disable requireCamelCaseOrUpperCaseIdentifiers
    return this.store.findRecord('incident', params.incident_id);
  }
});
