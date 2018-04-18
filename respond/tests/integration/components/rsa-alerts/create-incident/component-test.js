import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { click, findAll, render } from '@ember/test-helpers';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import RSVP from 'rsvp';
import {
  getAllPriorityTypes,
  getAllEnabledUsers,
  getAllCategories } from 'respond/actions/creators/dictionary-creators';
import Immutable from 'seamless-immutable';
import { patchSocket, throwSocket } from '../../../../helpers/patch-socket';
import { patchFlash } from '../../../../helpers/patch-flash';
import { selectChoose, clickTrigger } from 'ember-power-select/test-support/helpers';

let init, setState;

module('Integration | Component | Respond Alerts Create Incident', function(hooks) {
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
          }
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

  test('The component appears in the DOM', async function(assert) {
    setState();
    await init;
    await render(hbs`{{rsa-alerts/create-incident}}`);
    assert.equal(findAll('.rsa-create-incident').length, 1, 'The component appears in the DOM');
  });

  test('Apply button is disabled when there is no name', async function(assert) {
    setState();
    await init;
    await render(hbs`{{rsa-alerts/create-incident}}`);
    assert.equal(findAll('.apply.is-disabled').length, 1, 'The APPLY button is disabled when there is no incidentName');
  });

  test('Apply button is enabled when there is an incident name', async function(assert) {
    setState();
    await init;
    await render(hbs`{{rsa-alerts/create-incident name="Suspected C&C"}}`);
    assert.equal(findAll('.apply:not(.is-disabled)').length, 1, 'The APPLY button is not disabled when there is an incidentName');
  });

  test('Clicking Apply will execute the create incident and show a success flash message', async function(assert) {
    assert.expect(5);
    const done = assert.async();
    setState();
    await init;
    await render(hbs`{{rsa-alerts/create-incident name="Suspected C&C"}}`);
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
            name: 'Suspected C&C',
            priority: 'LOW',
            assignee: null,
            categories: null
          }
        }
      });
    });
    await click('.apply .rsa-form-button');
  });

  test('Manually selected Priority, Assignee and Categories are reflected in request payload', async function(assert) {
    assert.expect(3);
    setState();
    await init;
    patchSocket((method, modelName, query) => {
      assert.equal(method, 'createRecord');
      assert.equal(modelName, 'incidents');
      assert.deepEqual(query, {
        data: {
          associated: [],
          entity: {
            name: 'Suspected C&C',
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
    await render(hbs`{{rsa-alerts/create-incident name="Suspected C&C"}}`);

    clickTrigger('.create-incident-priority');
    selectChoose('.create-incident-priority', 'Critical');

    clickTrigger('.create-incident-assignee');
    selectChoose('.create-incident-assignee', 'Sim Boyd');

    clickTrigger('.create-incident-categories');
    selectChoose('.create-incident-categories', 'Environmental: Earthquake');

    await click('.apply .rsa-form-button');
  });

  test('An error will show a failed flash message', async function(assert) {
    assert.expect(2);
    setState();
    await init;
    await render(hbs`{{rsa-alerts/create-incident name="Suspected C&C"}}`);
    throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.incidents.actions.actionMessages.incidentCreationFailed');
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedMessage);
    });
    await click('.apply .rsa-form-button');
  });
});


