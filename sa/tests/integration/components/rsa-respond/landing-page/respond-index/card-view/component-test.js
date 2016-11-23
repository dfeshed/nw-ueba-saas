import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import wait from 'ember-test-helpers/wait';
import IncidentsCube from 'sa/utils/cube/incidents';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-respond/landing-page/respond-index/card-view', 'Integration | Component | rsa respond/landing page/respond index/card view', {
  integration: true,

  beforeEach() {
    const newCube = IncidentsCube.create({
      array: []
    });
    const inProgressCube = IncidentsCube.create({
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

    inProgressCube.get('records').pushObjects([
      EmberObject.create({
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
      })
    ]);

    const users = [
      EmberObject.create({ id: '1', name: 'User 1', email: 'user1@rsa.com' }),
      EmberObject.create({ id: '2', name: 'User 2', email: 'user2@rsa.com' }),
      EmberObject.create({ id: '3', name: 'User 3', email: 'user3@rsa.com' })
    ];

    newCube.users = users;
    inProgressCube.users = users;

    this.set('model', {
      newIncidents: newCube,
      inProgressIncidents: inProgressCube
    });
  }
});

test('it renders', function(assert) {

  this.render(hbs`{{rsa-respond/landing-page/respond-index/card-view model=model}}`);

  assert.equal(this.$('.rsa-respond-card__status-header').length, 2, 'There are 2 rsa-respond-card__status-header elements');
  assert.equal(this.$('.rsa-incident-carousel').length, 2, '2 carousel elements are present');
  const that = this;
  return wait().then(function() {
    assert.equal(that.$('.rsa-incident-tile').length, 6, 'Correct number of Tile components are present');
  });

});
