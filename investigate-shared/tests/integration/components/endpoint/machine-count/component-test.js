import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, settled } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | machine count', function(hooks) {
  setupRenderingTest(hooks);

  test('it should render the machine count component', async function(assert) {
    await render(hbs`{{endpoint/machine-count}}`);
    return settled().then(() => {
      // assert.equal(find('.rsa-loader').classList.contains('is-larger'), true, 'Rsa loader displayed');
      assert.equal(document.querySelectorAll('.machine-count').length, 1, 'It should render the machine count');
    });
  });

  test('it should show loading indicator', async function(assert) {
    this.set('item', { checksumSha256: 123 });
    this.set('machineCountMapping', { 1234: 1 });
    await render(hbs`{{endpoint/machine-count item=item machineCountMapping=machineCountMapping}}`);
    assert.equal(document.querySelectorAll('.is-small').length, 1, 'Rsa loader displayed');
  });

  test('it should display count', async function(assert) {
    this.set('item', { checksumSha256: 123 });
    this.set('machineCountMapping', { 123: 10 });
    await render(hbs`{{endpoint/machine-count item=item machineCountMapping=machineCountMapping}}`);
    assert.equal(document.querySelectorAll('.is-small').length, 0, 'Rsa loader not displayed');
    assert.equal(document.querySelector('.machine-count-text').textContent.trim(), 10, 'Count text is rendered');
  });
});
