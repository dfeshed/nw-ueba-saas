import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from '../../../helpers/engine-resolver';
import waitForReduxStateChange from '../../../helpers/redux-async-helpers';
import wait from 'ember-test-helpers/wait';
import { throwSocket, patchSocket } from '../../../helpers/patch-socket';
import { patchFlash } from '../../../helpers/patch-flash';
import { getOwner } from '@ember/application';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import $ from 'jquery';

let redux, i18n;

moduleForComponent('rsa-alerts', 'Integration | Component | Respond Alerts', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    localStorage.clear();
    this.registry.injection('component', 'i18n', 'service:i18n');

    // inject and handle redux
    this.inject.service('redux');
    redux = this.get('redux');
    // inject and handle i18n
    this.inject.service('i18n');
    i18n = this.get('i18n');
    initialize(this);
  }
});

const selectors = {
  explorer: '.rsa-respond-explorer',
  filterPanelToggleButton: '.close-filters button',
  tableRow: '.rsa-explorer-table .rsa-data-table-body-row',
  tableCell: '.rsa-data-table-body-cell',
  customDateToggle: '.explorer-filters .created-filter .x-toggle-component input',
  customDateInput: '.explorer-filters .created-filter .rsa-form-input',
  alertTypeFilters: '.explorer-filters .alert-type-filter',
  resetFiltersButton: '.explorer-filters footer button',
  closeInspectorButton: '.close-inspector button',
  firstRowSelectCheckbox: '.rsa-explorer-table .rsa-data-table-body-cell:eq(0) .rsa-form-checkbox-label',
  selectAllCheckbox: '.rsa-explorer-table .rsa-data-table-header-cell:eq(0) .rsa-form-checkbox-label',
  createdColumnSortButton: '.rsa-explorer-table .column-sort:eq(0)',
  explorerTableErrorMessage: '.rsa-explorer-table .items-error',
  deleteButton: '.rsa-respond-explorer .rsa-alerts-toolbar-controls .delete-button button',
  confirmationOkButton: '.modal-footer-buttons .is-danger button',
  noResultsMessage: '.rsa-explorer-table .no-results-message .message'
};

test('The rsa-alerts component renders to the DOM', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  assert.equal(this.$('.rsa-alerts').length, 1, 'The rsa-alerts component should be found in the DOM');
});

test('The returned alerts appear as rows in the table', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  // Wait until the alerts are added
  const getItems = waitForReduxStateChange(redux, 'respond.alerts.items');
  getItems.then(() => {
    // check to make sure we see the alerts appear in the data table
    assert.ok(this.$(selectors.tableRow).length >= 1, 'At least one row of alerts appears in the data table');
  });
});

test('The component shows an error message if there is an error fetching alerts', function(assert) {
  const errorMessage = i18n.t('respond.errorPage.fetchError');
  const done = throwSocket({ methodToThrow: 'stream', modelNameToThrow: 'alerts' });
  this.render(hbs`{{rsa-alerts}}`);
  assert.equal(this.$(selectors.explorerTableErrorMessage).text().trim(), errorMessage, 'An error message is displayed if the fetch of alerts fails');
  done();
});

test('Clicking the filter button toggles the filter panel', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  assert.equal(this.$(`${selectors.explorer}.show-filters`).length, 1, 'The filter panel is showing');
  this.$(selectors.filterPanelToggleButton).click();
  return wait().then(() => {
    assert.equal(this.$(`${selectors.explorer}.show-filters`).length, 0, 'The filter panel is not showing');
    this.$(selectors.filterPanelToggleButton).click();
    return wait().then(() => {
      assert.equal(this.$(`${selectors.explorer}.show-filters`).length, 1, 'The filter panel is showing');
    });
  });
});

test('Clicking on a row focuses it and shows the inspector', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  // Wait until the alerts are added
  const getItems = waitForReduxStateChange(redux, 'respond.alerts.items');
  return getItems.then(() => {
    // check to make sure we see the alerts appear in the data table
    assert.ok(this.$(selectors.tableRow).length >= 1, 'At least one row of alerts appears in the data table');
    assert.equal(this.$(`${selectors.explorer}.show-inspector`).length, 0, 'The inspector panel is not showing');
    this.$(`${selectors.tableRow}:eq(0) ${selectors.tableCell}:eq(2)`).click();
    return wait().then(() => {
      assert.equal(this.$(`${selectors.explorer}.show-inspector`).length, 1, 'The inspector panel is showing');
      this.$(selectors.closeInspectorButton).click();
      return wait().then(() => {
        assert.equal(this.$(`${selectors.explorer}.show-inspector`).length, 0, 'After clicking the close inspector button the inspector panel is not showing');
      });
    });
  });
});

test('Clicking on the custom date switch changes to a custom date range control, and clicking again switches back', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  assert.equal(this.$(selectors.customDateInput).length, 0, 'There are no custom date range input fields');
  this.$(selectors.customDateToggle).click();
  return wait().then(() => {
    assert.equal(this.$(selectors.customDateInput).length, 2, 'There are two custom date range input fields');
    this.$(selectors.customDateToggle).click();
    return wait().then(() => {
      assert.equal(this.$(selectors.customDateInput).length, 0, 'There are no custom date range input fields');
    });
  });
});

