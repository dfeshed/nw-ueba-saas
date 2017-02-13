import Ember from 'ember';

const {
  Route
} = Ember;

export default Route.extend({
  model({ incident_id }) {
    return {
      incidentId: incident_id
    };
  }
});
