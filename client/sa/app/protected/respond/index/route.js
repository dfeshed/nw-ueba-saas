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

  session: Ember.inject.service(),

  // cube holding all new incidents
  newCube: null,

  // cube holding inprogress incidents
  inProgressCube: null,

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

  // cube object that holds all "new" incidents
  newCube: null,

  // cube object that holds all "in progress" incidents
  inProgressCube: null,
  /*
  * Creates a new stream object and passes the appropriate timeRange values.
  * @param filter - array of object to filter the stream data
  * @param sort  - array of object to filter the stream data
  * @private
  */
  _createStream(filter, subDestinationUrlParams, cube) {
    let sort = [{ field: 'prioritySort', descending: true }];

    this.store.stream('incident', {
                  subDestinationUrlParams,
                  sort,
                  filter
                }, {
                  requireRequestId: false
                }).autoStart()
        .subscribe((response) => {
          let { data } = response;
          cube.get('records').pushObjects(data);
        }, function() {
          Ember.Logger.error('Error processing stream call for incident model');
        });
    return cube;
  },

  /*
  * Creates a new stream notify object
  * @private
  */
  _createNotify() {
    let username,
      _currentSession = this.get('session');

    if (_currentSession) {
      username = _currentSession.session.content.authenticated.username;
    } else {
      Ember.Logger.error('unable to read current username');
    }
    /* whenever an incident is added/edited/deleted, in order to get an aysnchronous update
      we trigger 2 socket streams, like this
      /topic/incidents/owner/<loggedin_username>
      /topic/incidents/owner/all_incidents
      The first socket call will return message whenever an incident assigned to the
      logged in user has been changed. The 2nd subscription will return any other incidents
      that the user is allowed to see based on their privileges.
    */
    let updateSocketParams = [username, 'all_incidents'];

    updateSocketParams.forEach((subDestinationUrlParams) => {
      this.store.notify('incident',
                  { subDestinationUrlParams },
                  { requireRequestId: false })
        .autoStart()
        .subscribe((response) => {
          let { data } = response;
          // notificationCode 0 => incidents was added, 1 => incidents were edited, 2 => incidents were deleted
          // @TODO: not handling the delete incidents case yet
          if (response.notificationCode !== 2) {
            this._updateCube(data);
          }
        }, function() {
          Ember.Logger.error('Error processing notify call for incident model');
        });
    });
  },

  /*
  * Updates the cube with the latest set of modifications
  * @param incidents - array of incidents that has to be updated
  * @private
  */
  _updateCube(incidents) {
    let currentCubes = [ this.get('newCube'), this.get('inProgressCube')],
      // filter the new and in progress incidents from the stream and push it to an array
      filteredIncidents = [ incidents.filterBy('statusSort', 0),
                            incidents.filterBy('statusSort', 1)
                          ];
    filteredIncidents.forEach((incidents, index) => {
      // For each of the updated incident, check if the incident already exists in the cube.
      // If so, edit with the latest value, else add it to the list of records
      let records = currentCubes[ index ].get('records'),
        recordsToAdd = [];
      incidents.forEach((incident) => {
        if (records.findBy('id', incident.id)) {
          records.edit(incident.id, incident);
        } else {
          /*
          add the incident to be pushed to cube to a temporary array. we don't want
          to trigger cube's calculations for every single push.
          we'll do a bulk push to trigger the cube calculations just once.
          */
          recordsToAdd.pushObject(incident);
        }
      });
      if (recordsToAdd.length > 0) {
        records.pushObjects(recordsToAdd);
      }
    });
  },

  /*
  * Populate multiple models by kicking of two streams to get the list of incidents.
  * @public
  */
  model() {
    let username,
        _currentSession = this.get('session');

    if (_currentSession) {
      username = _currentSession.session.content.authenticated.username;
    }
    let updateSocketParams = [username, 'all_incidents'],
      timeRangeUnit = this.get('timeRangeUnit'),
      timeRange = this.get('timeRange'),
      newCube =  IncidentsCube.create({
        array: [],
        timeRangeUnit,
        timeRange
      }),
     inProgressCube =  IncidentsCube.create({
        array: [],
        timeRangeUnit,
        timeRange
      });

    this.setProperties({
      newCube,
      inProgressCube
    });

    // Kick off the initial page load data request.
    this._createStream([{ field: 'statusSort', value: 0 }], 'new', newCube);
    this._createStream([{ field: 'statusSort', value: 1 }], 'inprogress', inProgressCube);
    // kick off both the async update stream
    this._createNotify();

    return Ember.RSVP.hash({
      newIncidents: newCube,
      inProgressIncidents: inProgressCube,
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
      Ember.Logger.log(`updating incident ${ json.id }`);

      let promise = this.findIncidentModel(json);
      promise.then(function(model) {
        if (model) {
          Ember.Logger.log(`incident ${ model.id } found`);

          model.setProperties({
            'statusSort': json.statusSort,
            'prioritySort': json.prioritySort
          });

          // Saving the assignee
          if (json.assignee.id === '-1') {
            // The incident has been un assigned.
            model.set('assignee', undefined);
          } else {
            // Before setting the new assignee-id, check the incident has an assignee object.
            if (model.get('assignee') === undefined) {
              model.set('assignee', Ember.Object.create());
            }
            model.set('assignee.id', json.assignee.id);
          }

          Ember.Logger.log(`Saving incident model with id ${ model.id }`);
          model.save();
        } else {
          Ember.Logger.warn('Incident model not found');
        }
      });
    }
  }
});
