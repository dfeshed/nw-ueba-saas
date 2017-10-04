import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../../helpers/engine-resolver';
import waitForReduxStateChange from '../../../../helpers/redux-async-helpers';
import { getAlerts, getSelectedAlerts } from 'respond/selectors/alerts';
import { getItems, toggleItemSelected } from 'respond/actions/creators/alert-creators';
import wait from 'ember-test-helpers/wait';
import $ from 'jquery';

let redux;

const createIncidentModalSelector = '.rsa-application-modal-content.create-incident-modal';
const addToIncidentModalSelector = '.rsa-application-modal-content.add-to-incident-modal';

moduleForComponent('rsa-alerts/toolbar-controls', 'Integration | Component | Respond Alerts Toolbar Controls', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    this.inject.service('redux');
    redux = this.get('redux');
  }
});

test('The component is rendered to the DOM', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls}}`);
  assert.equal(this.$('.rsa-alerts-toolbar-controls').length, 1, 'The Alerts Toolbar Controls component should be found in the DOM');
});

test('The Delete button is rendered to the DOM', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls}}`);
  assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.delete-button .rsa-form-button').length, 1,
    'The Delete Alerts button should be found in the DOM');
});

test('The Delete button is disabled if no alerts are selected', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=true }}`);
  assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.delete-button .rsa-form-button-wrapper.is-disabled').length, 1,
    'The Delete button is disabled by default since no alerts are selected');
});

test('The Delete button is enabled if any alert is selected', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=false }}`);
  assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.delete-button .rsa-form-button-wrapper:not(.is-disabled)').length, 1,
    'The Delete button is enabled if it has selections');
});

test('The Create Incident and Add to Incident buttons are rendered to the DOM', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls}}`);
  assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.create-incident-button .rsa-form-button').length, 1,
    'The Create Incident button should be found in the DOM');
  assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.add-to-incident-button .rsa-form-button').length, 1,
    'The Add to Incident button should be found in the DOM');
});

test('The Create Incident/Add to Incident buttons are disabled if no alerts are selected', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=true}}`);
  assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.create-incident-button .rsa-form-button-wrapper.is-disabled').length, 1,
    'The Create Incident button is disabled by default since no alerts are selected');
  assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.add-to-incident-button .rsa-form-button-wrapper.is-disabled').length, 1,
    'The Add to Incident button is disabled by default since no alerts are selected');
});

test('The Create/Add to Incident buttons are enabled if any alert is selected', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=false }}`);
  assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.create-incident-button .rsa-form-button-wrapper:not(.is-disabled)').length, 1,
    'The Create Incident button is enabled if it has selections');
  assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.add-to-incident-button .rsa-form-button-wrapper:not(.is-disabled)').length, 1,
    'The Add to Incident button is enabled if it has selections');
});

test('The Create/Add to Incident buttons are disabled if any of the selected alerts are part of an incident already', function(assert) {
  redux.dispatch(getItems());  // fetch the alerts and get them into state
  const fetch = waitForReduxStateChange(redux, 'respond.alerts.items');
  return fetch.then(() => {
    const [firstAlert] = getAlerts(redux.getState()); // grab the first alert and check that it's part of an incident
    assert.ok(firstAlert.partOfIncident, 'The first alert is already part of an incident');
    const select = waitForReduxStateChange(redux, ('respond.alerts.itemsSelected'));
    // select the first alert via it's ID
    redux.dispatch(toggleItemSelected(firstAlert.id));
    return select.then(() => {
      const state = redux.getState();
      assert.ok(getSelectedAlerts(state).length >= 1, 'There are now selected alerts');
      this.render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=false }}`);
      assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.create-incident-button .rsa-form-button-wrapper.is-disabled').length, 1,
        'The Create Incident button is disabled if hasSelectedAlertsBelongingToIncidents is true');
      assert.equal(this.$('.rsa-alerts-toolbar-controls .action-control.add-to-incident-button .rsa-form-button-wrapper.is-disabled').length, 1,
        'The Add to Incident button is disabled if hasSelectedAlertsBelongingToIncidents is true');
    });
  });
});

test('Clicking the Create Incident button opens the create-incident modal', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=false }}`);
  assert.equal($('.create-incident-modal').length, 0, 'There is no modal displayed');
  this.$('.create-incident-button .rsa-form-button').click();
  return wait().then(() => {
    assert.equal($(createIncidentModalSelector).length, 1, 'The create-incident modal is displayed');
  });
});

test('Clicking cancel in the modal closes the create-incident modal', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=false }}`);
  assert.equal($('.create-incident-modal').length, 0, 'There is no modal displayed');
  this.$('.create-incident-button .rsa-form-button').click();
  return wait().then(() => {
    assert.equal($(createIncidentModalSelector).length, 1, 'The create-incident modal is displayed');
    $('.cancel .rsa-form-button').click();
    return wait().then(() => {
      assert.equal($(createIncidentModalSelector).length, 0, 'The create-incident modal is gone');
    });
  });
});

test('Clicking the Add to Incident button opens the add-to-incident modal', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=false }}`);
  assert.equal($('.add-to-incident-modal').length, 0, 'There is no modal displayed');
  this.$('.add-to-incident-button .rsa-form-button').click();
  return wait().then(() => {
    assert.equal($(addToIncidentModalSelector).length, 1, 'The add-to-incident modal is displayed');
  });
});

test('Clicking cancel in the Add to Incident modal closes the modal', function(assert) {
  this.render(hbs`{{rsa-alerts/toolbar-controls hasNoSelections=false }}`);
  assert.equal($('.add-to-incident-modal').length, 0, 'There is no modal displayed');
  this.$('.add-to-incident-button .rsa-form-button').click();
  return wait().then(() => {
    assert.equal($(addToIncidentModalSelector).length, 1, 'The add-to-incident modal is displayed');
    $('.cancel .rsa-form-button').click();
    return wait().then(() => {
      assert.equal($(addToIncidentModalSelector).length, 0, 'The add-to-incident modal is gone');
    });
  });
});
