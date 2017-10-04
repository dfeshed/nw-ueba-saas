import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import * as addAlertsCreators from 'respond/actions/creators/add-alerts-to-incident-creators';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';

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

moduleForComponent('rsa-alerts/add-to-incident', 'Integration | Component | Respond Alerts Add to Incident', {
  integration: true,
  resolver: engineResolverFor('respond'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      const fullState = { respond: { alertIncidentAssociation: state } };
      applyPatch(Immutable.from(fullState));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('The component appears in the DOM', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  assert.equal(this.$('.rsa-add-to-incident').length, 1, 'The component appears in the DOM');
});

test('Apply button is disabled when there is no incident selected', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  assert.equal(this.$('.apply.is-disabled').length, 1, 'The APPLY button is disabled when there is no incident selected');
});

test('Apply button is enabled when there is a selected incident', function(assert) {
  setState({ ...initialState, selectedIncident: { id: 'INC-123' } });
  this.render(hbs`{{rsa-alerts/add-to-incident }}`);
  assert.equal(this.$('.apply:not(.is-disabled)').length, 1, 'The APPLY button is not disabled when there is a selected incident');
});

test('Apply button is disabled when there is a selected incident but add-to-incident operation is in progress', function(assert) {
  setState({ ...initialState, selectedIncident: { id: 'INC-123' }, isAddAlertsInProgress: true });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  assert.equal(this.$('.apply.is-disabled').length, 1,
    'The APPLY button is disabled when there is selected incident but isAddAlertsInProgress is true');
});

test('An error is displayed if the incidentSearchStatus property is "error"', function(assert) {
  setState({ ...initialState, incidentSearchStatus: 'error' });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  assert.equal(this.$('.rsa-data-table .search-error').text().trim().length > 0, true,
    'A search error appears in the dom');
});

test('A loading spinner is displayed if the incidentSearchStatus property is "streaming"', function(assert) {
  setState({ ...initialState, incidentSearchStatus: 'streaming' });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  assert.equal(this.$('.rsa-data-table .rsa-loader').length, 1, 'A loading spinner appears in the dom');
});

test('Incident search results appear in the table', function(assert) {
  setState({ ...initialState, incidentSearchResults: exampleIncidentSearchResults });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  assert.equal(this.$('.rsa-data-table .rsa-data-table-body-row').length, 2, 'Two incident result rows appear in the data table');
});

test('The selected incident row shows a selection indicator (check icon)', function(assert) {
  setState({ ...initialState, incidentSearchResults: exampleIncidentSearchResults, selectedIncident: exampleIncidentSearchResults[0] });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  assert.equal(this.$('.rsa-data-table .rsa-data-table-body-cell .selected.rsa-icon').length, 1,
    'One of the rows is selected');
});

test('Clicking on a row dispatches the selectIncident action creator', function(assert) {
  const actionSpy = sinon.spy(addAlertsCreators, 'selectIncident');
  setState({ ...initialState, incidentSearchResults: exampleIncidentSearchResults });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  this.$('.rsa-data-table .rsa-data-table-body-row').first().click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The selectIncident action was called once');
    assert.ok(actionSpy.calledWith(exampleIncidentSearchResults[0]));
  });
});

test('Clicking on a the Apply button dispatches the addAlertsToIncident action creator', function(assert) {
  const actionSpy = sinon.spy(addAlertsCreators, 'addAlertsToIncident');
  setState({
    ...initialState,
    incidentSearchResults: exampleIncidentSearchResults,
    selectedIncident: exampleIncidentSearchResults[0]
  });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  this.$('.apply button').click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The addAlertsToIncident action was called once');
  });
});

test('Typing in the searchbox dispatches the updateSearchIncidentsText action creator', function(assert) {
  const actionSpy = sinon.spy(addAlertsCreators, 'updateSearchIncidentsText');
  setState({ ...initialState });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  this.$('input').val('test');
  // Execute 3 keyups to ensure that we test the debounce on the keyup handler for searching incidents
  this.$('input').trigger('keyup');
  this.$('input').trigger('keyup');
  this.$('input').trigger('keyup');
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateSearchIncidentsText action was called once');
    assert.ok(actionSpy.calledWith('test'));
  });
});

test('Clicking the sort icon/button in a column dispatches the sortBy action creator', function(assert) {
  const actionSpy = sinon.spy(addAlertsCreators, 'updateSearchIncidentsSortBy');
  setState({ ...initialState });
  this.render(hbs`{{rsa-alerts/add-to-incident}}`);
  this.$('.rsa-data-table .rsa-icon.sort').first().click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The updateSearchIncidentsSortBy action was called once');
  });
});

test('Clicking the cancel button calls the finish action', function(assert) {
  assert.expect(1);
  this.on('finish', function() {
    assert.ok(true);
  });
  this.render(hbs`{{rsa-alerts/add-to-incident finish=(action 'finish')}}`);
  this.$('.cancel button').click();
});