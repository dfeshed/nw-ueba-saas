import Ember from 'ember';
import { viewType } from 'sa/protected/respond/index/route';

const {
  Component
} = Ember;

export default Component.extend({
  tagName: 'article',
  classNames: 'rsa-respond-card',

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
  ]
});
