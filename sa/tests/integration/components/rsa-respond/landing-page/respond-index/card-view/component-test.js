import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import wait from 'ember-test-helpers/wait';
import IncidentsCube from 'sa/utils/cube/incidents';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-respond/landing-page/respond-index/card-view', 'Integration | Component | rsa respond/landing page/respond index/card view', {
  integration: true,

  beforeEach() {
    let newCube = IncidentsCube.create({
      array: []
    });
    let inProgressCube = IncidentsCube.create({
      array: []
    });

    newCube.get('records').pushObjects([EmberObject.create({
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
      }
    }),
      EmberObject.create({
        riskScore: 1,
        id: 'INC-492',
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
        }
      })]);

    inProgressCube.get('records').pushObjects([EmberObject.create({
      riskScore: 1,
      id: 'INC-493',
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
      }
    }),
      EmberObject.create({
        riskScore: 1,
        id: 'INC-494',
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
        }
      })]);

    this.set('model', {
      newIncidents: newCube,
      inProgressIncidents: inProgressCube,
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

  this.render(hbs`{{rsa-respond/landing-page/respond-index/card-view model=model}}`);

  assert.equal(this.$('.rsa-respond-card__status-header').length, 2, 'There are 2 rsa-respond-card__status-header elements');
  assert.equal(this.$('.rsa-incident-carousel').length, 2, '2 carousel elements are present');
  let that = this;
  return wait().then(function() {
    assert.equal(that.$('.rsa-incident-tile').length, 6, 'Correct number of Tile components are present');
  });

});