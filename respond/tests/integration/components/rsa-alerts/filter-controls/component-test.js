import { waitUntil, click, findAll, find, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { run } from '@ember/runloop';
import { setupRenderingTest } from 'ember-qunit';
import Immutable from 'seamless-immutable';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import {
  getAllAlertTypes,
  getAllAlertSources,
  getAllAlertNames
} from 'respond/actions/creators/dictionary-creators';
import { getAllCategories } from 'respond-shared/actions/creators/create-incident-creators';
import RSVP from 'rsvp';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { findElement } from '../../../../helpers/find-element';
import { alertFilterData } from './data';
import waitForReduxStateChange from '../../../../helpers/redux-async-helpers';
import { clickTrigger, selectChoose } from 'ember-power-select/test-support/helpers';

let setState;

module('Integration | Component | Respond Alerts Filters', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    setState = async(state = {}) => {
      const fullState = { respond: { incidents: state } };
      patchReducer(this, Immutable.from(fullState));
      const redux = this.owner.lookup('service:redux');
      // initialize all of the required data into redux app state
      return RSVP.allSettled([
        redux.dispatch(getAllAlertTypes()),
        redux.dispatch(getAllAlertSources()),
        redux.dispatch(getAllAlertNames())
      ]);
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The Alerts Filters component renders to the DOM', async function(assert) {
    assert.expect(1);

    await setState();

    this.set('updateFilter', function() {});
    await render(hbs`{{rsa-alerts/filter-controls updateFilter=(action updateFilter)}}`);
    assert.ok(findAll('.filter-option').length >= 1, 'The Alerts Filters component should be found in the DOM');
  });

  test('All of the alert type filters appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);

    await setState();

    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-alerts/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.alert-type-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 10, 'There should be 10 alert type filter options');
    await click('.filter-option.alert-type-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first-of-type');
  });

  test('All of the alert source filters appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);

    await setState();

    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-alerts/filter-controls updateFilter=(action updateFilter)}}`);
    const selector = '.filter-option.alert-source-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 8, 'There should be 8 alert source filter options');
    await click('.filter-option.alert-source-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first-of-type');
  });

  test('All of the part-of-incident filter options appear as checkboxes, and clicking one dispatches an action', async function(assert) {
    assert.expect(2);

    await setState();

    this.set('updateFilter', function() {
      assert.ok(true);
    });
    await render(hbs`{{rsa-alerts/filter-controls updateFilter=(action updateFilter)}}`);

    const selector = '.filter-option.part-of-incident-filter .rsa-form-checkbox-label';
    assert.equal(findAll(selector).length, 2, 'There should be 2 part-of-incident filter options');
    await click('.filter-option.part-of-incident-filter .rsa-form-checkbox-label input.rsa-form-checkbox:first-of-type');
  });

  test('The severity slider filter appears in the DOM', async function(assert) {
    await setState();

    await render(hbs`{{rsa-alerts/filter-controls}}`);
    const selector = '.filter-option.severity-filter .noUi-tooltip';
    assert.equal(findAll(selector).length, 2, 'The are two tooltips');
    assert.equal(findAll(selector)[0].textContent.trim(), '0', 'The left end slider value should be 0');
    assert.equal(findAll(selector)[1].textContent.trim(), '100', 'The right end slider value should be 100');
  });

  test('The alert name filter appears as a power-select-multiple, and selecting an option dispatches an action', async function(assert) {
    assert.expect(3);

    await setState();

    this.set('updateFilter', function() {
      assert.ok(true);
    });

    await render(hbs`{{rsa-alerts/filter-controls updateFilter=(action updateFilter)}}`);

    const optionSelector = '.ember-power-select-dropdown .ember-power-select-options .ember-power-select-option';
    // lazy rendering of the list means we cannot assert that the full set of alert names are present
    await clickTrigger('.alert-name-filter');
    assert.ok(find('.ember-basic-dropdown-content-wormhole-origin'), 'alert name filter dropdown open');
    assert.ok(findAll(optionSelector).length >= 1, 'There should be at least one alert name filter options');
    await selectChoose('.alert-name-filter', '.ember-power-select-option', 0);
  });

  test('Delete and uncheck the filter after all alerts are removed', async function(assert) {
    patchReducer(this, Immutable.from(alertFilterData));
    const redux = this.owner.lookup('service:redux');

    redux.dispatch(getAllCategories());
    await waitForReduxStateChange(redux, 'respondShared.createIncident.categoryTags');

    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-alerts}}
    `);

    const optionSelector = '.ember-power-select-dropdown .ember-power-select-options .ember-power-select-option';
    await clickTrigger('.alert-name-filter');
    assert.equal(findAll(optionSelector).length, 3, 'There should be 3 alert type filter options to start');
    assert.equal(findElement(optionSelector, 'Nehal alert').length, 1);
    assert.equal(findElement(optionSelector, 'test').length, 1);
    assert.equal(findElement(optionSelector, 'Toran Alert').length, 1);

    const bodySelector = '.rsa-data-table-body-cell';
    const rowSelector = '.rsa-explorer-table .rsa-data-table-body-row';
    const nameSelector = `${rowSelector}:nth-of-type(4) ${bodySelector}:nth-of-type(4) .alert-name`;
    const labelSelector = `${rowSelector}:nth-of-type(4) ${bodySelector}:first-of-type .rsa-form-checkbox-label`;
    const inputSelector = `${labelSelector} input`;
    await waitUntil(() => findAll(rowSelector).length === 4, { timeout: 5000 });
    assert.equal(document.querySelector(nameSelector).textContent.trim(), 'Toran Alert', 'Alert Element selected to delete should have the AlertName `Toran Alert`');

    await click(inputSelector);

    const request = this.owner.lookup('service:request');

    let deleteItemFired = false;
    const { promiseRequest, streamRequest } = request;
    request.promiseRequest = function({ method, modelName }) {
      if (method === 'findAll' && modelName === 'alert-names') {
        return new RSVP.Promise((resolve) => {
          deleteItemFired = true;
          run(null, resolve, { data: [ 'Nehal alert', 'test' ] });
        });
      }
      return promiseRequest.apply(this, arguments);
    };

    let getItemsFired = false;
    request.streamRequest = function({ method, modelName }) {
      if (method === 'stream' && modelName === 'alerts') {
        return new RSVP.Promise((resolve) => {
          getItemsFired = true;
          run(null, resolve, { data: [] });
        });
      }
      return streamRequest.apply(this, arguments);
    };

    await click('.rsa-alerts-toolbar-controls .is-danger button');

    const confirmOKButtonSelector = '.modal-footer-buttons .rsa-form-button';
    assert.equal(document.querySelectorAll('#modalDestination').length, 1);
    assert.equal(document.querySelector('#modalDestination').classList.contains('active'), true, 'the modal was not present for delete');
    assert.equal(findAll(confirmOKButtonSelector)[5].innerHTML.trim(), 'OK', 'OK button is clicked');
    assert.ok(findAll('#modalDestination [test-id=test-warning-title]').length == 1 && findAll('#modalDestination [test-id=test-warnings]').length == 1, 'Confirm modal has warnings');
    await click(findAll(confirmOKButtonSelector)[5]);

    await waitUntil(() => {
      const { respond: { dictionaries: { alertNames } } } = redux.getState();
      return alertNames && alertNames.length === 2;
    });

    await waitUntil(() => deleteItemFired === true).then(() => {
      // unpatch the promiseRequest
      request.promiseRequest = promiseRequest;
    });

    await waitUntil(() => getItemsFired === true).then(() => {
      // unpatch the streamRequest
      request.streamRequest = streamRequest;
    });

    await clickTrigger('.alert-name-filter');
    assert.equal(findAll(optionSelector).length, 2, 'There should be 2 alert type filter options left');
    assert.equal(findElement(optionSelector, 'Nehal alert').length, 1);
    assert.equal(findElement(optionSelector, 'test').length, 1);

    assert.equal(document.querySelectorAll('#modalDestination').length, 1);
    assert.equal(document.querySelector('#modalDestination').classList.contains('active'), false, 'the modal is now inactive');
  });
});
