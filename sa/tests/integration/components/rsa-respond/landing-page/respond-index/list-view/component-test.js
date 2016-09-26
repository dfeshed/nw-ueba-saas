import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import wait from 'ember-test-helpers/wait';
import IncidentsCube from 'sa/utils/cube/incidents';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-respond/landing-page/respond-index/list-view', 'Integration | Component | rsa respond/landing page/respond index/list view', {
  integration: true,

  beforeEach() {
    let allCube = IncidentsCube.create({
      array: []
    });

    allCube.get('records').pushObjects([EmberObject.create({
      riskScore: 1,
      id: 'INC-491',
      name: 'Suspected command and control communication with www.mozilla.com',
      createdBy: 'User X',
      created: '2015-10-10',
      lastUpdated: '2015-10-10',
      statusSort: 0,
      prioritySort: 0,
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
        statusSort: 1,
        prioritySort: 1,
        alertCount: 10,
        eventCount: 2,
        sources: ['Event Stream Analysis'],
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
      })]);

    this.set('model', {
      allIncidents: allCube,
      users: [
        EmberObject.create({ id: '1', firstName: 'User 1', lastName: 'LastName 1', friendlyName: 'user1', email: 'user1@rsa.com' }),
        EmberObject.create({ id: '2', firstName: 'User 2', lastName: 'LastName 2', friendlyName: 'user2', email: 'user2@rsa.com' }),
        EmberObject.create({ id: '3', firstName: 'User 3', lastName: 'LastName 3', friendlyName: 'user3', email: 'user3@rsa.com' })
      ],
      categoryTags: [
        EmberObject.create({
          id: '1',
          parent: 'parentCategory1',
          name: 'childCategory1'
        }),
        EmberObject.create({
          id: '2',
          parent: 'parentCategory1',
          name: 'childCategory2'
        })
      ]
    });
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index/list-view model=model}}`);

  assert.equal(this.$('.rsa-data-table').length, 1, 'Data table is present');

  let that = this;
  return wait().then(function() {
    assert.equal(that.$('.rsa-data-table .rsa-data-table-body .rsa-data-table-body-rows .rsa-data-table-body-row').length, 2, 'All incidents are displayed');
  });
});