import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../helpers/redux-data-helper';
import { render, find, findAll } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

let setState;

module('Integration | Component | Text Pagination', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    initialize(this.owner);
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  test('testing text analysis pagination being on first page', async function(assert) {
    assert.expect(6);
    new ReduxDataHelper(setState)
      .isTextView()
      .textPageNumber(1)
      .textSetCursorFlags(false, false, true, false)
      .build();
    await render(hbs`{{recon-pager/text-pagination}}`);
    assert.equal(findAll('.data-pagination').length, 1, 'Pagination controls expected');
    assert.equal(find('.page-first-button').classList.contains('is-disabled'), true, 'Page first should be disabled');
    assert.equal(find('.page-previous-button').classList.contains('is-disabled'), true, 'Page previous should be disabled');
    assert.equal(find('.page-next-button').classList.contains('is-disabled'), false, 'Page next should not be disabled');
    assert.equal(find('.page-last-button').classList.contains('is-disabled'), true, 'Page last should be disabled');
    assert.equal(find('.readonly-page-number').value, 1, 'Currently on 1st page');
  });

  test('testing text analysis pagination being on a random page before seeing the last page', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(setState)
      .isTextView()
      .textPageNumber(3)
      .textSetCursorFlags(true, true, true, false)
      .build();
    await render(hbs`{{recon-pager/text-pagination}}`);
    assert.equal(find('.page-first-button').classList.contains('is-disabled'), false, 'Page first should be enabled');
    assert.equal(find('.page-previous-button').classList.contains('is-disabled'), false, 'Page previous should be enabled');
    assert.equal(find('.page-next-button').classList.contains('is-disabled'), false, 'Page next should not be disabled');
    assert.equal(find('.page-last-button').classList.contains('is-disabled'), true, 'Page last should be disabled');
    assert.equal(find('.readonly-page-number').value, 3, 'Currently on 3rd page');
  });

  test('testing text analysis pagination on a random page after seeing the last page', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(setState)
      .isTextView()
      .textPageNumber(4)
      .textLastPage(6)
      .textSetCursorFlags(true, true, true, true)
      .build();
    await render(hbs`{{recon-pager/text-pagination}}`);
    assert.equal(find('.page-first-button').classList.contains('is-disabled'), false, 'Page first should be enabled');
    assert.equal(find('.page-previous-button').classList.contains('is-disabled'), false, 'Page previous should be enabled');
    assert.equal(find('.page-next-button').classList.contains('is-disabled'), false, 'Page next should not be disabled');
    assert.equal(find('.page-last-button').classList.contains('is-disabled'), false, 'Page last should be disabled');
    assert.equal(find('.readonly-page-number').value, 4, 'Currently on 3rd page');
  });

  test('testing text analysis pagination being on the last page', async function(assert) {
    assert.expect(5);
    new ReduxDataHelper(setState)
      .isTextView()
      .textPageNumber(6)
      .textLastPage(6)
      .textSetCursorFlags(true, true, false, false)
      .build();
    await render(hbs`{{recon-pager/text-pagination}}`);
    assert.equal(find('.page-first-button').classList.contains('is-disabled'), false, 'Page first should be enabled');
    assert.equal(find('.page-previous-button').classList.contains('is-disabled'), false, 'Page previous should be enabled');
    assert.equal(find('.page-next-button').classList.contains('is-disabled'), true, 'Page next should not be disabled');
    assert.equal(find('.page-last-button').classList.contains('is-disabled'), true, 'Page last should be disabled');
    assert.equal(find('.readonly-page-number').value, 6, 'Currently on the last page');
  });
});