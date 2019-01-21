import { setupRenderingTest } from 'ember-qunit';
import { module, test, skip } from 'qunit';
import { settled, waitUntil, click, findAll, find, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import waitForReduxStateChange from '../../../helpers/redux-async-helpers';
import { throwSocket, patchSocket } from '../../../helpers/patch-socket';
import { patchFlash } from '../../../helpers/patch-flash';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { initializeAlerts } from 'respond/actions/creators/alert-creators';

let i18n, redux, setState;

module('Integration | Component | Respond Alerts', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    localStorage.clear();
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
    redux = this.owner.lookup('service:redux');
    setState = () => {
      redux.dispatch(initializeAlerts());
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
    alertTypeFilters: '.explorer-filters .alert-type-filter',
    resetFiltersButton: '.explorer-filters footer button',
    closeInspectorButton: '.close-inspector button',
    firstRowSelectCheckbox: '.rsa-explorer-table .rsa-data-table-body-cell:first-of-type .rsa-form-checkbox-label',
    selectAllCheckbox: '.rsa-explorer-table .rsa-data-table-header-cell:first-of-type .rsa-form-checkbox-label',
    createdColumnSortButton: '.rsa-explorer-table .sort-indicator:first-of-type',
    explorerTableErrorMessage: '.rsa-explorer-table .items-error',
    deleteButton: '.rsa-explorer .rsa-explorer-toolbar .is-danger button',
    confirmationOkButton: '.modal-footer-buttons .is-primary button',
    noResultsMessage: '.rsa-explorer-table .no-results-message .message'
  };

  skip('The rsa-alerts component renders to the DOM', async function(assert) {
    const done = assert.async();
    setState();
    await render(hbs`{{rsa-alerts}}`);
    assert.equal(findAll('.rsa-alerts').length, 1, 'The rsa-alerts component should be found in the DOM');
    await settled().then(() => done());
  });

  test('The returned alerts appear as rows in the table', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-alerts}}`);
    setState();
    const getItems = waitForReduxStateChange(redux, 'respond.alerts.items');
    await getItems;
    assert.ok(findAll('.rsa-data-table-body-row').length >= 1, 'At least one row of alerts appears in the data table');
    await settled().then(() => done());
  });

  test('The component shows an error message if there is an error fetching alerts', async function(assert) {
    const done = throwSocket({ methodToThrow: 'stream', modelNameToThrow: 'alerts' });
    setState();
    const errorMessage = i18n.t('rsaExplorer.fetchError');
    await render(hbs`{{rsa-alerts}}`);
    assert.equal(find(selectors.explorerTableErrorMessage).textContent.trim(), errorMessage, 'An error message is displayed if the fetch of tasks fails');
    await settled().then(() => done());
  });

  test('Clicking the filter button toggles the filter panel', async function(assert) {
    const done = assert.async();
    setState();
    await render(hbs`{{rsa-alerts}}`);
    assert.equal(findAll(`${selectors.explorer}.show-filters`).length, 1, 'The filter panel is showing');
    await click(selectors.filterPanelToggleButton);
    assert.equal(findAll(`${selectors.explorer}.show-filters`).length, 0, 'The filter panel is not showing');
    await click(selectors.filterPanelToggleButton);
    assert.equal(this.$(`${selectors.explorer}.show-filters`).length, 1, 'The filter panel is showing');
    await settled().then(() => done());
  });

  test('Clicking on a row focuses it and shows the inspector', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-alerts}}`);
    setState();
    await waitForReduxStateChange(redux, 'respond.alerts.items');
    // check to make sure we see the tasks appear in the data table
    assert.ok(this.$(selectors.tableRow).length >= 1, 'At least one row of alerts appears in the data table');
    assert.equal(findAll(`${selectors.explorer}.show-inspector`).length, 0, 'The inspector panel is not showing');
    await click(`${selectors.tableRow}:first-of-type ${selectors.tableCell}:nth-of-type(2)`);
    assert.equal(findAll(`${selectors.explorer}.show-inspector`).length, 1, 'The inspector panel is showing');
    await click(selectors.closeInspectorButton);
    assert.equal(this.$(`${selectors.explorer}.show-inspector`).length, 0, 'After clicking the close inspector button the inspector panel is not showing');
    await settled().then(() => done());
  });

  test('Clicking on the custom date switch changes to a custom date range control, and clicking again switches back', async function(assert) {
    assert.expect(3);
    const done = assert.async();

    const dateInputReady = (expected) => {
      return () => {
        const dateInput = findAll(selectors.customDateInput);
        return dateInput.length === expected;
      };
    };
    setState();
    await waitForReduxStateChange(redux, 'respond.alerts.items');

    await render(hbs`{{rsa-alerts}}`);
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

  test('Selecting and deselecting a filter refelects the selection/deselection in the UI', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-alerts}}`);
    setState();
    await waitForReduxStateChange(redux, 'respond.alerts.items');
    assert.equal(find(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), false, 'The filter is not selected');
    await click(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:first-of-type`);
    assert.equal(find(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), true, 'The filter is selected');
    await click(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:first-of-type`);
    assert.equal(find(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), false, 'The filter is not selected');
    await settled().then(() => done());
  });

  test('The reset filters button returns the filters to the original state', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-alerts}}`);
    setState();
    await waitForReduxStateChange(redux, 'respond.alerts.items');
    await render(hbs`{{rsa-alerts}}`);
    assert.equal(find(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), false, '1. The filter is not selected');
    await click(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:first-of-type`);
    assert.equal(find(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), true, '2. The filter is selected');
    await click(selectors.resetFiltersButton);
    assert.equal(find(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:first-of-type`).classList.contains('checked'), false, '3. The filter is not selected');
    await settled().then(() => done());
  });

  test('Clicking on select-all in header selects all the rows', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-alerts}}`);
    setState();
    await waitForReduxStateChange(redux, 'respond.alerts.items');
    assert.ok(findAll(selectors.tableRow).length >= 1, 'At least one row of alerts appears in the data table');
    assert.equal(findAll(`${selectors.selectAllCheckbox}.checked`).length, 0, 'The select all checkbox is NOT checked');
    assert.equal(findAll(`${selectors.tableRow} ${selectors.tableCell}:first-of-type .rsa-form-checkbox.checked`).length, 0, 'There are no selected rows');
    await click(`${selectors.selectAllCheckbox} input`);
    assert.equal(findAll(`${selectors.selectAllCheckbox}.checked`).length, 1, 'The select all checkbox is checked');
    assert.equal(findAll(`${selectors.tableRow} ${selectors.tableCell}:first-of-type .rsa-form-checkbox.checked`).length, this.$(selectors.tableRow).length, 'All of the rows are selected');
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
      assert.equal(flash.message.string, expectedMessage);
      flashSuccess = true;
    });
    await render(hbs`{{rsa-alerts}}`);
    setState();
    await waitForReduxStateChange(redux, 'respond.alerts.items');
    const onDelete = waitForReduxStateChange(redux, 'respond.alerts.items');
    await click(`${selectors.selectAllCheckbox} input`);
    await click(selectors.deleteButton);
    await click(selectors.confirmationOkButton);
    await onDelete;
    await waitUntil(() => flashSuccess === true, { timeout: 8000 });
    await waitUntil(() => {
      const noResultsMessageFound = find(selectors.noResultsMessage);
      return noResultsMessageFound && noResultsMessageFound.textContent && noResultsMessageFound.textContent.trim() === noResultsMessage.string;
    }, { timeout: 8000 });
    assert.equal(find(selectors.noResultsMessage).textContent.trim(), noResultsMessage, 'There are no more results and the no results message displays');
    await settled().then(() => done());
  });

  test('Clicking on a table header cell toggles the sort', async function(assert) {
    assert.expect(4);
    let socketSuccessOne = false;
    let socketSuccessTwo = false;
    const done = assert.async();
    await render(hbs`{{rsa-alerts}}`);
    setState();
    // Wait until the alerts are added
    const getItems = waitForReduxStateChange(redux, 'respond.alerts.items');
    await getItems;

    // Clicking once, and the sort is descending
    patchSocket((method, modelName, query) => {
      assert.equal(query.sort[0].field, 'receivedTime');
      assert.equal(query.sort[0].descending, false);
      socketSuccessOne = true;
    }, 'stream', 'alerts');
    await click(selectors.createdColumnSortButton);

    await waitUntil(() => socketSuccessOne === true).then(() => {
      // Clicking again, and the sort is not descending (ascending)
      patchSocket((method, modelName, query) => {
        assert.equal(query.sort[0].field, 'receivedTime');
        assert.equal(query.sort[0].descending, true);
        socketSuccessTwo = true;
      }, 'stream', 'alerts');
    });
    await click(selectors.createdColumnSortButton);
    await waitUntil(() => socketSuccessOne === true && socketSuccessTwo === true);
    await settled().then(() => done());
  });

  test('Clicking on a row checkbox toggles the row selection', async function(assert) {
    const done = assert.async();
    await render(hbs`{{rsa-alerts}}`);
    setState();
    const getItems = waitForReduxStateChange(redux, 'respond.alerts.items');
    await getItems;
    assert.equal(find(selectors.firstRowSelectCheckbox).classList.contains('checked'), false, 'The first row is not selected/checked');
    await click(selectors.firstRowSelectCheckbox);
    assert.equal(find(selectors.firstRowSelectCheckbox).classList.contains('checked'), true, 'The first row is selected/checked');
    await click(selectors.firstRowSelectCheckbox);
    assert.equal(find(selectors.firstRowSelectCheckbox).classList.contains('checked'), false, 'The first row is not selected/checked');
    await settled().then(() => done());
  });
});

