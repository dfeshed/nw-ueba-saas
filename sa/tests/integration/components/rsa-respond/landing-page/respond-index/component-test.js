import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Ember from 'ember';
import IncidentsCube from 'sa/utils/cube/incidents';

const { Object: EmberObject, Logger } = Ember;

moduleForComponent('rsa-respond-index', 'Integration | Component | rsa respond/landing page/respond index', {
  integration: true,

  beforeEach() {

    this.set('i18n', this.container.lookup('service:i18n'));

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
      id: 'INC-1001',
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
      id: 'INC-1002',
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
      id: 'INC-1003',
      name: 'Suspected command and control communication with www.mozilla.com',
      createdBy: 'User X',
      created: '2015-10-10',
      lastUpdated: '2015-10-10',
      statusSort: 0,
      prioritySort: 1,  // priority MEDIUM
      alertCount: 10,
      eventCount: 2,
      sources: ['Event Stream Analysis'],
      assignee: {
        id: '1'
      }
    }),
    EmberObject.create({
      riskScore: 1,
      id: 'INC-1004',
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

test('Changing filter affects total incident count indicator', function(assert) {

  this.render(hbs`{{rsa-respond/landing-page/respond-index model=model i18n=i18n}}`);

  // Change to list view which will display an incident count
  this.$('.rsa-respond-index-header__list-btn').trigger('click');


  let countLabelValue = this.$('.rsa-respond-index-header__label').text();
  Logger.log(`countLabelValue: ${ countLabelValue }`);
  assert.ok(countLabelValue.indexOf('4') != -1, 'Check initial incident count');

  // Filter incidents by Medium priority where Medium priority equates to prioritySort: 1 inside incident object
  this.$('.rsa-respond-list__filter-panel__priority .priority-1 input:first').prop('checked', true).trigger('change');

  let filteredCountLabelValue = this.$('.rsa-respond-index-header__label').text();
  assert.ok(filteredCountLabelValue.indexOf('Showing 1 of 4') != -1, 'Check filtered incident count');

});