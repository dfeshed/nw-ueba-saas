import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import IncidentsCube from 'sa/utils/cube/incidents';

const { Object: EmberObject } = Ember;

moduleForComponent('rsa-respond-index', 'Integration | Component | rsa respond/landing page/respond index', {
  integration: true,

  beforeEach() {

    let newCube = IncidentsCube.create({
      array: []
    });
    let inProgressCube = IncidentsCube.create({
      array: []
    });
    let allCube = IncidentsCube.create({
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

    allCube.get('records').pushObjects(newCube.get('records'));
    allCube.get('records').pushObjects(inProgressCube.get('records'));

    this.set('model', {
      newIncidents: newCube,
      inProgressIncidents: inProgressCube,
      allIncidents: allCube
    });
  }
});

test('it renders', function(assert) {
  this.render(hbs`{{rsa-respond/landing-page/respond-index model=model}}`);

  assert.equal(this.$('.rsa-respond-index-header').length, 1, 'Testing to see if a rsa-respond-index-header element exists.');
  assert.equal(this.$('.rsa-respond-card, .rsa-respond-list').length, 1, 'Testing to see if rsa-respond-card or rsa-respond-list exists.');
});