test('Selecting and deselecting a filter refelects the selection/deselection in the UI', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  assert.equal(this.$(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:eq(0)`).hasClass('checked'), false, 'The filter is not selected');
  this.$(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:eq(0)`).click();
  return wait().then(() => {
    assert.equal(this.$(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:eq(0)`).hasClass('checked'), true, 'The filter is selected');
    this.$(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:eq(0)`).click();
    return wait().then(() => {
      assert.equal(this.$(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:eq(0)`).hasClass('checked'), false, 'The filter is not selected');
    });
  });
});

test('The reset filters button returns the filters to the original state', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  assert.equal(this.$(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:eq(0)`).hasClass('checked'), false, 'The filter is not selected');
  this.$(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:eq(0)`).click();
  return wait().then(() => {
    assert.equal(this.$(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:eq(0)`).hasClass('checked'), true, 'The filter is selected');
    this.$(selectors.resetFiltersButton).click();
    return wait().then(() => {
      assert.equal(this.$(`${selectors.alertTypeFilters} .rsa-form-checkbox-label:eq(0)`).hasClass('checked'), false, 'The filter is not selected');
    });
  });
});

test('Clicking on select-all in header selects all the rows', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  // Wait until the alerts are added
  const getItems = waitForReduxStateChange(redux, 'respond.alerts.items');
  return getItems.then(() => {
    // check to make sure we see the alerts appear in the data table
    assert.ok(this.$(selectors.tableRow).length >= 1, 'At least one row of alerts appears in the data table');
    assert.equal(this.$(`${selectors.selectAllCheckbox}.checked`).length, 0, 'The select all checkbox is NOT checked');
    assert.equal(this.$(`${selectors.tableRow} ${selectors.tableCell}:eq(0) .rsa-form-checkbox.checked`).length, 0, 'There are no selected rows');
    this.$(`${selectors.selectAllCheckbox} input`).click();
    return wait().then(() => {
      assert.equal(this.$(`${selectors.selectAllCheckbox}.checked`).length, 1, 'The select all checkbox is checked');
      assert.equal(this.$(`${selectors.tableRow} ${selectors.tableCell}:first-of-type .rsa-form-checkbox.checked`).length, this.$(selectors.tableRow).length, 'All of the rows are selected');
    });
  });
});

test('Clicking on the delete button deletes the item after confirming via dialog', function(assert) {
  assert.expect(3);
  let onDelete;
  const noResultsMessage = i18n.t('respond.explorer.noResults');
  patchFlash((flash) => {
    const translation = getOwner(this).lookup('service:i18n');
    const expectedMessage = translation.t('respond.entities.actionMessages.updateSuccess');
    assert.equal(flash.type, 'success');
    assert.equal(flash.message.string, expectedMessage);
  });
  this.render(hbs`{{rsa-alerts}}`);
  // Wait until the alerts are added
  const getItems = waitForReduxStateChange(redux, 'respond.alerts.items');
  return getItems.then(() => {
    onDelete = waitForReduxStateChange(redux, 'respond.alerts.items');
    this.$(`${selectors.selectAllCheckbox} input`).click();
    return wait().then(() => {
      this.$(selectors.deleteButton).click();
      return wait().then(() => {
        $('.modal-footer-buttons .is-danger button').click();
        return wait();
      }).then(() => {
        onDelete.then(() => {
          assert.equal(this.$(selectors.noResultsMessage).text().trim(), noResultsMessage, 'There are no more results and the no results message displays');
        });
      });
    });
  });
});

test('Clicking on a table header cell toggles the sort', function(assert) {
  assert.expect(4);
  this.render(hbs`{{rsa-alerts}}`);
  // Wait until the incidents are added
  const getItems = waitForReduxStateChange(redux, 'respond.alerts.items');
  return getItems.then(() => {
    // Clicking once, and the sort is descending
    patchSocket((method, modelName, query) => {
      assert.equal(query.sort[0].field, 'receivedTime');
      assert.equal(query.sort[0].descending, false);
    }, 'stream', 'alerts');
    this.$(selectors.createdColumnSortButton).click();
    return wait().then(() => {
      // Clicking again, and the sort is not descending (ascending)
      patchSocket((method, modelName, query) => {
        assert.equal(query.sort[0].field, 'receivedTime');
        assert.equal(query.sort[0].descending, true);
      }, 'stream', 'alerts');
      this.$(selectors.createdColumnSortButton).click();
    });
  });
});

test('Clicking on a row checkbox toggles the row selection', function(assert) {
  this.render(hbs`{{rsa-alerts}}`);
  // Wait until the incidents are added
  const getItems = waitForReduxStateChange(redux, 'respond.alerts.items');
  return getItems.then(() => {
    assert.equal(this.$(selectors.firstRowSelectCheckbox).hasClass('checked'), false, 'The first row is not selected/checked');
    this.$(selectors.firstRowSelectCheckbox).click();
    return wait().then(() => {
      assert.equal(this.$(selectors.firstRowSelectCheckbox).hasClass('checked'), true, 'The first row is selected/checked');
      this.$(selectors.firstRowSelectCheckbox).click();
      return wait().then(() => {
        assert.equal(this.$(selectors.firstRowSelectCheckbox).hasClass('checked'), false, 'The first row is not selected/checked');
      });
    });
  });
});
