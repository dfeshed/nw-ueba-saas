import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import IncidentsCube from 'sa/utils/cube/incidents';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar', 'Integration | Component | rsa respond/landing page/respond index/list view/bulk edit bar', {
  integration: true,

  beforeEach() {
    const incidentCube = IncidentsCube.create({
      array: []
    });

    incidentCube.get('records').pushObjects([
      EmberObject.create({
        riskScore: 1,
        id: 'INC-491',
        name: 'Suspected command and control communication with www.mozilla.com',
        createdBy: 'User X',
        created: '2015-10-10',
        lastUpdated: '2015-10-10',
        statusSort: 0, // Status: New
        prioritySort: 0, // Priority: Low
        alertCount: 10,
        eventCount: 2,
        sources: ['Event Stream Analysis'],
        assignee: {
          id: '1'
        },
        categories: []
      }),
      EmberObject.create({
        riskScore: 1,
        id: 'INC-492',
        name: 'Suspected command and control communication with www.mozilla.com',
        createdBy: 'User X',
        created: '2015-10-10',
        lastUpdated: '2015-10-10',
        statusSort: 1, // Status: Assigned
        prioritySort: 1, // Priority: Medium
        alertCount: 10,
        eventCount: 2,
        sources: ['ECAT', 'Web Threat Detection'],
        assignee: {
          id: '2'
        },
        categories: [{
          name: 'childCategory1',
          parent: 'parentCategory1',
          id: '1'
        }, {
          name: 'childCategory2',
          parent: 'parentCategory1',
          id: '2'
        }]
      }),
      EmberObject.create({
        riskScore: 1,
        id: 'INC-493',
        name: 'Suspected command and control communication with www.mozilla.com',
        createdBy: 'User X',
        created: '2015-10-10',
        lastUpdated: '2015-10-10',
        statusSort: 2, // Status: In-Progress
        prioritySort: 2, // Priority: High
        alertCount: 10,
        eventCount: 2,
        sources: ['Malware Analysis', 'Web Threat Detection'],
        assignee: {
          id: '3'
        },
        categories: []
      })
    ]);

    const users = [
      EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', friendlyName: 'user2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', friendlyName: 'user3', email: 'user3@rsa.com' })
    ];

    this.setProperties({
      incidents: incidentCube.get('records'),
      isBulkEditInProgress: false,
      showBulkEditMessage: false,
      users
    });
  }
});

test('it renders', function(assert) {

  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view/bulk-edit-bar
    users=users
    incidents=incidents
    saveAction=bulkSaveAction
    isBulkEditInProgress=isBulkEditInProgress
    showSuccessMessage=showBulkEditMessage}}`);
  assert.equal(this.$('.bulk-edit-bar').length, 1, 'Main element is present');

});
