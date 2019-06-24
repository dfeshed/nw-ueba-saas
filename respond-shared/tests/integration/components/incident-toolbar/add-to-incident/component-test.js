import { click, find, findAll, render, settled, triggerKeyEvent, fillIn } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import Immutable from 'seamless-immutable';
import { patchReducer } from '../../../../../tests/helpers/vnext-patch';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import sinon from 'sinon';
import addToIncidentCreators from 'respond-shared/actions/creators/add-to-incident-creators';
import { patchFlash } from '../../../../helpers/patch-flash';
import { throwSocket, patchSocket } from '../../../../helpers/patch-socket';

const initialState = {
  incidentSearchText: null,
  incidentSearchSortBy: 'created',
  incidentSearchSortIsDescending: true,
  incidentSearchStatus: null,
  incidentSearchResults: [],
  selectedIncident: null,
  stopSearchStream: null,
  isAddToIncidentInProgress: false
};

let setState;

const exampleIncidentSearchResults = Immutable.from([
  { id: 'INC-123', name: 'Test Incident 123', assignee: { id: 'meiskm' }, created: 150671337600 },
  { id: 'INC-321', name: 'Test Incident 321', assignee: { id: 'meiskm' }, created: 150671337600 }
]);

module('Integration | Component | incident-toolbar/add-to-incident', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state = {}) => {
      const fullState = {
        respondShared: {
          incidentSearchParams: state
        }
      };
      patchReducer(this, Immutable.from(fullState));
    };
    initialize(this.owner);
    this.owner.inject('component', 'i18n', 'service:i18n');
  });

  test('Typing in the searchbox dispatches the updateSearchIncidentsText action creator', async function(assert) {
    setState();
    const actionSpy = sinon.spy(addToIncidentCreators, 'updateSearchIncidentsText');
    await render(hbs`{{incident-toolbar/add-to-incident}}`);

    // Execute 3 keyups to ensure that we test the debounce on the keyup handler for searching incidents
    const $input = find('.ember-view input');
    await fillIn($input, 'test');
    triggerKeyEvent($input, 'keyup', 5);

    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The updateSearchIncidentsText action was called once');
      assert.ok(actionSpy.calledWith('test'));
      actionSpy.resetHistory();
      actionSpy.restore();
    });
  });

  test('The component appears in the DOM', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{incident-toolbar/add-to-incident}}`);
    assert.equal(findAll('.rsa-add-to-incident').length, 1, 'The component appears in the DOM');
  });

  test('Apply button is disabled when there is no incident selected', async function(assert) {
    setState({ ...initialState });
    await render(hbs`{{incident-toolbar/add-to-incident}}`);
    assert.equal(findAll('.apply.is-disabled').length, 1, 'The APPLY button is disabled when there is no incident selected');
  });

  test('Apply button is enabled when there is a selected incident', async function(assert) {
    setState({ ...initialState, selectedIncident: { id: 'INC-123' } });
    await render(hbs`{{incident-toolbar/add-to-incident }}`);
    assert.equal(findAll('.apply:not(.is-disabled)').length, 1, 'The APPLY button is not disabled when there is a selected incident');
  });

  test('Apply button is disabled when there is a selected incident but add-to-incident operation is in progress', async function(assert) {
    setState({ ...initialState, selectedIncident: { id: 'INC-123' }, isAddToIncidentInProgress: true });
    await render(hbs`{{incident-toolbar/add-to-incident}}`);
    assert.equal(findAll('.apply.is-disabled').length, 1,
      'The APPLY button is disabled when there is selected incident but isAddToIncidentInProgress is true');
  });

  test('An error is displayed if the incidentSearchStatus property is "error"', async function(assert) {
    setState({ ...initialState, incidentSearchStatus: 'error' });
    await render(hbs`{{incident-toolbar/add-to-incident}}`);
    assert.equal(find('.rsa-data-table .search-error').textContent.trim().length > 0, true,
      'A search error appears in the dom');
  });

  test('A loading spinner is displayed if the incidentSearchStatus property is "streaming"', async function(assert) {
    setState({ ...initialState, incidentSearchStatus: 'streaming' });
    await render(hbs`{{incident-toolbar/add-to-incident}}`);
    assert.equal(findAll('.rsa-data-table .rsa-loader').length, 1, 'A loading spinner appears in the dom');
  });

  test('Incident search results appear in the table', async function(assert) {
    setState({ ...initialState, incidentSearchResults: exampleIncidentSearchResults });
    await render(hbs`{{incident-toolbar/add-to-incident}}`);
    assert.equal(findAll('.rsa-data-table .rsa-data-table-body-row').length, 2, 'Two incident result rows appear in the data table');
  });

  test('The selected incident row shows a selection indicator (check icon)', async function(assert) {
    setState({ ...initialState, incidentSearchResults: exampleIncidentSearchResults, selectedIncident: exampleIncidentSearchResults[0] });
    await render(hbs`{{incident-toolbar/add-to-incident}}`);
    assert.equal(findAll('.rsa-data-table .rsa-data-table-body-cell .selected.rsa-icon').length, 1,
      'One of the rows is selected');
  });

  test('Clicking on a row dispatches the selectIncident action creator', async function(assert) {
    const actionSpy = sinon.spy(addToIncidentCreators, 'selectIncident');
    setState({ ...initialState, incidentSearchResults: exampleIncidentSearchResults });
    await render(hbs`{{incident-toolbar/add-to-incident}}`);
    await click(find('.rsa-data-table .rsa-data-table-body-row'));
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The selectIncident action was called once');
      assert.ok(actionSpy.calledWith(exampleIncidentSearchResults[0]));
      actionSpy.resetHistory();
      actionSpy.restore();
    });
  });

  test('Clicking on a the Apply button shows a success flash message when the request is successful', async function(assert) {
    setState({
      ...initialState,
      incidentSearchResults: exampleIncidentSearchResults,
      selectedIncident: exampleIncidentSearchResults[0]
    });
    assert.expect(5);
    const done = assert.async();

    patchSocket((method, modelName) => {
      assert.equal(method, 'updateRecord');
      assert.equal(modelName, 'associated-alerts');
    });
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedMessage = translation.t('respond.incidents.actions.actionMessages.addAlertToIncidentSucceeded',
        { incidentId: 'INC-123', entity: 'alerts' });
      assert.equal(flash.type, 'success');
      assert.equal(flash.message.string, expectedMessage);
      done();
    });
    this.set('handleFinish', () => {
      assert.ok(true);
    });
    await render(hbs`{{incident-toolbar/add-to-incident finish=handleFinish}}`);
    await click('.apply button');
  });

  test('Clicking on a the Apply button shows a failure flash message when the request fails', async function(assert) {
    assert.expect(2);
    const done = throwSocket();
    patchFlash((flash) => {
      const translation = this.owner.lookup('service:i18n');
      const expectedError = translation.t('respond.incidents.actions.actionMessages.addAlertToIncidentFailed', { entity: 'alerts' });
      assert.equal(flash.type, 'error');
      assert.equal(flash.message.string, expectedError);
      done();
    });
    setState({
      ...initialState,
      incidentSearchResults: exampleIncidentSearchResults,
      selectedIncident: exampleIncidentSearchResults[0]
    });
    await render(hbs`{{incident-toolbar/add-to-incident}}`);
    await click('.apply button');
    return settled();
  });

  test('Clicking the sort icon/button in a column dispatches the sortBy action creator', async function(assert) {
    const actionSpy = sinon.spy(addToIncidentCreators, 'updateSearchIncidentsSortBy');
    setState({ ...initialState });
    await render(hbs`{{incident-toolbar/add-to-incident}}`);
    await click(find('.rsa-data-table .header-title'));
    return settled().then(() => {
      assert.ok(actionSpy.calledOnce, 'The updateSearchIncidentsSortBy action was called once');
      actionSpy.resetHistory();
      actionSpy.restore();
    });
  });

  test('Clicking the cancel button calls the finish action', async function(assert) {
    setState({ ...initialState });
    assert.expect(1);

    this.set('finish', () => {
      assert.ok(true);
    });

    await render(hbs`{{incident-toolbar/add-to-incident finish=(action finish)}}`);
    await click('.cancel button');
  });
});