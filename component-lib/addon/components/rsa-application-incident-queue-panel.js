import Ember from 'ember';
import layout from '../templates/components/rsa-application-incident-queue-panel';
import { incidentStatusIds } from 'sa/incident/constants';
import computed from 'ember-computed-decorators';

const {
  Component,
  $,
  inject: {
    service
  },
  Logger
} = Ember;

export default Component.extend({
  layout,

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

  actions: {
    fetchIncidents(queue) {
      this.set('activeTab', queue);
      if (queue === 'my') {
        this.loadQueue('notify', this.get('username'));
      } else {
        this.loadQueue('stream', 'new');
      }
    },
    gotoIncidentDetail() {
      /* simulates the "incident queue" button click that in-turn hides the
      incident queue panel when an incident card is clicked */
      $('.incident-queue-trigger').click();
    }
  },

  loadQueue(method, subDestinationUrlParams) {
    this.set('loadingData', true);
    let me = this;

    this.get('request').streamRequest({
      method,
      modelName: 'incident',
      query: {
        filter: [{ field: 'statusSort', values: incidentStatusIds }],
        sort: [{ field: 'riskScore', descending: true }],
        subDestinationUrlParams
      },
      onResponse({ data }) {
        me.set('loadingData', false);
        if (data) {
          me.set('incidents', data);
        }
      },
      onError() {
        Logger.error('Error processing stream call for incident queue');
      }
    });
  },

  init() {
    this._super(arguments);

    this.get('eventBus').on('rsa-application-incident-queue-panel-will-toggle', () => {
      this.toggleProperty('isExpanded');
      if (this.get('isExpanded') === true) {
        this.loadQueue('notify', this.get('username'));
      } else {
        this.set('loadingData', false);
        this.set('incidents', null);
      }
    });
  }
});
