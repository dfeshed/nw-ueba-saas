import Ember from 'ember';

const { Route } = Ember;

export default Route.extend({
  model(params) {
    // jscs:disable requireCamelCaseOrUpperCaseIdentifiers
    return this.store.findRecord('incident', params.incident_id);
  }
});
