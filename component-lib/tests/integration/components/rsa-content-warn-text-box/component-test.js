import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll, find } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | content-warn-text-box', function(hooks) {
  setupRenderingTest(hooks);

  test('it includes the proper classes', async function(assert) {
    await render(hbs `{{#rsa-content-warn-text-box}}foo{{/rsa-content-warn-text-box}}`);
    const warnCount = findAll('.rsa-content-warn-text-box').length;
    assert.equal(warnCount, 1);
  });

  test('Is NOT Showing', async function(assert) {
    await render(hbs `{{#rsa-content-warn-text-box}}foo{{/rsa-content-warn-text-box}}`);
    const isShowing = findAll('.is-showing').length;
    assert.equal(isShowing, 0);
  });

  test('Set the isShowing param', async function(assert) {
    await render(hbs `{{#rsa-content-warn-text-box isShowing=true}}foo{{/rsa-content-warn-text-box}}`);
    const isShowing = findAll('.is-showing').length;
    assert.equal(isShowing, 1);
  });

  test('Is NOT alert', async function(assert) {
    await render(hbs `{{#rsa-content-warn-text-box}}foo{{/rsa-content-warn-text-box}}`);
    const isAlert = findAll('.is-alert').length;
    assert.equal(isAlert, 0);
  });

  test('Set the isAlert param', async function(assert) {
    await render(hbs `{{#rsa-content-warn-text-box isAlert=true}}foo{{/rsa-content-warn-text-box}}`);
    const isAlert = findAll('.is-alert').length;
    assert.equal(isAlert, 1);
  });

  test('it sets the message', async function(assert) {
    await render(hbs `{{#rsa-content-warn-text-box value="abc"}}foo{{/rsa-content-warn-text-box}}`);
    const message = find('.message').textContent;
    assert.notEqual(message.indexOf('abc'), -1);
  });
});
