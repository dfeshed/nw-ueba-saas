import { render } from '@ember/test-helpers';
import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-link-to-win', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    await render(hbs`{{#rsa-link-to-win '/' target="_blank"}}Hello world{{/rsa-link-to-win}}`);
    assert.equal(this.element.querySelectorAll('*').length, 1, 'Expected root DOM node');
    assert.equal(this.element.querySelector('*').textContent.trim(), 'Hello world', 'Expected to yield block');
  });
});
