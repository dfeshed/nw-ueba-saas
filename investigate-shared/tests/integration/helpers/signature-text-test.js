import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { findAll, render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('signature-text', 'Integration | Helper | signature text', function(hooks) {
  setupRenderingTest(hooks);
  test('it renders signature text helper', async function(assert) {
    await render(hbs`{{signature-text}}`);
    assert.equal(findAll('.ember-view')[0].innerText, 'unsigned', 'signature-text is rendered');
  });

  test('it renders signature text with value', async function(assert) {
    this.set('text', 'microsoft,signed');
    await render(hbs`{{signature-text text}}`);
    assert.equal(findAll('.ember-view')[0].innerText, 'microsoft,signed', 'signature-text with value and signer is rendered');
  });

  test('it renders signature text with value and signer', async function(assert) {
    this.set('text', 'microsoft,signed');
    this.set('signer', 'verified');
    await render(hbs`{{signature-text text signer}}`);
    assert.equal(findAll('.ember-view')[0].innerText, 'microsoft,signed,verified', 'signature-text with value and signer is rendered');
  });
});
