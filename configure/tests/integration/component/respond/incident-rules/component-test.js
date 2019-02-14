import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { next } from '@ember/runloop';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Component from '@ember/component';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { waitUntil, click, find, findAll, render } from '@ember/test-helpers';
import Immutable from 'seamless-immutable';
import rules from '../../../../data/subscriptions/incident-rules/findAll/data';
import { Promise } from 'rsvp';
import Service from '@ember/service';
import $ from 'jquery';

const initialState = {
  rules,
  rulesStatus: 'complete',
  isTransactionUnderway: false,
  selectedRule: null
};

let setState;
const timeout = 10000;

module('Integration | Component | Respond Incident Rules', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('configure')
  });

  hooks.beforeEach(async function() {
    setState = async(state) => {
      return new Promise((resolve) => {
        const fullState = { configure: { respond: { incidentRules: state } } };
        patchReducer(this, Immutable.from(fullState));
        next(null, resolve);
      });
    };
  });

  test('The component appears in the DOM with the expected number of rows', async function(assert) {
    await setState({ ...initialState });
    await render(hbs`{{respond/incident-rules}}`);
    assert.equal(findAll('.rsa-incident-rules').length, 1, 'The component appears in the DOM');
    assert.equal(findAll('tbody tr').length, 20, 'There are 20 rows in the table');
    assert.equal(findAll('tbody tr.is-selected').length, 0, 'There are no selected rows');
  });

  test('The no results message appears when there are no rules', async function(assert) {
    await setState({ ...initialState, rules: [] });
    await render(hbs`{{respond/incident-rules}}`);
    assert.equal(find('.no-results-message .message').textContent.trim(), 'No incident rules were found', 'The no results message appears when there are no rules');
  });

  test('The loading spinner appears when rulesState is "wait"', async function(assert) {
    await setState({ ...initialState, rulesStatus: 'wait' });
    await render(hbs`{{respond/incident-rules}}`);
    assert.equal(findAll('.rsa-loader').length, 1, 'The loading spinner appears when the rulesStatus is "wait"');
  });

  test('The row cells have the expected data', async function(assert) {
    await setState({ ...initialState });
    await render(hbs`{{respond/incident-rules}}`);
    const $firstRowCells = this.$('tbody tr').first().find('td');
    assert.equal($($firstRowCells[0]).find('.handle').length, 1, 'The first cell in the row has a drag handle for reordering results');
    assert.equal($($firstRowCells[1]).find('input[type=radio]').length, 1, 'The second cell in the row has a selection radio button');
    assert.equal($($firstRowCells[3]).find('.enabled-rule').length, 1, 'The fourth cell in the row has an enabled-rule class');
    assert.equal($($firstRowCells[4]).find('a').length, 1, 'The fifth cell in the row has a link');
    assert.equal($($firstRowCells[6]).find('.rsa-content-datetime').length, 1, 'The seventh cell in the row has a converted date');
  });

  test('it shows the selected row with the proper class name', async function(assert) {
    await setState({ ...initialState, selectedRule: '59b92bbf4cb0f0092b6b6a8b' });
    await render(hbs`{{respond/incident-rules}}`);
    assert.equal(findAll('tbody tr.is-selected').length, 1, 'There is one row selected');
  });

  test('it has the transaction overlay when isTransactionUnderway is true', async function(assert) {
    await setState({ ...initialState, isTransactionUnderway: true });
    await render(hbs`{{respond/incident-rules}}`);
    assert.equal(findAll('.transaction-in-progress .transaction-overlay').length, 1, 'The transaction overlay appears');
  });

  test('click handler should not prevent propagating of event', async function(assert) {
    assert.expect(3);

    const transitions = [];
    const FakeRoutingService = Service.extend({
      generateURL: () => {
        return;
      },
      transitionTo: (name) => {
        transitions.push(name);
      }
    });
    this.owner.register('service:-routing', FakeRoutingService);

    let clicked, rowClicked;
    const FakeComponent = Component.extend({
      layout: hbs`
        {{#respond/incident-rules/row
          rule=rule
          selectedItemId=selectedRuleId onRowClick=(action 'handleRowClick' rule)}}
          <div test-id="linkWrapper">
            {{#link-to 'respond.incident-rule' rule.id test-id="ruleLink"}}{{rule.name}}{{/link-to}}
          </div>
        {{/respond/incident-rules/row}}
      `,
      click() {
        clicked = true;
      },
      actions: {
        handleRowClick() {
          rowClicked = true;
        }
      }
    });

    this.owner.register('component:test-clazz', FakeComponent);

    this.set('selectedRuleId', 1);
    this.set('rule', { id: 1, name: 'x' });
    await render(hbs`{{test-clazz rule=rule selectedRuleId=selectedRuleId}}`);

    const divSelector = '[test-id=linkWrapper]';
    await click(divSelector);

    await waitUntil(() => rowClicked === true, { timeout });
    assert.equal(rowClicked, true);
    assert.equal(clicked, undefined);

    const linkSelector = '[test-id=ruleLink]';
    await click(linkSelector);

    await waitUntil(() => transitions.length > 0, { timeout });
    assert.deepEqual(transitions, ['respond.incident-rule']);
  });
});
