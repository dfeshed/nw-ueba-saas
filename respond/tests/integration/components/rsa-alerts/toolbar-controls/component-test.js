import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, fillIn, find, findAll, render, settled } from '@ember/test-helpers';
import RSVP from 'rsvp';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import Immutable from 'seamless-immutable';
import {
  getAllPriorityTypes,
  getAllEnabledUsers,
  getAllCategories } from 'respond/actions/creators/dictionary-creators';
import { patchSocket, throwSocket } from '../../../../helpers/patch-socket';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { patchFlash } from '../../../../helpers/patch-flash';
import { selectChoose } from 'ember-power-select/test-support/helpers';

let init, setState;

const initialState = {
  incidentSearchText: null,
  incidentSearchSortBy: 'created',
  incidentSearchSortIsDescending: true,
  incidentSearchStatus: null,
  incidentSearchResults: [],
  selectedIncident: null,
  stopSearchStream: null,
  isAddAlertsInProgress: false
};

const createIncidentModalSelector = '.rsa-application-modal.create-incident-modal';
const addToIncidentModalSelector = '.rsa-application-modal.add-to-incident-modal';

const exampleIncidentSearchResults = Immutable.from([
  { id: 'INC-123', name: 'Test Incident 123', assignee: { id: 'meiskm' }, created: 150671337600 },
  { id: 'INC-321', name: 'Test Incident 321', assignee: { id: 'meiskm' }, created: 150671337600 }
]);

module('Integration | Component | Respond Alerts Toolbar Controls', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    setState = (state = {}) => {
      const fullState = {
        respond: {
          alerts: {
            items: [],
            itemsSelected: [],
            ...state
          },
          alertIncidentAssociation: state
        }
      };
      patchReducer(this, Immutable.from(fullState));
    };
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

  test('Clicking Apply button of create incident modal will execute the create incident and show a success flash message', async function(assert) {
    assert.expect(5);
    const done = assert.async();
    setState();
    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-alerts/toolbar-controls
        hasNoSelections=false
      }}
    `);
    await click('.create-incident-button .rsa-form-button');
    const $input = find('.create-incident-form input');
    await fillIn($input, 'INC-123');
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.incidents.actions.actionMessages.incidentCreated', { incidentId: 'INC-24' });
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'createRecord');
      assert.equal(modelName, 'incidents');
      assert.deepEqual(query, {
        data: {
          associated: [],
          entity: {
            name: 'INC-123',
            priority: 'LOW',
            assignee: null,
            categories: null
          }
        }
      });
    });
    await click('.apply .rsa-form-button');
  });

  test('Manually selected Priority, Assignee and Categories of create incident modal are reflected in request payload', async function(assert) {
    assert.expect(3);
    setState();
    const redux = this.owner.lookup('service:redux');
    init = RSVP.allSettled([
      redux.dispatch(getAllPriorityTypes()),
      redux.dispatch(getAllEnabledUsers()),
      redux.dispatch(getAllCategories())
    ]);
    await init;
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'createRecord');
      assert.equal(modelName, 'incidents');
      assert.deepEqual(query, {
        data: {
          associated: [],
          entity: {
            name: 'INC-123',
            priority: 'CRITICAL',
            assignee: {
              accountId: null,
              description: 'person3@test.com',
              email: null,
              id: '4',
              isInactive: false,
              name: 'Sim Boyd',
              type: null
            },
            categories: [{
              id: '58c690184d5aff1637200188',
              name: 'Earthquake',
              parent: 'Environmental'
            }]
          }
        }
      });
    });

    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-alerts/toolbar-controls hasNoSelections=false}}
    `);

    await click('.create-incident-button .rsa-form-button');

    const $input = find('.create-incident-form input');

    await fillIn($input, 'INC-123');

    await selectChoose('.create-incident-priority', 'Critical');

    await selectChoose('.create-incident-assignee', 'Sim Boyd');

    await selectChoose('.create-incident-categories', 'Environmental: Earthquake');

    await click('.apply .rsa-form-button');
  });

  test('Clicking on a the Apply button of create incident modal shows a failure flash message when the request fail', async function(assert) {
    assert.expect(2);
    setState();
    await init;
    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-alerts/toolbar-controls hasNoSelections=false}}
    `);

    await click('.create-incident-button .rsa-form-button');

    const $input = find('.create-incident-form input');

    await fillIn($input, 'INC-123');

    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.incidents.actions.actionMessages.incidentCreationFailed');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
    });
    await click('.apply .rsa-form-button');
  });

  test('Clicking on a the Apply button of add to incident modal shows a success flash message when the request is successful', async function(assert) {
    assert.expect(4);
    const done = assert.async();

    patchSocket((method, modelName) => {
      assert.equal(method, 'updateRecord');
      assert.equal(modelName, 'alerts-associated');
    });
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.incidents.actions.actionMessages.addAlertToIncidentSucceeded', {
        incidentId: 'INC-123'
      });
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    setState({
      ...initialState,
      incidentSearchResults: exampleIncidentSearchResults,
      selectedIncident: exampleIncidentSearchResults[0]
    });
    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-alerts/toolbar-controls hasNoSelections=false}}
    `);
    await click('.add-to-incident-button .rsa-form-button');
    await click('.apply button');
  });

  test('Clicking on a the Apply button of add to incident modal shows a failure flash message when the request fails', async function(assert) {
    assert.expect(2);
    const done = throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('respond.incidents.actions.actionMessages.addAlertToIncidentFailed');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
      done();
    });
    setState({
      ...initialState,
      incidentSearchResults: exampleIncidentSearchResults,
      selectedIncident: exampleIncidentSearchResults[0]
    });
    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-alerts/toolbar-controls hasNoSelections=false}}
    `);
    await click('.add-to-incident-button .rsa-form-button');
    await click('.apply button');
    return settled();
  });
});
