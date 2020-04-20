import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { next } from '@ember/runloop';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import Component from '@ember/component';
import { patchReducer } from '../../../../helpers/vnext-patch';
import { waitUntil, click, find, findAll, render, triggerEvent } from '@ember/test-helpers';
import Immutable from 'seamless-immutable';
import rules from '../../../../data/subscriptions/incident-rules/findAll/data';
import { Promise } from 'rsvp';
import Service from '@ember/service';

const initialState = {
  rules,
  rulesStatus: 'complete',
  isTransactionUnderway: false,
  selectedRules: []
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

  test('The error message appears when rulesState is "error"', async function(assert) {
    await setState({ ...initialState, rulesStatus: 'error' });
    await render(hbs`{{respond/incident-rules}}`);
    assert.equal(findAll('.rsa-loader').length, 0);
    assert.equal(findAll('.no-results-message').length, 0);
    assert.equal(findAll('.incident-rules-toolbar').length, 0);
    assert.equal(findAll('.rules-error').length, 1);
  });

  test('The row cells have the expected data', async function(assert) {
    await setState({ ...initialState });
    await render(hbs`{{respond/incident-rules}}`);
    const rows = findAll('tbody tr');
    assert.equal(rows.length, 20, 'We have 20 rows');

    // All date values are present satisfying the if conditions.
    const firstRowCells = rows[0].querySelectorAll('td');
    assert.equal(firstRowCells.length, 11, 'The row must have 11 cells');
    assert.equal(firstRowCells[0].querySelectorAll('.handle').length, 1, 'The first cell in the row has a drag handle for reordering results');
    assert.equal(firstRowCells[1].querySelectorAll('input[type=checkbox]').length, 1, 'The second cell in the row has a selection checkbox');
    assert.equal(firstRowCells[3].querySelectorAll('.enabled-rule').length, 1, 'The fourth cell in the row has an enabled-rule class');
    assert.equal(firstRowCells[4].querySelectorAll('a').length, 1, 'The fifth cell in the row has a link');
    assert.equal(firstRowCells[6].querySelectorAll('.rsa-content-datetime').length, 1, 'The seventh cell in the row has a converted date');
    assert.equal(firstRowCells[9].querySelectorAll('.rsa-content-datetime').length, 1, 'The tenth cell in the row has a converted date');
    assert.equal(firstRowCells[10].querySelectorAll('.rsa-content-datetime').length, 1, 'The eleventh cell in the row has a converted date');

    // The date values not present.
    const secondRowCells = rows[1].querySelectorAll('td');
    assert.equal(secondRowCells.length, 11, 'The row must have 11 cells');
    assert.equal(secondRowCells[0].querySelectorAll('.handle').length, 1, 'The first cell in the row has a drag handle for reordering results');
    assert.equal(secondRowCells[1].querySelectorAll('input[type=checkbox]').length, 1, 'The second cell in the row has a selection checkbox');
    assert.equal(secondRowCells[3].querySelectorAll('.disabled-rule').length, 1, 'The fourth cell in the row has an enabled-rule class');
    assert.equal(secondRowCells[4].querySelectorAll('a').length, 1, 'The fifth cell in the row has a link');
    assert.equal(secondRowCells[6].querySelectorAll('.rsa-content-datetime').length, 0, 'The seventh cell in the row has a converted date');
    assert.equal(secondRowCells[9].querySelectorAll('.rsa-content-datetime').length, 0, 'The tenth cell in the row has a converted date');
    assert.equal(secondRowCells[10].querySelectorAll('.rsa-content-datetime').length, 0, 'The eleventh cell in the row has a converted date');
  });

  test('it shows the selected row with the proper class name', async function(assert) {
    await setState({ ...initialState, selectedRules: ['59b92bbf4cb0f0092b6b6a8b'] });
    await render(hbs`{{respond/incident-rules}}`);
    assert.equal(findAll('tbody tr.is-selected').length, 1, 'There is one row selected');
  });

  test('it has the transaction overlay when isTransactionUnderway is true', async function(assert) {
    await setState({ ...initialState, isTransactionUnderway: true });
    await render(hbs`{{respond/incident-rules}}`);
    assert.equal(findAll('.transaction-in-progress .transaction-overlay').length, 1, 'The transaction overlay appears');
  });

  test('click handler should not prevent propagating of event', async function(assert) {
    assert.expect(2);

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
        {{respond/incident-rules/row
          rule=rule
          selectedRules=selectedRules
          onRowClick=(action 'handleRowClick' rule)}}
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

    this.set('selectedRules', [1]);
    this.set('rule', { id: 1, name: 'x' });
    await render(hbs`{{test-clazz rule=rule selectedRules=selectedRules}}`);

    const divSelector = '.order';
    await click(divSelector);

    await waitUntil(() => rowClicked === true, { timeout });
    assert.equal(rowClicked, true);
    assert.equal(clicked, undefined);
  });

  test('Check if dragging a row selects the row', async function(assert) {
    assert.expect(2);

    await setState({ ...initialState });
    await render(hbs`{{respond/incident-rules}}`);
    const rows = findAll('[data-sortable-handle]');
    assert.equal(rows.length, 20, 'We have 20 rows');

    // Turn off animation
    findAll('.sortable-item').forEach((d) => d.style['transition-property'] = 'none');

    // verifiying the drag Event of the row
    const [firstRow] = rows;
    await triggerEvent(firstRow, 'mousedown', { clientX: 0, clientY: 0, which: 1 });
    await triggerEvent(firstRow, 'mousemove', { clientX: 0, clientY: 10, which: 1 });
    await triggerEvent(firstRow, 'mouseup', { clientX: 0, clientY: 50, which: 1 });

    assert.equal(findAll('tbody tr.is-selected').length, 1, 'There is one row selected');
  });

  test('row should be selected and previous selections retained when the cell containing check box is clicked', async function(assert) {
    assert.expect(5);

    await setState({ ...initialState });
    await render(hbs`{{respond/incident-rules}}`);
    const rows = findAll('tbody tr');
    assert.equal(rows.length, 20, 'We have 20 rows');

    const firstRowCells = rows[0].querySelectorAll('td');
    assert.equal(firstRowCells.length, 11, 'The row must have 11 cells');

    const secondRowCells = rows[1].querySelectorAll('td');
    assert.equal(secondRowCells.length, 11, 'The row must have 11 cells');

    // verifiying clicking on the cell is same as clicking on the checkbox,
    // the row should be selected and all previously selection of rows shoudl be retained.
    await click(firstRowCells[1]);
    assert.equal(findAll('tbody tr.is-selected').length, 1, 'There is one row selected');

    await click(secondRowCells[1]);
    assert.equal(findAll('tbody tr.is-selected').length, 2, 'There are two rows selected');

  });

  test('clicking on the select-all checkbox toggles the selected rules', async function(assert) {
    await setState({ ...initialState, selectedRules: ['59b92bbf4cb0f0092b6b6a8b'] });
    await render(hbs`{{respond/incident-rules}}`);

    await click('.select input.rsa-form-checkbox');
    assert.equal(findAll('tbody tr.is-selected').length, 20, 'Select-All selects the 20 rules');
    assert.ok(find('.select input.rsa-form-checkbox.checked'), 'the select-all checkbox is checked');

    await click('.select input.rsa-form-checkbox');
    assert.equal(findAll('tbody tr:not(.is-selected)').length, 20, 'Select-All unselects the 20 rules');
    assert.ok(find('.select input.rsa-form-checkbox:not(.checked)'), 'the select-all checkbox is unchecked');
  });

});
