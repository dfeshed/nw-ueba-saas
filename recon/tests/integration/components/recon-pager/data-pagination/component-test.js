import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';
import { selectChoose } from 'ember-power-select/test-support/helpers';
import { click, fillIn, find, render, triggerKeyEvent } from '@ember/test-helpers';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';

let setState;

const setupState = (packetCountValue) => {
  new ReduxDataHelper(setState)
    .isPacketView()
    .packetTotal(packetCountValue)
    .build();
};

module('Integration | Component | Packet Pagination', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('testing packet pagination basic controls', async function(assert) {
    assert.expect(6);
    setupState(410);
    await render(hbs`{{recon-pager/data-pagination}}`);

    assert.ok(find('.data-pagination'), 'Pagination controls expected');
    assert.equal(find('.last-page').textContent.trim(), 5, 'Last Page Number as expected');
    assert.ok(find('.page-first-button.is-disabled'), 'Page first should be disabled');
    assert.ok(find('.page-previous-button.is-disabled'), 'Page previous should be disabled');
    assert.notOk(find('.page-next-button.is-disabled'), 'Page next should not be disabled');
    assert.notOk(find('.page-last-button.is-disabled'), 'Page last should not be disabled');
  });

  test('testing change in number of packets per page', async function(assert) {
    assert.expect(2);
    setupState(410);
    await render(hbs`{{recon-pager/data-pagination}}`);

    assert.equal(find('.ember-power-select-trigger').textContent.trim(), '100');
    await selectChoose('.power-select-dropdown', '300');
    assert.equal(find('.last-page').textContent.trim(), 2, 'Last Page Number should be changed');
  });

  test('testing jump to specific page', async function(assert) {
    assert.expect(9);
    setupState(210);
    await render(hbs`{{recon-pager/data-pagination}}`);

    assert.ok(find('.page-first-button.is-disabled'), 'Page first should be disabled');
    assert.ok(find('.page-previous-button.is-disabled'), 'Page previous should be disabled');
    assert.notOk(find('.page-next-button.is-disabled'), 'Page next should not be disabled');
    assert.notOk(find('.page-last-button.is-disabled'), 'Page last should not be disabled');

    await fillIn('.input-page-number', 2);
    await triggerKeyEvent('.input-page-number', 'keypress', 13);
    assert.equal(find('.input-page-number').value, 2, ' Page Number as expected');
    assert.notOk(find('.page-first-button.is-disabled'), 'Page first now should not be disabled');
    assert.notOk(find('.page-previous-button.is-disabled'), 'Page previous now should not be disabled');
    assert.notOk(find('.page-next-button.is-disabled'), 'Page next should not be disabled');
    assert.notOk(find('.page-last-button.is-disabled'), 'Page last should not be disabled');

  });

  test('testing packet pagination navigation buttons', async function(assert) {
    assert.expect(8);
    setupState(410);

    await render(hbs`{{recon-pager/data-pagination}}`);

    assert.ok(find('.data-pagination'), 'Pagination controls expected');
    assert.equal(find('.input-page-number').value, 1, 'Currently on 1st page');
    assert.equal(find('.last-page').textContent.trim(), 5, 'Last Page Number as expected');

    await click('.page-next-button .rsa-form-button');

    assert.equal(find('.input-page-number').value, 2, 'Moves to 2nd page');
    assert.notOk(find('.page-first-button.is-disabled'), 'Page first should not be disabled');
    assert.notOk(find('.page-previous-button.is-disabled'), 'Page previous should not be disabled');
    assert.notOk(find('.page-next-button.is-disabled'), 'Page next should not be disabled');
    assert.notOk(find('.page-last-button.is-disabled'), 'Page last should not be disabled');
  });

  test('Recon should pick default Page Size set by the user', async function(assert) {
    assert.expect(2);
    new ReduxDataHelper(setState)
      .isPacketView()
      .packetTotal(410)
      .packetPageSize(300)
      .build();
    await render(hbs`{{recon-pager/data-pagination}}`);
    assert.equal(find('.ember-power-select-trigger').textContent.trim(), '300');
    assert.equal(find('.last-page').textContent.trim(), 2, 'Last Page Number should be changed');
  });
});