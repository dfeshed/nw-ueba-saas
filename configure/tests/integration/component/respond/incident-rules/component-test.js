import { moduleForComponent, test } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import sinon from 'sinon';
import engineResolverFor from '../../../../helpers/engine-resolver';
import { applyPatch, revertPatch } from '../../../../helpers/patch-reducer';
import wait from 'ember-test-helpers/wait';
import Immutable from 'seamless-immutable';
import rules from '../../../../data/subscriptions/incident-rules/findAll/data';
import $ from 'jquery';
import * as alertRuleCreators from 'configure/actions/creators/respond/incident-rule-creators';

const initialState = {
  rules,
  rulesStatus: 'complete',
  isTransactionUnderway: false,
  selectedRule: null
};

let setState;

moduleForComponent('respond/incident-rules', 'Integration | Component | Respond Incident Rules', {
  integration: true,
  resolver: engineResolverFor('configure'),
  beforeEach() {
    this.registry.injection('component', 'i18n', 'service:i18n');
    setState = (state) => {
      const fullState = { configure: { respond: { incidentRules: state } } };
      applyPatch(Immutable.from(fullState));
      this.inject.service('redux');
    };
  },
  afterEach() {
    revertPatch();
  }
});

test('The component appears in the DOM with the expected number of rows', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rules useLazyRendering=false}}`);
  assert.equal(this.$('.rsa-incident-rules').length, 1, 'The component appears in the DOM');
  assert.equal(this.$('.rsa-data-table-body-row').length, 11, 'There are 11 rows in the table');
  assert.equal(this.$('.rsa-data-table-body-row.is-selected').length, 0, 'There are no selected rows');
});

test('The no results message appears when there are no rules', function(assert) {
  setState({ ...initialState, rules: [] });
  this.render(hbs`{{respond/incident-rules useLazyRendering=false}}`);
  assert.equal(this.$('.no-results-message .message').text().trim(), 'No alert rules were found', 'The no results message appears when there are no rules');
});

test('The loading spinner appears when rulesState is "wait"', function(assert) {
  setState({ ...initialState, rulesStatus: 'wait' });
  this.render(hbs`{{respond/incident-rules useLazyRendering=false}}`);
  assert.equal(this.$('.rsa-loader').length, 1, 'The loading spinner appears when the rulesStatus is "wait"');
});

test('The row cells have the expected data', function(assert) {
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rules useLazyRendering=false}}`);
  const $firstRowCells = this.$('.rsa-data-table-body-row').first().find('.rsa-data-table-body-cell');
  assert.equal($($firstRowCells[0]).find('.drag-handle').length, 1, 'The first cell in the row has a drag handle for reordering results');
  assert.equal($($firstRowCells[1]).find('input[type=radio]').length, 1, 'The second cell in the row has a selection radio button');
  assert.equal($($firstRowCells[3]).find('.enabled-rule').length, 1, 'The fourth cell in the row has an enabled-rule class');
  assert.equal($($firstRowCells[4]).find('a').length, 1, 'The fifth cell in the row has a link');
  assert.equal($($firstRowCells[6]).find('.rsa-content-datetime').length, 1, 'The seventh cell in the row has a converted date');
});

test('it shows the selected row with the proper class name', function(assert) {
  setState({ ...initialState, selectedRule: '59b92bbf4cb0f0092b6b6a8b' });
  this.render(hbs`{{respond/incident-rules useLazyRendering=false}}`);
  assert.equal(this.$('.rsa-data-table-body-row.is-selected').length, 1, 'There is one row selected');
});

test('it has the transaction overlay when isTransactionUnderway is true', function(assert) {
  setState({ ...initialState, isTransactionUnderway: true });
  this.render(hbs`{{respond/incident-rules useLazyRendering=false}}`);
  assert.equal(this.$('.transaction-in-progress .transaction-overlay').length, 1, 'The transaction overlay appears');
});

test('Clicking on a cell/row dispatches the selectRule creator', function(assert) {
  const actionSpy = sinon.spy(alertRuleCreators, 'selectRule');
  setState({ ...initialState });
  this.render(hbs`{{respond/incident-rules useLazyRendering=false}}`);
  // click on a cell
  $(this.$('.rsa-data-table-body-cell')[1]).click();
  return wait().then(() => {
    assert.ok(actionSpy.calledOnce, 'The selectRule creator was called once');
    actionSpy.restore();
  });
});