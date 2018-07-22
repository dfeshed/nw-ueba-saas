import { module, test } from 'qunit';
import { setupRenderingTest } from 'ember-qunit';
import { render, findAll } from '@ember/test-helpers';
import hbs from 'htmlbars-inline-precompile';

module('Integration | Component | rsa-data-filters/filters/range-filter', function(hooks) {
  setupRenderingTest(hooks);

  test('it renders the range filter', async function(assert) {
    this.set('filterOptions', { name: 'size' });
    await render(hbs`{{rsa-data-filters/filters/range-filter filterOptions=filterOptions}}`);
    assert.equal(findAll('.range-filter').length, 1, 'Expecting to render slider');
  });
});
