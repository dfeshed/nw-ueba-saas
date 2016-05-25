import Ember from 'ember';
import timeUtil from 'sa/utils/time';
import IncidentsCube from 'sa/utils/cube/incidents';

export default Ember.Route.extend({
  /**
   * The time range unit of the current data query. A value from the enumeration sa/utils/time.UNITS.
   * @type String
   * @public
   */
  timeRangeUnit: timeUtil.UNITS.DAY,

  /**
   * The time range for these data records. An object with 2 properties, 'from' and 'to', which are both UTC Dates
   * (in milliseconds) cast as long integers.  This property is computed from 'timeRangeUnit'. To change the
   * timeRange, simply update the timeRangeUnit.  Note that timeRange is always computed using now as the 'to' value.
   * @type {from: number, to: number}
   * @public
   */
  timeRange: Ember.computed('timeRangeUnit', function() {
    let now = Number(new Date());
    return {
      from: now - timeUtil.toMillisec(this.get('timeRangeUnit')),
      to: now
    };
  }),

  /*
   * Creates a new stream object and passes the appropriate timeRange values.
   * @param filter - array of object to filter the stream data
   * @param sort  - array of object to filter the stream data
   * @private
   */
  _createStream(sort, filter, reqDestinationUrlParams) {
    let timeRangeUnit = this.get('timeRangeUnit'),
      timeRange = this.get('timeRange'),
      newModel =  IncidentsCube.create({
        array: [],
        timeRangeUnit,
        timeRange
      });
    this.store.stream('incident', {
      sort,
      filter,
      reqDestinationUrlParams
    }, { requireRequestId: false }).autoStart()
      .toArray(newModel.get('records'));
    return newModel;
  },

  /*
   * Populate multiple models by kicking of two streams to get the list of incidents.
   * @public
   */
  model() {
    let newModel,
      inProgressModel;

    // Kick off the data request.
    newModel = this._createStream(
      [{ field: 'prioritySort', descending: true }],
      [{ field: 'statusSort', value: 0 }]
    );

    inProgressModel = this._createStream(
      [{ field: 'prioritySort', descending: false }],
      [{ field: 'statusSort', value: 1 }]
    );

    return ({
      newIncidents: newModel.array,
      inProgressIncidents: inProgressModel.array,
      users: this.store.findAll('user')
    });
  },

  /**
   * Finds an Ember model object based on POJO json with id attribute
   *
   * Assumes the given incident is a POJO (JSON), not an ember data model instance with an id.
   * It query the backend and returns a promise with the model object
   * @param {object} json The Incident JSON.
   * @returns {promise}
   * @public
   */
  findIncidentModel(json) {
    if (!json) {
      return null;
    }
    return this.store.findRecord('incident', json.id);
  },

  actions: {
    /*
     * Action handler that gets invoked when the user clicks on the tile.
     * populates the incident model if it is not in ember data store and
     * takes the user to the detail page
     */
    gotoIncidentDetail(json) {
      if (json && json.id) {
        this.transitionTo('protected.respond.incident', json.id);
      }
    },

    /*
     * Action handler that gets invoked when the user updates an incident.
     */
    saveIncident(json) {
      Ember.Logger.log(`updating incident`);

      let promise = this.findIncidentModel(json);
      promise.then(function(model) {
        if (model) {
          Ember.Logger.log(`updating incident ${ model.id }`);

          model.setProperties({
            'statusSort': json.statusSort,
            'prioritySort': json.prioritySort,
            'assignee.id': json.assignee.id
          });

          model.save();
        } else {
          Ember.Logger.warn('Incident model not found');
        }
      });
    }
  }
});
