import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, findAll, render } from '@ember/test-helpers';
import { patchReducer } from '../../../../tests/helpers/vnext-patch';
import Immutable from 'seamless-immutable';
import {
  getAllEnabledUsers,
  getAllPriorityTypes,
  getAllCategories
} from 'respond-shared/actions/creators/create-incident-creators';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import RSVP from 'rsvp';

const createIncidentModalSelector = '.rsa-application-modal.create-incident-modal';
const addToIncidentModalSelector = '.rsa-application-modal.add-to-incident-modal';

let init, setState;

module('Integration | Component | incident-toolbar', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state = {}) => {
      const fullState = {
        respondShared: {
          createIncident: state
        }
      };
      patchReducer(this, Immutable.from(fullState));
      // initialize all of the required data into redux app state
      const redux = this.owner.lookup('service:redux');
      init = RSVP.allSettled([
        redux.dispatch(getAllPriorityTypes()),
        redux.dispatch(getAllEnabledUsers()),
        redux.dispatch(getAllCategories())
      ]);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  hooks.beforeEach(function() {
    this.set('clearResults', () => {});
  });

  test('The Create Incident and Add to Incident buttons and dropdown are rendered to the DOM', async function(assert) {
    await setState();
    await render(hbs`{{incident-toolbar}}`);
    assert.equal(findAll('.create-incident-button .rsa-form-button').length, 1,
      'The Create Incident button should be found in the DOM');
    assert.equal(findAll('.add-to-incident-button .rsa-form-button').length, 1,
      'The Add to Incident button should be found in the DOM');
    assert.equal(findAll('.incident-dropdown').length, 1, 'The Incidents dropdown should be found in the DOM');

  });

  test('Clicking the Create Incident button opens the create-incident modal', async function(assert) {
    setState();
    await init;
    await render(hbs`{{incident-toolbar isDisabled=false clearResults=clearResults}}`);
    assert.equal(findAll('.create-incident-modal').length, 0, 'There is no modal displayed');
    await click('.create-incident-button .rsa-form-button');
    assert.equal(findAll(createIncidentModalSelector).length, 1, 'The create-incident modal is displayed');
  });

  test('Clicking cancel in the modal closes the create-incident modal', async function(assert) {
    await setState();
    await render(hbs`{{incident-toolbar isDisabled=false clearResults=clearResults}}`);
    assert.equal(findAll('.create-incident-modal').length, 0, 'There is no modal displayed');
    await click('.create-incident-button .rsa-form-button');
    assert.equal(findAll(createIncidentModalSelector).length, 1, 'The create-incident modal is displayed');
    await click('.cancel .rsa-form-button');
    assert.equal(findAll(createIncidentModalSelector).length, 0, 'The create-incident modal is gone');
  });

  test('Clicking the Add to Incident button opens the add-to-incident modal', async function(assert) {
    await setState();
    await render(hbs`{{incident-toolbar isDisabled=false clearResults=clearResults}}`);
    assert.equal(findAll('.add-to-incident-modal').length, 0, 'There is no modal displayed');
    await click('.add-to-incident-button .rsa-form-button');
    assert.equal(findAll(addToIncidentModalSelector).length, 1, 'The add-to-incident modal is displayed');
  });

  test('Clicking cancel in the Add to Incident modal closes the modal', async function(assert) {
    await setState();
    await render(hbs`{{incident-toolbar isDisabled=false clearResults=clearResults}}`);
    assert.equal(findAll('.add-to-incident-modal').length, 0, 'There is no modal displayed');
    await click('.add-to-incident-button .rsa-form-button');
    assert.equal(findAll(addToIncidentModalSelector).length, 1, 'The add-to-incident modal is displayed');
    await click('.cancel .rsa-form-button');
    assert.equal(findAll(addToIncidentModalSelector).length, 0, 'The add-to-incident modal is gone');
  });

  test('Clicking the Create Incident drop down list item opens the create-incident modal', async function(assert) {
    await setState();
    await render(hbs`{{incident-toolbar isDisabled=false clearResults=clearResults}}`);
    assert.equal(findAll('.create-incident-modal').length, 0, 'There is no modal displayed');
    await click('.incident-dropdown .rsa-form-button');
    await click('.create-incident-list-item');
    assert.equal(findAll(createIncidentModalSelector).length, 1, 'The create-incident modal is displayed');
    await click('.cancel .rsa-form-button');
  });

  test('Clicking the Add to Incident drop down list item opens the add-to-incident modal', async function(assert) {
    await setState();
    await render(hbs`{{incident-toolbar isDisabled=false clearResults=clearResults}}`);
    assert.equal(findAll('.create-incident-modal').length, 0, 'There is no modal displayed');
    await click('.incident-dropdown .rsa-form-button');
    await click('.add-to-incident-list-item');
    assert.equal(findAll(addToIncidentModalSelector).length, 1, 'The add-to-incident modal is displayed');
    await click('.cancel .rsa-form-button');
  });
});