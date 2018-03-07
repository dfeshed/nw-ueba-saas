import { click, find, findAll, render, settled } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import engineResolverFor from '../../../../helpers/engine-resolver';
import * as addAlertsCreators from 'respond/actions/creators/add-alerts-to-incident-creators';
import Immutable from 'seamless-immutable';
import { patchFlash } from '../../../../helpers/patch-flash';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { throwSocket, patchSocket } from '../../../../helpers/patch-socket';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

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

let setState;
const exampleIncidentSearchResults = Immutable.from([
  { id: 'INC-123', name: 'Test Incident 123', assignee: { id: 'meiskm' }, created: 150671337600 },
  { id: 'INC-321', name: 'Test Incident 321', assignee: { id: 'meiskm' }, created: 150671337600 }
]);

module('Integration | Component | Respond Alerts Add to Incident', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('respond')
  });

  hooks.beforeEach(function() {
    setState = (state) => {
      const fullState = { respond: { alertIncidentAssociation: state } };
      patchReducer(this, Immutable.from(fullState));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('The component appears in the DOM', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    assert.equal(findAll('.rsa-add-to-incident').length, 1, 'The component appears in the DOM');
  });

  test('Apply button is disabled when there is no incident selected', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    assert.equal(findAll('.apply.is-disabled').length, 1, 'The APPLY button is disabled when there is no incident selected');
  });

  test('Apply button is enabled when there is a selected incident', async function(assert) {
    setState({ ...initialState, selectedIncident: { id: 'INC-123' } });
    await render(hbs`{{rsa-alerts/add-to-incident }}`);
    assert.equal(findAll('.apply:not(.is-disabled)').length, 1, 'The APPLY button is not disabled when there is a selected incident');
  });

  test('Apply button is disabled when there is a selected incident but add-to-incident operation is in progress', async function(assert) {
    setState({ ...initialState, selectedIncident: { id: 'INC-123' }, isAddAlertsInProgress: true });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    assert.equal(findAll('.apply.is-disabled').length, 1,
      'The APPLY button is disabled when there is selected incident but isAddAlertsInProgress is true');
  });

  test('An error is displayed if the incidentSearchStatus property is "error"', async function(assert) {
    setState({ ...initialState, incidentSearchStatus: 'error' });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    assert.equal(find('.rsa-data-table .search-error').textContent.trim().length > 0, true,
      'A search error appears in the dom');
  });

  test('A loading spinner is displayed if the incidentSearchStatus property is "streaming"', async function(assert) {
    setState({ ...initialState, incidentSearchStatus: 'streaming' });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    assert.equal(findAll('.rsa-data-table .rsa-loader').length, 1, 'A loading spinner appears in the dom');
  });

  test('Incident search results appear in the table', async function(assert) {
    setState({ ...initialState, incidentSearchResults: exampleIncidentSearchResults });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    assert.equal(findAll('.rsa-data-table .rsa-data-table-body-row').length, 2, 'Two incident result rows appear in the data table');
  });

  test('The selected incident row shows a selection indicator (check icon)', async function(assert) {
    setState({ ...initialState, incidentSearchResults: exampleIncidentSearchResults, selectedIncident: exampleIncidentSearchResults[0] });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    assert.equal(findAll('.rsa-data-table .rsa-data-table-body-cell .selected.rsa-icon').length, 1,
      'One of the rows is selected');
  });

  test('Clicking on a row dispatches the selectIncident action creator', async function(assert) {
    const actionSpy = sinon.spy(addAlertsCreators, 'selectIncident');
    setState({ ...initialState, incidentSearchResults: exampleIncidentSearchResults });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    this.$('.rsa-data-table .rsa-data-table-body-row').first().click();
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The selectIncident action was called once');
      assert.ok(actionSpy.calledWith(exampleIncidentSearchResults[0]));
    });
  });

  test('Clicking on a the Apply button shows a success flash message when the request is successful', async function(assert) {
    assert.expect(4);

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
    });
    setState({
      ...initialState,
      incidentSearchResults: exampleIncidentSearchResults,
      selectedIncident: exampleIncidentSearchResults[0]
    });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    await click('.apply button');
    return settled();
  });

  test('Clicking on a the Apply button shows a failure flash message when the request fails', async function(assert) {
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
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    await click('.apply button');
    return settled();
  });

  test('Typing in the searchbox dispatches the updateSearchIncidentsText action creator', async function(assert) {
    const actionSpy = sinon.spy(addAlertsCreators, 'updateSearchIncidentsText');
    setState({ ...initialState });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    this.$('input').val('test');

    // Execute 3 keyups to ensure that we test the debounce on the keyup handler for searching incidents
    this.$('input').trigger('keyup');
    this.$('input').trigger('keyup');
    this.$('input').trigger('keyup');

    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The updateSearchIncidentsText action was called once');
      assert.ok(actionSpy.calledWith('test'));
    });
  });

  test('Clicking the sort icon/button in a column dispatches the sortBy action creator', async function(assert) {
    const actionSpy = sinon.spy(addAlertsCreators, 'updateSearchIncidentsSortBy');
    setState({ ...initialState });
    await render(hbs`{{rsa-alerts/add-to-incident}}`);
    this.$('.rsa-data-table .rsa-icon.sort').first().click();
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The updateSearchIncidentsSortBy action was called once');
    });
  });

  test('Clicking the cancel button calls the finish action', async function(assert) {
    assert.expect(1);

    this.set('finish', () => {
      assert.ok(true);
    });

    await render(hbs`{{rsa-alerts/add-to-incident finish=(action finish)}}`);
    await click('.cancel button');
  });
});
