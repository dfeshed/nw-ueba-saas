import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import { find, render } from '@ember/test-helpers';
import { selectors } from '../../events-list-row/generic/detail/selectors';

module('Integration | Component | properties-for/key-value', function(hooks) {
  setupRenderingTest(hooks);

  test('renders key/value for given member with name & value pair or N/A', async function(assert) {
    this.set('member', {
      value: '127.0.0.1',
      name: 'x'
    });
    await render(hbs`{{properties-for/key-value member=member}}`);
    assert.equal(find(selectors.key).textContent.trim(), 'X');
    assert.equal(find(selectors.value).textContent.trim(), '127.0.0.1');
    assert.equal(find(selectors.value).classList.contains('entity'), true);

    this.set('member', {
      value: '',
      name: 'x'
    });
    await render(hbs`{{properties-for/key-value member=member}}`);
    assert.equal(find(selectors.key).textContent.trim(), 'X');
    assert.equal(find(selectors.value).textContent.trim(), 'N/A');
    assert.equal(find(selectors.value).classList.contains('entity'), false);
  });
});
