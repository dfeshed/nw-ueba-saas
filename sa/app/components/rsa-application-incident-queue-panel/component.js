/**
 * @file Incident Tab component
 *
 * This component is used to display "My Incidents" and "All Incidents" tabs in a panel.
 * Component is collapsed by default and expanded upon interaction.
 * @public
 */
import Ember from 'ember';
import { incidentStatusIds } from 'sa/incident/constants';
import computed from 'ember-computed-decorators';

const {
  Component,
  Object: EmberObject,
  inject: {
    service
  },
  Logger
} = Ember;

export default Component.extend({
  eventBus: service('event-bus'),

  layoutService: service('layout'),

  classNames: ['rsa-application-incident-queue-panel'],

  classNameBindings: ['isExpanded'],

  isExpanded: false,

  request: service(),

  incidents: null,

  // Flag to determine if loading indicator must be displayed
  loadingData: false,

  // currently active tab
  activeTab: 'my', // 'my' or 'all'

  session: service(),

  // Config that overrides dynamically set default width of rsa-data-table
  customColumnConfig: [
    EmberObject.create({
      width: '100%'
    })
  ],

  // gets the logged in user id
  @computed('session')
  username(session) {
    let username;
    if (session) {
      username = session.get('session.content.authenticated.user.id');
    } else {
      Logger.error('unable to read current username');
    }
    return username;
  },

  init() {
    this._super(arguments);

    this.get('eventBus').on('rsa-application-incident-queue-panel-will-toggle', () => {
      this.toggleProperty('isExpanded');
      if (this.get('isExpanded') === true) {
        this.fetchModel();
      } else {
        this.set('loadingData', false);
        this.set('incidents', null);
      }
    });
  },

  loadQueue(method, subDestinationUrlParams) {
    this.set('loadingData', true);

    this.get('request').streamRequest({
      method,
      modelName: 'incident',
      query: {
        filter: [{ field: 'statusSort', values: incidentStatusIds }],
        sort: [{ field: 'riskScore', descending: true }],
        subDestinationUrlParams
      },
      onResponse: ({ data }) => {
        this.set('loadingData', false);
        if (data) {
          this.set('incidents', data);
        }
      },
      onError: () => {
        Logger.error('Error processing stream call for incident queue');
        this.set('loadingData', false);
      }
    });
  },

  fetchModel() {
    const queue = this.get('activeTab');
    if (queue === 'my') {
      this.loadQueue('notify', this.get('username'));
    } else {
      this.loadQueue('stream', 'new');
    }
  },

  actions: {
    fetchIncidents(queue) {
      this.set('activeTab', queue);
      this.fetchModel();
    },
    gotoIncidentDetail() {
      /* simulates the "incident queue" button click that in-turn hides the
       incident queue panel when an incident card is clicked */
      this.get('layoutService').toggleIncidentQueue();
    }
  }

});