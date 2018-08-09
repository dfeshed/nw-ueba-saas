import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | tree-path-nav', function(hooks) {
  setupRenderingTest(hooks);

  test('It renders one link for the root path', async function(assert) {
    await render(hbs`{{tree-path-nav path="/"}}`);

    assert.equal(this.element.getElementsByClassName('tree-path-nav').length, 1);
  });

  test('It renders two links for /sys', async function(assert) {
    await render(hbs`{{tree-path-nav path="/sys"}}`);

    assert.equal(this.element.getElementsByClassName('tree-path-nav').length, 2);
  });

  test('It renders three links for /sys/stats', async function(assert) {
    await render(hbs`{{tree-path-nav path="/sys/stats"}}`);

    assert.equal(this.element.getElementsByClassName('tree-path-nav').length, 3);
  });
});
