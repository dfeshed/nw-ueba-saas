import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-text-with-tooltip/content-note', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders', async function(assert) {
    this.set('showNote', 'true');

    await render(hbs`{{rsa-text-with-tooltip/content-note}}`);

    assert.equal(findAll('.tool-tip-note').length, 1, 'Expected to render the note');

  });
});
