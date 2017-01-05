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
  Logger,
  String: {
    htmlSafe
  }
} = Ember;

export default Component.extend({
  eventBus: service('event-bus'),
  fatalErrors: service(),

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

  loadQueue(queryFilter) {
    this.setProperties({
      'loadingData': true,
      'incidents': null
    });

    this.get('request').streamRequest({
      method: 'stream',
      modelName: 'incident',
      query: {
        subDestinationUrlParams: 'new',
        sort: [{ field: 'riskScore', descending: true }],
        filter: queryFilter
      },
      onResponse: ({ data }) => {
        this.set('loadingData', false);
        if (data) {
          this.set('incidents', data);
        }
      },
      onError: (error) => {
        this.set('loadingData', false);
        Logger.error(`Error loading incidents. Error: ${error}`);
        this.get('fatalErrors').logError(htmlSafe(this.get('i18n').t('respond.errors.unexpected')));
      },
      onTimeout: () => {
        this.set('loadingData', false);
        Logger.warn('Timeout loading incidents.');
        this.get('fatalErrors').logError(htmlSafe(this.get('i18n').t('respond.errors.timeout')));
      }
    });
  },

  fetchModel() {
    const queue = this.get('activeTab');
    const filters = [ { field: 'statusSort', values: incidentStatusIds }];

    if (queue === 'my') {
      filters.addObject({ field: 'assignee.id', value: this.get('username') });
    }
    this.loadQueue(filters);
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