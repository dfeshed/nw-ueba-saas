import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-text-with-tooltip/copy-content', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    this.set('text', 'test-value');

    await render(hbs`{{rsa-text-with-tooltip/copy-content}}`);

    assert.equal(findAll('.copy-content').length, 1, 'Expected to render the copy content icon');
  });
});
