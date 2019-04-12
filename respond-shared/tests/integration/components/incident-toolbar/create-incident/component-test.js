import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, findAll, render } from '@ember/test-helpers';
import { selectChoose } from 'ember-power-select/test-support/helpers';

const priorityTypes = ['LOW', 'MEDIUM', 'HIGH', 'CRITICAL'];

const enabledUsers = [{
  'id': 'local',
  'name': 'local',
  'email': null,
  'description': 'local@test.com',
  'type': null,
  'accountId': null,
  'disabled': false
}];

const groupedCategories = [{
  'id': '58c690184d5aff1637200187',
  'parent': 'Environmental',
  'name': 'Deterioration'
},
{
  'id': '58c690184d5aff1637200189',
  'parent': 'Environmental',
  'name': 'EMI'
}];

module('Integration | Component | incident-toolbar/create-incident', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.set('priorityTypes', priorityTypes);
    this.set('enabledUsers', enabledUsers);
    this.set('groupedCategories', groupedCategories);
  });

  test('Apply button is disabled when there is no name', async function(assert) {
    await render(hbs`{{incident-toolbar/create-incident}}`);
    assert.equal(findAll('.apply.is-disabled').length, 1, 'The APPLY button is disabled when there is no incidentName');
  });

  test('Apply button is enabled when there is an incident name', async function(assert) {
    await render(hbs`{{incident-toolbar/create-incident
      name="Suspected C&C"
      priorityTypes=priorityTypes
      enabledUsers=enabledUsers
      groupedCategories=groupedCategories
      enabledUsers=enabledUsers}}`);
    assert.equal(findAll('.apply:not(.is-disabled)').length, 1, 'The APPLY button is not disabled when there is an incidentName');
  });

  test('Clicking Apply will execute the create incident and show a success flash message', async function(assert) {
    this.set('createIncident', (incidentDetails) => {
      assert.deepEqual(incidentDetails, {
        assignee: null,
        categories: null,
        name: 'Suspected C&C',
        priority: 'LOW'
      });
    });

    await render(hbs`{{incident-toolbar/create-incident
      name="Suspected C&C"
      priorityTypes=priorityTypes
      enabledUsers=enabledUsers
      groupedCategories=groupedCategories
      createIncident=createIncident}}`);
    await click('.apply .rsa-form-button');
  });

  test('Manually selected Priority, Assignee and Categories are reflected in request payload', async function(assert) {
    this.set('createIncident', (incidentDetails) => {
      assert.deepEqual(incidentDetails, {
        name: 'Suspected C&C',
        priority: 'CRITICAL',
        assignee: {
          accountId: null,
          description: 'local@test.com',
          disabled: false,
          email: null,
          id: 'local',
          name: 'local',
          type: null
        },
        categories: [{
          id: '58c690184d5aff1637200189',
          name: 'EMI',
          parent: 'Environmental'
        }]
      });
    });

    await render(hbs`{{incident-toolbar/create-incident
      name="Suspected C&C"
      priorityTypes=priorityTypes
      enabledUsers=enabledUsers
      groupedCategories=groupedCategories
      createIncident=createIncident}}`);

    await selectChoose('.create-incident-priority', 'Critical');

    await selectChoose('.create-incident-assignee', 'local');

    await selectChoose('.create-incident-categories', 'Environmental: EMI');

    await click('.apply .rsa-form-button');
  });
});
