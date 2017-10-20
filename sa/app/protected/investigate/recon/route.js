import Route from 'ember-route';


export default Route.extend({

  queryParams: {
    eventId: {
      refreshModel: false
    },
    endpointId: {
      refreshModel: false
    }
  }
});
