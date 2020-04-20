import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, findAll, render } from '@ember/test-helpers';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

const createIncidentModalSelector = '.rsa-application-modal.create-incident-modal';
const addToIncidentModalSelector = '.rsa-application-modal.add-to-incident-modal';

module('Integration | Component | Respond Alerts Toolbar Controls', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component is rendered to the DOM', async function(assert) {
    await render(hbs`{{rsa-alerts/toolbar-controls}}`);
    assert.equal(findAll('.rsa-alerts-toolbar-controls').length, 1, 'The Alerts Toolbar Controls component should be found in the DOM');
  });

  test('The Delete button is rendered to the DOM', async function(assert) {
    await render(hbs`{{rsa-alerts/toolbar-controls}}`);
    assert.equal(findAll('.rsa-alerts-toolbar-controls .action-control.delete-button .rsa-form-button').length, 1,
      'The Delete Alerts button should be found in the DOM');
  });

  test('The Delete button is disabled if no alerts are selected', async function(assert) {
    await render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=true}}`);
    assert.equal(findAll('.rsa-alerts-toolbar-controls .action-control.delete-button .rsa-form-button-wrapper.is-disabled').length, 1,
      'The Delete button is disabled by default since no alerts are selected');
  });

  test('The Delete button is enabled if any alert is selected', async function(assert) {
    await render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=false}}`);
    assert.equal(findAll('.rsa-alerts-toolbar-controls .action-control.delete-button .rsa-form-button-wrapper:not(.is-disabled)').length, 1,
      'The Delete button is enabled if it has selections');
  });

  test('The Create Incident and Add to Incident buttons are rendered to the DOM', async function(assert) {
    await render(hbs`{{rsa-alerts/toolbar-controls}}`);
    assert.equal(findAll('.rsa-alerts-toolbar-controls .create-incident-button .rsa-form-button').length, 1,
      'The Create Incident button should be found in the DOM');
    assert.equal(findAll('.rsa-alerts-toolbar-controls .add-to-incident-button .rsa-form-button').length, 1,
      'The Add to Incident button should be found in the DOM');
  });

  test('The Create Incident/Add to Incident buttons are disabled if no alerts are selected', async function(assert) {
    await render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=true}}`);
    assert.equal(findAll('.rsa-alerts-toolbar-controls .create-incident-button .rsa-form-button-wrapper.is-disabled').length, 1,
      'The Create Incident button is disabled by default since no alerts are selected');
    assert.equal(findAll('.rsa-alerts-toolbar-controls .add-to-incident-button .rsa-form-button-wrapper.is-disabled').length, 1,
      'The Add to Incident button is disabled by default since no alerts are selected');
  });

  test('The Create/Add to Incident buttons are enabled if any alert is selected', async function(assert) {
    await render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=false}}`);
    assert.equal(findAll('.rsa-alerts-toolbar-controls .create-incident-button .rsa-form-button-wrapper:not(.is-disabled)').length, 1,
      'The Create Incident button is enabled if it has selections');
    assert.equal(findAll('.rsa-alerts-toolbar-controls .add-to-incident-button .rsa-form-button-wrapper:not(.is-disabled)').length, 1,
      'The Add to Incident button is enabled if it has selections');
  });

  test('Clicking the Create Incident button opens the create-incident modal', async function(assert) {
    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-alerts/toolbar-controls
        hasNoSelections=false
      }}
    `);
    assert.equal(findAll('.create-incident-modal').length, 0, 'There is no modal displayed');
    await click('.create-incident-button .rsa-form-button');
    assert.equal(findAll(createIncidentModalSelector).length, 1, 'The create-incident modal is displayed');
  });

  test('Clicking cancel in the modal closes the create-incident modal', async function(assert) {
    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-alerts/toolbar-controls
        hasNoSelections=false
      }}
    `);
    assert.equal(findAll('.create-incident-modal').length, 0, 'There is no modal displayed');
    await click('.create-incident-button .rsa-form-button');
    assert.equal(findAll(createIncidentModalSelector).length, 1, 'The create-incident modal is displayed');
    await click('.cancel .rsa-form-button');
    assert.equal(findAll(createIncidentModalSelector).length, 0, 'The create-incident modal is gone');
  });

  test('Clicking the Add to Incident button opens the add-to-incident modal', async function(assert) {
    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-alerts/toolbar-controls
        hasNoSelections=false
      }}
    `);
    assert.equal(findAll('.add-to-incident-modal').length, 0, 'There is no modal displayed');
    await click('.add-to-incident-button .rsa-form-button');
    assert.equal(findAll(addToIncidentModalSelector).length, 1, 'The add-to-incident modal is displayed');
  });

  test('Clicking cancel in the Add to Incident modal closes the modal', async function(assert) {
    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-alerts/toolbar-controls
        hasNoSelections=false
      }}
    `);
    assert.equal(findAll('.add-to-incident-modal').length, 0, 'There is no modal displayed');
    await click('.add-to-incident-button .rsa-form-button');
    assert.equal(findAll(addToIncidentModalSelector).length, 1, 'The add-to-incident modal is displayed');
    await click('.cancel .rsa-form-button');
    assert.equal(findAll(addToIncidentModalSelector).length, 0, 'The add-to-incident modal is gone');
  });

});