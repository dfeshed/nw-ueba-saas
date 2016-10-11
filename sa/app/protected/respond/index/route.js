import Ember from 'ember';
import { incidentStatusIds, incStatus } from 'sa/incident/constants';
import IncidentsCube from 'sa/utils/cube/incidents';

const {
  Route,
  inject: {
    service
  },
  $,
  set,
  Logger,
  observer,
  run
} = Ember;

export const viewType = {
  LIST_VIEW: 'listView',
  NEW_INC_CARD_VIEW: 'newIncCardView',
  IN_PROG_INC_CARD_VIEW: 'inProgIncCardView'
};

export default Route.extend({
  session: service(),
  layoutService: service('layout'),
  respondMode: service(),
  listViewCube: null,
  cardViewCube: null,

  activate() {
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'main');
    this.set('layoutService.actionConfig', null);
    /* close the incident queue panel, if it is in open state */
    let disabledQueue = $('.incident-queue-trigger.is-disabled');
    if (disabledQueue.length <= 0) {
      $('.incident-queue-trigger').click();
    }
  },

  deactivate() {
    this.set('layoutService.actionConfig', 'app');
  },

  /*
   * Creates a new stream object.
   * @param filter - array of object to filter the stream data
   * @param sort  - array of object to filter the stream data
   * @private
   */
  _createStream(filter, sort, subDestinationUrlParams, cube) {
    cube.set('status', 'wait');
    cube.set('users', {});
    this.request.streamRequest({
      method: 'stream',
      modelName: 'incident',
      query: {
        subDestinationUrlParams,
        sort,
        filter
      },
      onResponse({ data }) {
        if (data) {
          cube.get('records').pushObjects(data);
        }
        cube.set('status', 'streaming');
      },
      onError(response) {
        Logger.error('Error processing stream call for incident model', response);
      }
    });
  },

  /*
   * Creates a new stream notify object
   * @private
   */
  _createNotify(cubes, filterFunc) {
    let username;
    let _currentSession = this.get('session');

    if (_currentSession) {
      username = _currentSession.session.content.authenticated.user.id;
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

    [username, 'all_incidents'].forEach((subDestinationUrlParams) => {
      this.request.streamRequest({
        method: 'notify',
        modelName: 'incident',
        query: {
          subDestinationUrlParams
        },
        streamOptions: {
          requireRequestId: false
        },
        onResponse: ({ data, notificationCode }) => {
          this._updateCube(data, cubes, filterFunc, notificationCode);
        },
        onError(response) {
          Logger.error('Error processing notify call for incident model', response);
        }
      });
    });

  },

  /*
   * Updates the cube with the latest set of modifications
   * @param incidents - array of incidents that has to be updated
   * @private
   */
  _updateCube(incidents, cubes, filterFunc, notificationCode) {
    incidents.setEach('asyncUpdate', true);

    // If the user is in card view and the status is not in 'new', 'assigned' or 'in progress' remove it
    if (this.get('respondMode.selected') === 'card') {
      let incidentsToBeRemoved = [];
      let cardViewStatuses = [incStatus.ASSIGNED,incStatus.IN_PROGRESS, incStatus.NEW ];

      incidentsToBeRemoved = incidents.filter((incident) => {
        return (cardViewStatuses.indexOf(incident.statusSort) < 0);
      });
      cubes.forEach((cube) => {
        incidentsToBeRemoved.forEach((incident, index) => {
          let records = cube.get('records');
          if (records.findBy('id', incident.id)) {
            records.edit(incident.id, {}, true);
            incidentsToBeRemoved.splice(index, 1);
          }
        });
      });
    }

    let filteredIncidents = filterFunc(incidents);
    let [newIncidentsCube, inProgressCube] = cubes;

    filteredIncidents.forEach((incidents, index) => {
      // For each of the updated incident, check if the incident already exists in the cube.
      // If so, edit with the latest value, else add it to the list of records
      let records = cubes[ index ].get('records');
      let recordsToAdd = [];

      // notificationCode 0 => incidents was added, 1 => incidents were edited, 2 => incidents were deleted
      if (notificationCode === 0) {
        records.pushObjects(incidents);
      } else if (notificationCode === 1) {
        incidents.forEach((incident) => {
          // For each of the updated incident, check if the incident already exists in the cube.
          // If so, edit with the latest value, else add it to the list of records
          if (records.findBy('id', incident.id)) {
            records.edit(incident.id, incident);
          } else {
            /*
             add the incident to be pushed to cube to a temporary array. we don't want
             to trigger cube's calculations for every single push.
             we'll do a bulk push to trigger the cube calculations just once.
             */
            recordsToAdd.pushObject(incident);
            // Remove the updated items from the other cube (for ex, when status is changed, we need to delete the incident from the
            // older cube instance)
            if (incident.statusSort === incStatus.NEW) {
              inProgressCube.get('records').edit(incident.id, {}, true);
            } else if (incident.statusSort === incStatus.IN_PROGRESS || incident.statusSort === incStatus.ASSIGNED) {
              newIncidentsCube.get('records').edit(incident.id, {}, true);
            }
          }
        });
      }

      if (recordsToAdd.length > 0) {
        records.pushObjects(recordsToAdd);
      }
    });
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
      let newCube = IncidentsCube.create({
        array: []
      });
      let inProgressCube = IncidentsCube.create({
        array: [],
        sortField: 'lastUpdated'
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
            return (incident.statusSort === incStatus.ASSIGNED) || (incident.statusSort === incStatus.IN_PROGRESS);
          })
        ]);

      incidentModels = {
        newIncidents: newCube,
        inProgressIncidents: inProgressCube
      };
      this.set('cardViewCube', incidentModels);
    } else {
      let incidentsCube = IncidentsCube.create({
        array: [],
        categoryTags: []
      });
      this.set('listViewCube', incidentsCube);

      // Kick off the initial page load data request.
      this._createStream([{ field: 'statusSort', values: incidentStatusIds }],
        [{ field: 'prioritySort', descending: true }],
        'new',
        incidentsCube);

      this.request.streamRequest({
        method: 'stream',
        modelName: 'category-tags',
        query: {},
        onResponse: ({ data }) => {
          set(incidentsCube, 'categoryTags', data);
        },
        onError() {
          Logger.error('Error loading tags');
        }
      });
      // kick off both the async update stream
      this._createNotify([incidentsCube], (incidents)=>[incidents]);

      incidentModels = {
        allIncidents: incidentsCube
      };
    }

    return incidentModels;
  },

  afterModel(resolvedModel) {
    this.get('store').findAll('user').then((user) => {
      if (this.get('respondMode.selected') === 'card') {
        set(resolvedModel.newIncidents, 'users', user);
        set(resolvedModel.inProgressIncidents, 'users', user);
      } else {
        set(resolvedModel.allIncidents, 'users', user);
      }
    }).catch(() => {
      Logger.error('Error getting users');
    });
  },

  actions: {

    /**
     * @description Action handler that gets invoked when the user sorts incidents.
     * @param field Describes how to sort the incidents. Example: A 'Risk Score' field will sort incidents by Risk Score.
     * @param direction Describes how to order sorted incidents. Options: Ascending and Descending
     * @param view Descibes which incidents should be sorted. Options: ListView, New Incidents in Card View, or In Progress Incidents in Card View.
     * @public
     */
    sortAction(field, direction, view) {
      let cube;

      switch (view) {
        case viewType.NEW_INC_CARD_VIEW:
          cube = this.get('cardViewCube.newIncidents');
          break;
        case viewType.IN_PROG_INC_CARD_VIEW:
          cube = this.get('cardViewCube.inProgressIncidents');
          break;
        case viewType.LIST_VIEW:
          cube = this.get('listViewCube');
          break;
        default:
          return; // error - view type must be specified to perform sort
      }

      run(() => {
        cube.sort(field, (direction === 'desc'));
      });
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

    /**
     * @description Action handler that gets invoked when the user updates an incident.
     * @param incId IncidentID
     * @param attributeChanged The hash of keys and values to set
     * @public
     */
    saveIncident(incId, attributeChanged) {
      Logger.debug(`Updating incident ${ incId }`);

      this.store.queryRecord('incident', { incidentId: incId })
        .then(function(model) {
          if (model) {
            Logger.log(`incident ${ model.id } found`);

            model.setProperties(attributeChanged);

            Logger.log(`Saving incident model ${ model.get('id') }`);
            model.save().then(() => {
              Logger.debug('Incident was saved');
            }).catch((reason) => {
              Logger.error(`Error saving incident. Reason: ${ reason }`);
            });
          } else {
            Logger.warn('Incident model not found');
          }
        });
    }
  }
});
