import Ember from 'ember';

const { Route,
  RSVP: {
    hash
    }
  } = Ember;

export default Route.extend({
  model(params) {
    return hash({
      incident: this.store.findRecord('incident', params.incident_id),
      users: this.store.findAll('user')
    });
  }
});
