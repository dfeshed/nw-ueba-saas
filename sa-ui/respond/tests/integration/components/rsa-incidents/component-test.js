import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import { settled, waitUntil, click, findAll, find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import waitForReduxStateChange from '../../../helpers/redux-async-helpers';
import { throwSocket, patchSocket } from '../../../helpers/patch-socket';
import { patchFlash } from '../../../helpers/patch-flash';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { initializeIncidents } from 'respond/actions/creators/incidents-creators';

let i18n, redux, setState;

module('Integration | Component | Respond Incidents', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    localStorage.clear();
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    setState = () => {
      redux = this.owner.lookup('service:redux');
      redux.dispatch(initializeIncidents());
    };
    i18n = this.owner.lookup('service:i18n');
  });

  const selectors = {
    explorer: '.rsa-explorer',
    filterPanelToggleButton: '.close-filters button',
    tableRow: '.rsa-explorer-table .rsa-data-table-body-row',
    tableCell: '.rsa-data-table-body-cell',
    customDateToggle: '.explorer-filters .created-filter .x-toggle-component input',
    customDateInput: '.explorer-filters .created-filter .rsa-form-input',
    priorityFilters: '.explorer-filters .priority-filter',
    resetFiltersButton: '.explorer-filters footer button',
    closeInspectorButton: '.close-inspector button',
    firstRowSelectCheckbox: '.rsa-explorer-table .rsa-data-table-body-cell:first-of-type .rsa-form-checkbox-wrapper',
    selectAllCheckbox: '.rsa-explorer-table .rsa-data-table-header-cell:first-of-type .rsa-form-checkbox-wrapper',
    createdColumnSortButton: '.rsa-explorer-table .sort-indicator:first-of-type',
    explorerTableErrorMessage: '.rsa-explorer-table .items-error',
    deleteButton: '.rsa-explorer .rsa-explorer-toolbar .is-danger button',
    confirmationOkButton: '.modal-footer-buttons .is-primary button',
    noResultsMessage: '.rsa-explorer-table .no-results-message .message'
  };

  test('The rsa-incidents component renders to the DOM', async function(assert) {
    const done = assert.async();
    setState();
    await render(hbs`{{rsa-incidents}}`);
    const selector = '.rsa-incidents';
    await waitUntil(() => findAll(selector).length === 1, { timeout: 8000 });
    assert.equal(findAll(selector).length, 1, 'The rsa-incidents component should be found in the DOM');
    await settled().then(() => done());
  });

  test('each explorer column is properly translated', async function(assert) {
    const done = assert.async();
    setState();
    await render(hbs`{{rsa-incidents}}`);
    const selector = '.header-title';
    await waitUntil(() => findAll(selector).length === 8, { timeout: 8000 });
    assert.equal(findAll(selector)[0].textContent.trim(), 'Created');
    assert.equal(findAll(selector)[1].textContent.trim(), 'Priority');
    assert.equal(findAll(selector)[2].textContent.trim(), 'Risk Score');
    assert.equal(findAll(selector)[3].textContent.trim(), 'ID');
    assert.equal(findAll(selector)[4].textContent.trim(), 'Name');
    assert.equal(findAll(selector)[5].textContent.trim(), 'Status');
    assert.equal(findAll(selector)[6].textContent.trim(), 'Assignee');
    assert.equal(findAll(selector)[7].textContent.trim(), 'Alerts');
    await settled().then(() => done());
  });

  test('The returned incidents appear as rows in the table', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-incidents}}`);
    setState();
    await waitForReduxStateChange(redux, 'respond.incidents.items');
    assert.ok(findAll('.rsa-data-table-body-row').length >= 1, 'At least one row of incidents appears in the data table');
    await settled().then(() => done());
  });

  test('The component shows an error message if there is an error fetching alerts', async function(assert) {
    const done = throwSocket({ methodToThrow: 'stream', modelNameToThrow: 'incidents' });
    setState();
    const errorMessage = i18n.t('rsaExplorer.fetchError');
    await render(hbs`{{rsa-incidents}}`);
    assert.equal(find(selectors.explorerTableErrorMessage).textContent.trim(), errorMessage, 'An error message is displayed if the fetch of tasks fails');
    await settled().then(() => done());
  });

  test('Clicking the filter button toggles the filter panel', async function(assert) {
    const done = assert.async();
    setState();
    await render(hbs`{{rsa-incidents}}`);
    assert.equal(findAll(`${selectors.explorer}.show-filters`).length, 1, 'The filter panel is showing');
    await click(selectors.filterPanelToggleButton);
    assert.equal(findAll(`${selectors.explorer}.show-filters`).length, 0, 'The filter panel is not showing');
    await click(selectors.filterPanelToggleButton);
    assert.equal(findAll(`${selectors.explorer}.show-filters`).length, 1, 'The filter panel is showing');
    await settled().then(() => done());
  });

  test('Clicking on a row focuses it and shows the inspector', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-incidents}}`);
    setState();
    await waitForReduxStateChange(redux, 'respond.incidents.items');
    // check to make sure we see the tasks appear in the data table
    assert.ok(findAll(selectors.tableRow).length >= 1, 'At least one row of alerts appears in the data table');
    assert.equal(findAll(`${selectors.explorer}.show-inspector`).length, 0, 'The inspector panel is not showing');
    await click(`${selectors.tableRow}:first-of-type ${selectors.tableCell}:nth-of-type(2)`);
    assert.equal(findAll(`${selectors.explorer}.show-inspector`).length, 1, 'The inspector panel is showing');
    await click(selectors.closeInspectorButton);
    assert.equal(findAll(`${selectors.explorer}.show-inspector`).length, 0, 'After clicking the close inspector button the inspector panel is not showing');
    await settled().then(() => done());
  });

  test('Clicking on the custom date switch changes to a custom date range control, and clicking again switches back', async function(assert) {
    const done = assert.async();
    assert.expect(3);

    const dateInputReady = (expected) => {
      return () => {
        const dateInput = findAll(selectors.customDateInput);
        return dateInput.length === expected;
      };
    };

    setState();
    await waitForReduxStateChange(redux, 'respond.incidents.items');

    await render(hbs`{{rsa-incidents}}`);
    await waitUntil(dateInputReady(0));
    assert.equal(findAll(selectors.customDateInput).length, 0, 'There are no custom date range input fields');

    await click(selectors.customDateToggle);
    await waitUntil(dateInputReady(2));
    assert.equal(findAll(selectors.customDateInput).length, 2, 'There are two custom date range input fields');

    await click(selectors.customDateToggle);
    await waitUntil(dateInputReady(0));
    assert.equal(findAll(selectors.customDateInput).length, 0, 'There are no custom date range input fields');
    await settled().then(() => done());
  });

  test('Selecting and deselecting a filter reflects the selection/deselection in the UI', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-incidents}}`);
    setState();
    const loadPriorityTypes = waitForReduxStateChange(redux, 'respondShared.createIncident.priorityTypes');
    await loadPriorityTypes;

    assert.equal(find(`${selectors.priorityFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), false, 'The filter is not selected');
    await click(`${selectors.priorityFilters} .rsa-form-checkbox-label:first-of-type`);
    assert.equal(find(`${selectors.priorityFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), true, 'The filter is selected');
    await click(`${selectors.priorityFilters} .rsa-form-checkbox-label:first-of-type`);
    assert.equal(find(`${selectors.priorityFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), false, 'The filter is not selected');
    await settled().then(() => done());
  });

  test('The reset filters button returns the filters to the original state', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-incidents}}`);
    setState();
    const loadPriorityTypes = waitForReduxStateChange(redux, 'respondShared.createIncident.priorityTypes');
    await loadPriorityTypes;
    assert.equal(find(`${selectors.priorityFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), false, 'The filter is not selected');
    await click(`${selectors.priorityFilters} .rsa-form-checkbox-label:first-of-type`);
    assert.equal(find(`${selectors.priorityFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), true, 'The filter is selected');
    await click(selectors.resetFiltersButton);
    assert.equal(find(`${selectors.priorityFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), false, 'The filter is not selected');
    await settled().then(() => done());
  });

  test('Clicking on select-all in header selects all the rows', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-incidents}}`);
    setState();
    const getItems = waitForReduxStateChange(redux, 'respond.incidents.items');
    await getItems;
    assert.ok(findAll(selectors.tableRow).length >= 1, 'At least one row of alerts appears in the data table');
    assert.equal(findAll(`${selectors.selectAllCheckbox}.checked`).length, 0, 'The select all checkbox is NOT checked');
    assert.equal(findAll(`${selectors.tableRow} ${selectors.tableCell}:first-of-type .rsa-form-checkbox.checked`).length, 0, 'There are no selected rows');
    await click(`${selectors.selectAllCheckbox} input`);
    assert.equal(findAll(`${selectors.selectAllCheckbox}.checked`).length, 1, 'The select all checkbox is checked');
    assert.equal(findAll(`${selectors.tableRow} ${selectors.tableCell}:first-of-type .rsa-form-checkbox.checked`).length, findAll(selectors.tableRow).length, 'All of the rows are selected');
    await settled().then(() => done());
  });

  test('Clicking on the delete button deletes the item after confirming via dialog', async function(assert) {
    assert.expect(3);
    let flashSuccess = false;
    const done = assert.async();
    const noResultsMessage = i18n.t('rsaExplorer.noResults');
    patchFlash((flash) => {
      const expectedMessage = i18n.t('rsaExplorer.flash.updateSuccess');
      assert.equal(flash.type, 'success');
      assert.equal(flash.message, expectedMessage);
      flashSuccess = true;
    });
    await render(hbs`
      <div id='modalDestination'></div>
      {{rsa-incidents}}
    `);
    setState();
    const getItems = waitForReduxStateChange(redux, 'respond.incidents.items');
    await getItems;
    const onDelete = waitForReduxStateChange(redux, 'respond.incidents.items');
    await click(`${selectors.selectAllCheckbox} input`);
    await click(selectors.deleteButton);
    await click(selectors.confirmationOkButton);
    await onDelete;
    await waitUntil(() => flashSuccess === true, { timeout: 8000 });
    assert.equal(find(selectors.noResultsMessage).textContent.trim(), noResultsMessage, 'There are no more results and the no results message displays');
    await settled().then(() => done());
  });

  test('Clicking on a table header cell toggles the sort', async function(assert) {
    assert.expect(4);
    let socketSuccessOne = false;
    let socketSuccessTwo = false;
    const done = assert.async();
    await render(hbs`{{rsa-incidents}}`);
    setState();
    // Wait until the incidents are added
    const getItems = waitForReduxStateChange(redux, 'respond.incidents.items');
    await getItems;

    // Clicking once, and the sort is descending
    patchSocket((method, modelName, query) => {
      assert.equal(query.sort[0].field, 'created');
      assert.equal(query.sort[0].descending, false);
      socketSuccessOne = true;
    }, 'stream', 'incidents');
    await click(selectors.createdColumnSortButton);

    await waitUntil(() => socketSuccessOne === true).then(() => {
      // Clicking again, and the sort is not descending (ascending)
      patchSocket((method, modelName, query) => {
        assert.equal(query.sort[0].field, 'created');
        assert.equal(query.sort[0].descending, true);
        socketSuccessTwo = true;
      }, 'stream', 'incidents');
    });
    await click(selectors.createdColumnSortButton);
    await waitUntil(() => socketSuccessOne === true && socketSuccessTwo === true);
    await settled().then(() => done());
  });

  test('Clicking on a row checkbox toggles the row selection', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-incidents}}`);
    setState();
    const getItems = waitForReduxStateChange(redux, 'respond.incidents.items');
    await getItems;
    assert.equal(find(selectors.firstRowSelectCheckbox).classList.contains('checked'), false, 'The first row is not selected/checked');
    await click(selectors.firstRowSelectCheckbox);
    assert.equal(find(selectors.firstRowSelectCheckbox).classList.contains('checked'), true, 'The first row is selected/checked');
    await click(selectors.firstRowSelectCheckbox);
    assert.equal(find(selectors.firstRowSelectCheckbox).classList.contains('checked'), false, 'The first row is not selected/checked');
    await settled().then(() => done());
  });
});

