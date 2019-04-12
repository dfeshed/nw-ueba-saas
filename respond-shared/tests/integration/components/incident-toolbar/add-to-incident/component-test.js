import { click, find, findAll, render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';

const exampleIncidentSearchResults = Immutable.from([
  { id: 'INC-123', name: 'Test Incident 123', assignee: { id: 'meiskm' }, created: 150671337600 },
  { id: 'INC-321', name: 'Test Incident 321', assignee: { id: 'meiskm' }, created: 150671337600 }
]);

module('Integration | Component | incident-toolbar/add-to-incident', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    this.set('incidentSearchText', null);
    this.set('sortBy', 'created');
    this.set('isSortDescending', true);
    this.set('incidentSearchStatus', null);
  });

  test('Apply button is disabled when there is no incident selected', async function(assert) {
    this.set('isAddToAlertsUnavailable', true);
    await render(hbs`{{incident-toolbar/add-to-incident isAddToAlertsUnavailable=isAddToAlertsUnavailable}}`);
    assert.equal(findAll('.apply.is-disabled').length, 1, 'The APPLY button is disabled when there is no incident selected');
  });

  test('Apply button is enabled when there is a selected incident', async function(assert) {
    this.set('selectedIncident', exampleIncidentSearchResults[0]);
    this.set('incidentSearchResults', exampleIncidentSearchResults);
    await render(hbs`{{incident-toolbar/add-to-incident
      selectedIncident=selectedIncident
      incidentSearchResults=incidentSearchResults}}`);
    assert.equal(findAll('.apply:not(.is-disabled)').length, 1, 'The APPLY button is not disabled when there is a selected incident');
  });

  test('An error is displayed if the incidentSearchStatus property is "error"', async function(assert) {
    this.set('incidentSearchStatus', 'error');
    await render(hbs`{{incident-toolbar/add-to-incident
      incidentSearchStatus=incidentSearchStatus}}`);
    assert.equal(find('.rsa-data-table .search-error').textContent.trim().length > 0, true,
      'A search error appears in the dom');
  });

  test('A loading spinner is displayed if the incidentSearchStatus property is "streaming"', async function(assert) {
    this.set('incidentSearchStatus', 'streaming');
    await render(hbs`{{incident-toolbar/add-to-incident incidentSearchStatus=incidentSearchStatus}}`);
    assert.equal(findAll('.rsa-data-table .rsa-loader').length, 1, 'A loading spinner appears in the dom');
  });

  test('Incident search results appear in the table', async function(assert) {
    this.set('incidentSearchResults', exampleIncidentSearchResults);
    await render(hbs`{{incident-toolbar/add-to-incident incidentSearchResults=incidentSearchResults}}`);
    assert.equal(findAll('.rsa-data-table .rsa-data-table-body-row').length, 2, 'Two incident result rows appear in the data table');
  });

  test('The selected incident row shows a selection indicator (check icon)', async function(assert) {
    this.set('selectedIncident', exampleIncidentSearchResults[0]);
    this.set('incidentSearchResults', exampleIncidentSearchResults);
    await render(hbs`{{incident-toolbar/add-to-incident
      incidentSearchResults=incidentSearchResults
      selectedIncident=selectedIncident}}`);
    assert.equal(findAll('.rsa-data-table .rsa-data-table-body-cell .selected.rsa-icon').length, 1,
      'One of the rows is selected');
  });

  test('Clicking on a the Apply button should call addtoIncident action of parent component', async function(assert) {
    this.set('selectedIncident', exampleIncidentSearchResults[0]);
    this.set('incidentSearchResults', exampleIncidentSearchResults);

    this.set('addtoIncident', (incidentId) => {
      assert.equal(incidentId, 'INC-123');
    });

    this.set('handleFinish', () => {
      assert.ok(true);
    });
    await render(hbs`{{incident-toolbar/add-to-incident
      incidentSearchResults=incidentSearchResults
      selectedIncident=selectedIncident
      addtoIncident=addtoIncident
      finish=handleFinish}}`);
    await click('.apply button');
  });

  test('Typing in the searchbox should call searchIncident action of parent component', async function(assert) {

    this.set('searchIncident', (searchText) => {
      assert.equal(searchText, 'test');
    });
    await render(hbs`{{incident-toolbar/add-to-incident searchIncident=searchIncident}}`);

    this.$('input').val('test');

    // Execute 3 keyups to ensure that we test the debounce on the keyup handler for searching incidents
    this.$('input').trigger('keyup');
    this.$('input').trigger('keyup');
    this.$('input').trigger('keyup');
  });

  test('Clicking the sort icon/button in a column should call sortIncident action of parent component', async function(assert) {
    this.set('selectedIncident', exampleIncidentSearchResults[0]);
    this.set('incidentSearchResults', exampleIncidentSearchResults);

    this.set('sortIncident', (column, isNewSortDescending) => {
      assert.equal(column, 'name');
      assert.equal(isNewSortDescending, true);
    });

    await render(hbs`{{incident-toolbar/add-to-incident
      sortBy=sortBy
      isSortDescending=isSortDescending
      incidentSearchResults=incidentSearchResults
      selectedIncident=selectedIncident
      sortIncident=sortIncident}}`);

    this.$('.rsa-data-table .header-title')[2].click();
  });

  test('Clicking the cancel button calls the finish action', async function(assert) {

    this.set('finish', () => {
      assert.ok(true);
    });

    await render(hbs`{{incident-toolbar/add-to-incident finish=(action finish)}}`);

    await click('.cancel button');
  });
});
