import Ember from 'ember';
import { incidentStatusIds, incStatus } from 'sa/incident/constants';
import IncidentsCube from 'sa/utils/cube/incidents';

const {
  Route,
  inject: {
    service
  },
  Logger,
  observer,
  RSVP,
  merge,
  run,
  Object: EmberObject
} = Ember;

export default Route.extend({
  session: service(),
  respondMode: service(),

  // Array holding the list of all subscriptions
  currentStreams: [],

  /*
   * Creates a new stream object.
   * @param filter - array of object to filter the stream data
   * @param sort  - array of object to filter the stream data
   * @private
   */
  _createStream(filter, sort, subDestinationUrlParams, cube) {

    let stream = this.store.stream('incident', {
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
        Logger.error('Error processing stream call for incident model');
      });

    this.get('currentStreams').push(stream);
  },

  /*
   * Creates a new stream notify object
   * @private
   */
  _createNotify(cubes, filterFunc) {
    let username,
      _currentSession = this.get('session');

    if (_currentSession) {
      username = _currentSession.session.content.authenticated.username;
    } else {
      Logger.error('unable to read current username');
    }
    /* whenever an incident is added/edited/deleted, in order to get an aysnchronous update
     we trigger 2 socket streams, like this
     /topic/incidents/owner/<loggedin_username>
     /topic/incidents/owner/all_incidents
     The first socket call will return message whenever an incident assigned to the
     logged in user has been changed. The 2nd subscription will return any other incidents
     that the user is allowed to see based on their privileges.
     */
    let updateSocketParams = [username, 'all_incidents'],
      stream;

    updateSocketParams.forEach((subDestinationUrlParams) => {
      stream = this.store.notify('incident',
        { subDestinationUrlParams },
        { requireRequestId: false })
        .autoStart()
        .subscribe((response) => {
          let { data } = response;
          // notificationCode 0 => incidents was added, 1 => incidents were edited, 2 => incidents were deleted
          // @TODO: not handling the delete incidents case yet
          if (response.notificationCode !== 2) {
            this._updateCube(data, cubes, filterFunc);
          }
        }, function() {
          Logger.error('Error processing notify call for incident model');
        });
      this.get('currentStreams').push(stream);
    });
  },

  /*
   * Updates the cube with the latest set of modifications
   * @param incidents - array of incidents that has to be updated
   * @private
   */
  _updateCube(incidents, cubes, filterFunc) {
    incidents.setEach('asyncUpdate', true);

    let currentCubes = cubes,
      filteredIncidents = filterFunc(incidents);

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

  /**
   * Finds an Ember model object based on POJO json with id attribute
   *
   * Assumes the given incident is a POJO (JSON), not an ember data model instance with an id.
   * It query the backend and returns a promise with the model object
   * @param {object} json The Incident JSON.
   * @returns {promise}
   * @private
   */
  _findIncidentModel(json) {
    if (!json) {
      return null;
    }
    return this.store.findRecord('incident', json.id);
  },

  /**
   * @description Observes any change in the selected respond view mode to trigger a model reload. WillTransitions
   * is also been triggered to close any open connection before loading the model.
   * @private
   */
  // TODO: remove observer
  _respondeModeDidChange: observer('respondMode.selected', function() {
    this.refresh();
  }),

  /*
   * Populate multiple models by kicking of two streams to get the list of incidents.
   * @public
   */
  model() {

    let incidentModels;

    if (this.get('respondMode.selected') === 'card') {
      let newCube =  IncidentsCube.create({
          array: []
        }),
        inProgressCube =  IncidentsCube.create({
          array: []
        });

      // Kick off the initial page load data request.
      this._createStream([{ field: 'statusSort', value: incStatus.NEW }],
        [{ field: 'prioritySort', descending: true }],
        'new',
        newCube);
      this._createStream([{ field: 'statusSort', values: [incStatus.ASSIGNED, incStatus.IN_PROGRESS] }],
        [{ field: 'prioritySort', descending: true }],
        'inprogress',
        inProgressCube);

      // kick off both the async update stream
      this._createNotify(
        [newCube, inProgressCube],
        (incidents)=> [
          incidents.filterBy('statusSort', incStatus.NEW),
          incidents.filter((incident) => {
            return (incident.statusSort === incStatus.ASSIGNED) ||  (incident.statusSort === incStatus.IN_PROGRESS);
          })
        ]);

      incidentModels = {
        newIncidents: newCube,
        inProgressIncidents: inProgressCube
      };
    } else {
      let incidentsCube = IncidentsCube.create({
        array: []
      });

      // Kick off the initial page load data request.
      this._createStream([{ field: 'statusSort', values: incidentStatusIds }],
        [{ field: 'prioritySort', descending: true }],
        'new',
        incidentsCube);

      // kick off both the async update stream
      this._createNotify([incidentsCube], (incidents)=>[incidents]);

      incidentModels = {
        allIncidents: incidentsCube
      };
    }

    return RSVP.hash(
      merge(
        {
          users: this.store.findAll('user')
        },
        incidentModels
      ));
  },

  actions: {
    /**
     * @name willTransition
     * @description when the router will transit to another route, the opened stream are being closed
     * @public
     */
    willTransition() {
      let streamRequests = this.get('currentStreams');
      run(() => {
        streamRequests.forEach((streamRequest) => {
          streamRequest.stream.stop();
        });
      });
      this.set('currentStreams', []);
    },

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
      Logger.log(`updating incident ${ json.id }`);

      let promise = this._findIncidentModel(json);
      promise.then(function(model) {
        if (model) {
          Logger.log(`incident ${ model.id } found`);

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
              model.set('assignee', EmberObject.create());
            }
            model.set('assignee.id', json.assignee.id);
          }

          Logger.log(`Saving incident model with id ${ model.id }`);
          model.save();
        } else {
          Logger.warn('Incident model not found');
        }
      });
    }
  }
});
