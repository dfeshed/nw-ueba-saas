import Ember from 'ember';
import { incidentStatusIds, incStatus } from 'sa/incident/constants';
import IncidentsCube from 'sa/utils/cube/incidents';
import PersistenceHelper from 'sa/components/rsa-respond/landing-page/respond-index/list-view/persistence-helper';
import NotificationHelper from 'sa/protected/responded/mixins/notificationHelper';

const {
  Route,
  inject: {
    service
  },
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

export default Route.extend(NotificationHelper, {
  session: service(),
  contextualHelp: service(),
  layoutService: service('layout'),
  respondMode: service(),
  i18n: service(),
  listViewCube: null,
  cardViewCube: null,
  persistenceHelper: PersistenceHelper.create(),

  title() {
    return this.get('i18n').t('pageTitle', { section: this.get('i18n').t('responded.myQueue') });
  },

  /*
  gets the sort field and order required for making model available for the component.
  Delegates the responsibility to Persistence Helper to provide persisted sort column information.
  defaults to 'riskscore' if persisted sort field and sort order could not be obtained.
  @private
  */
  _getDefaultListSort() {
    return this.get('persistenceHelper').getSortedColumn() || { field: 'riskScore', descending: true };
  },

  activate() {
    this.set('layoutService.main', 'panelB');
    this.set('layoutService.panelA', 'quarter');
    this.set('layoutService.panelB', 'main');
    this.set('layoutService.actionConfig', null);
    /* close the incident queue panel, if it is in open state */
    const queueExpanded = this.get('layoutService.incidentQueueActive');
    if (queueExpanded) {
      this.get('layoutService').toggleIncidentQueue();
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
    cube.set('users', []);
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
      onError: (error) => {
        Logger.error(`Unexpected error. method: stream, model: incident, subDestinationUrlParams: ${ subDestinationUrlParams }. Error: ${error}`);
        this.displayFatalUnexpectedError();
      },
      onTimeout: () => {
        Logger.error(`Timeout. method: stream, model: incident, subDestinationUrlParams: ${ subDestinationUrlParams }`);
        this.displayFatalTimeoutError();
      }
    });
  },

  /*
   * Creates a new stream notify object
   * @private
   */
  _createNotify(cubes, filterFunc) {
    let username;
    const _currentSession = this.get('session');

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
        onError: (error) => {
          Logger.error(`Unexpected error. method: notify, model: incident, subDestinationUrlParams: ${ subDestinationUrlParams }. Error: ${error}`);
          this.displayFatalUnexpectedError();
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

    if (notificationCode !== 2) {
      incidents.setEach('asyncUpdate', true);
    }

    // If the user is in card view and the status is not in 'new', 'assigned' or 'in progress' remove it
    if (this.get('respondMode.selected') === 'card') {
      let incidentsToBeRemoved = [];
      const cardViewStatuses = [incStatus.ASSIGNED, incStatus.IN_PROGRESS, incStatus.NEW ];

      incidentsToBeRemoved = incidents.filter((incident) => {
        return (cardViewStatuses.indexOf(incident.statusSort) < 0);
      });
      cubes.forEach((cube) => {
        incidentsToBeRemoved.forEach((incident, index) => {
          const records = cube.get('records');
          if (records.findBy('id', incident.id)) {
            records.edit(incident.id, {}, true);
            incidentsToBeRemoved.splice(index, 1);
          }
        });
      });
    }

    const filteredIncidents = filterFunc(incidents);
    const [newIncidentsCube, inProgressCube] = cubes;

    filteredIncidents.forEach((incidents, index) => {
      // For each of the updated incident, check if the incident already exists in the cube.
      // If so, edit with the latest value, else add it to the list of records
      const records = cubes[ index ].get('records');
      const recordsToAdd = [];

      // notificationCode 0 => incidents was added, 1 => incidents were edited, 2 => incidents were deleted
      switch (notificationCode) {
        case 0:
          records.pushObjects(incidents);
          break;
        case 1:
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

          if (recordsToAdd.length > 0) {
            records.pushObjects(recordsToAdd);
          }
          break;
        case 2:
          cubes.forEach((cube) => {
            incidents.forEach((incidentId) => {
              const records = cube.get('records');
              records.edit(incidentId, {}, true);
            });
          });
          break;
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

    const newCube = IncidentsCube.create({
      array: []
    });
    const inProgressCube = IncidentsCube.create({
      array: [],
      sortField: 'lastUpdated'
    });
    const allIncidentsCube = IncidentsCube.create({
      array: [],
      categoryTags: []
    });

    const incidentModels = {
      allIncidents: allIncidentsCube,
      newIncidents: newCube,
      inProgressIncidents: inProgressCube
    };

    if (this.get('respondMode.selected') === 'card') {

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
        (incidents) => [
          incidents.filterBy('statusSort', incStatus.NEW),
          incidents.filter((incident) => {
            return (incident.statusSort === incStatus.ASSIGNED) || (incident.statusSort === incStatus.IN_PROGRESS);
          })
        ]);
      this.set('cardViewCube', incidentModels);

    } else {

      this.set('listViewCube', allIncidentsCube);

      // Kick off the initial page load data request.

      this._createStream([{ field: 'statusSort', values: incidentStatusIds }],
        [ this._getDefaultListSort() ],
        'new',
        allIncidentsCube);

      this.request.streamRequest({
        method: 'stream',
        modelName: 'category-tags',
        query: {},
        onResponse: ({ data }) => {
          set(allIncidentsCube, 'categoryTags', data);
        },
        onError: (error) => {
          Logger.warn(`Error loading categoryTags. Error: ${error}`);
          this.displayFlashErrorLoadingModel('categoryTags');
        },
        onTimeout: () => {
          Logger.warn('Timeout loading categoryTags.');
          this.displayFlashErrorLoadingModel('categoryTags');
        }
      });
      // kick off both the async update stream
      this._createNotify([allIncidentsCube], (incidents) => [incidents]);
    }

    return incidentModels;
  },

  afterModel(resolvedModel) {
    this.request.streamRequest({
      method: 'stream',
      modelName: 'users',
      query: {},
      onResponse: ({ data: users }) => {
        if (this.get('respondMode.selected') === 'card') {
          resolvedModel.newIncidents.users.addObjects(users);
          resolvedModel.inProgressIncidents.users.addObjects(users);
        } else {
          resolvedModel.allIncidents.users.addObjects(users);
        }
      },
      onError: (error) => {
        Logger.warn(`Error loading users. Error: ${error}`);
        this.displayFlashErrorLoadingModel('users');
      },
      onTimeout: () => {
        Logger.warn('Timeout loading users.');
        this.displayFlashErrorLoadingModel('users');
      }
    });
  },

  actions: {
    /**
     * @name bulkSave
     * @description Modifies a set of incidents based on the property and value sent in an object and the incident ID's listed in an array.
     * @param updateObject Contains a property and value that should be changed
     * @param arrayOfIncidentIDs Contains a set of incident ID's as strings
     * @public
     */
    bulkSave(updateObject, arrayOfIncidentIDs) {
      this.request.promiseRequest({
        method: 'updateRecord',
        modelName: 'incident',
        query: {
          incidentIds: arrayOfIncidentIDs,
          updates: updateObject
        }
      }).then((response) => {
        Logger.debug(`Successfully saved: ${ response }`);
        this.displaySuccessFlashMessage('incident.edit.update.bulkSuccessfulMessage', { count: arrayOfIncidentIDs.length });
      }).catch((reason) => {
        Logger.error(`Unable to save: ${ reason }`);
        this.displayErrorFlashMessage('incident.edit.update.errorMessage');
      });
    },

    /**
     * @name bulkDelete
     * @description Deletes an incident or incidents from the database
     * @param arrayOfIncidentIDs Contains a set of incident ID's as strings
     * @public
     */
    bulkDelete(arrayOfIncidentIDs) {

      this.request.promiseRequest({
        method: 'deleteRecord',
        modelName: 'incident',
        query: {
          sort: [{
            field: '_id',
            descending: true
          }],
          filter: [{
            field: '_id',
            values: arrayOfIncidentIDs
          }]
        }
      }).then((response) => {
        Logger.debug(`successfully deleted ${response}`);
        this.displaySuccessFlashMessage('incident.edit.delete.bulkSuccessfulMessage', { count: arrayOfIncidentIDs.length });
      }).catch((reason) => {
        Logger.error(`unable to delete incidents: ${reason}`);
        this.displayErrorFlashMessage('incident.edit.delete.errorMessage');
      });
    },

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
        this.transitionTo('protected.responded.incident', json.id);
      }
    },

    /**
     * @description Action handler that gets invoked when the user updates an incident.
     * @param incId IncidentID
     * @param attributesChanged The hash of keys and values to set
     * @public
     */
    saveIncident(incId, attributesChanged) {
      Logger.debug(`Updating incident ${ incId }`);

      this.store.queryRecord('incident', { incidentId: incId })
        .then((model) => {
          if (model) {
            Logger.log(`incident ${ model.id } found`);
            model.setProperties(attributesChanged);

            model.save().then(() => {
              this.displaySuccessFlashMessage('incident.edit.update.singleSuccessfulMessage');
            }).catch((reason) => {
              Logger.error(`Error saving incident. Reason: ${ reason }`);
              this.displayErrorFlashMessage('incident.edit.update.errorMessage');
            });

          } else {
            Logger.warn('Incident model not found');
          }
        });
    }
  }
});
