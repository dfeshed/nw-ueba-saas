import Ember from 'ember';
import IncidentHelper from 'sa/incident/helpers';
import { viewType } from 'sa/protected/respond/index/route';

const {

  Component,
  inject: {
    service
  },
  computed: {
    equal
  }
} = Ember;

export default Component.extend({
  model: null,

  respondMode: service(),

  isCardMode: equal('respondMode.selected', 'card'),

  // default sorted field for list view
  currentSort: 'riskScore',

  // properties passed to sort-options component
  newIncCardView: viewType.NEW_INC_CARD_VIEW,
  inProgCardView: viewType.IN_PROG_INC_CARD_VIEW,

  // default sort option for New Incidents section of card view
  newIncSortOrder: 'riskScore',

  // sort options for New Incidents section of card view
  newIncSortOrderList: [
    'alertCount',
    'assigneeFirstLastName',
    'dateCreated',
    'id',
    'priority',
    'riskScore'
  ],

  // default sorted field for In Progress Incidents section of card view
  inProgSortOrder: 'lastUpdated',

  // sort options for In Progress Incidents section of card view
  inProgSortOrderList: [
    'alertCount',
    'dateCreated',
    'lastUpdated',
    'id',
    'priority',
    'riskScore'
  ],

  // Config for the data table for the incidents list view landing page
  incidentListConfig: [
    {
      field: 'riskScore',
      title: 'incident.list.riskScore',
      dataType: 'custom',
      width: '7%',
      class: 'rsa-respond-list-riskscore',
      componentClass: 'rsa-content-badge-score',
      isDescending: true
    },
    {
      field: 'id',
      title: 'incident.list.id',
      class: 'incident-id',
      width: '5%',
      dataType: 'text'
    },
    {
      field: 'name',
      title: 'incident.list.name',
      width: '35%',
      class: 'rsa-respond-list-name',
      dataType: 'text'
    },
    {
      field: 'created',
      title: 'incident.list.createdDate',
      width: '10%',
      class: 'rsa-respond-list-created',
      dataType: 'date-time',
      componentClass: 'rsa-content-datetime'
    },
    {
      field: 'assigneeFirstLastName',
      title: 'incident.list.assignee',
      width: '10%',
      class: 'rsa-respond-list-assignee',
      dataType: 'text'
    },
    {
      field: 'statusSort',
      title: 'incident.list.status',
      width: '7%',
      class: 'rsa-respond-list-status',
      dataType: 'text'
    },
    {
      field: 'alertCount',
      title: 'incident.list.alertCount',
      width: '5%',
      class: 'rsa-respond-list-alertCount',
      dataType: 'text'
    },
    {
      field: 'sources',
      title: 'incident.list.sources',
      width: '10%',
      class: 'rsa-respond-list-sources',
      dataType: 'text'
    },
    {
      field: 'eventCount',
      title: 'incident.fields.events',
      width: '10%',
      class: 'rsa-respond-list-events',
      dataType: 'text'
    }
  ],

  /**
   * @name badgeStyle
   * @description define the badge style based on the incident risk score
   * @public
   */
  badgeStyle(riskScore) {
    return IncidentHelper.riskScoreToBadgeLevel(riskScore);
  },

  /**
   * @name sourceShortName
   * @description returns the source's defined short-name
   * @public
   */
  sourceShortName(source) {
    return IncidentHelper.sourceShortName(source);
  },

  actions: {
    // sets the current sorted column field name and the sort direction
    // and calls the sortAction in the route to do the actual sort for list view
    sortListView(column, direction) {
      column.set('isDescending', (direction === 'desc'));
      this.set('currentSort', column.field);
      this.sendAction('sortAction', column.field, direction, viewType.LIST_VIEW);
    }

  }
});
