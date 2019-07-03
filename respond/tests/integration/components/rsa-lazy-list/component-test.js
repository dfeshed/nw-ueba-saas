import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render } from '@ember/test-helpers';

module('Integration | Component | Lazy List', function(hooks) {

  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });


  const items = [
    { id: 'a' },
    { id: 'b' },
    { id: 'c' }
  ];

  test('it renders the correct number of list items in DOM', async function(assert) {

    this.set('items', items);
    await render(hbs`{{rsa-lazy-list items=items}}`);

    assert.equal(findAll('.rsa-lazy-list').length, 1, 'Expected to find the root component DOM element.');
    assert.equal(findAll('.rsa-list').length, 1, 'Expected to find the root element of child rsa-list component.');

    const itemsRendered = findAll('.rsa-list-item');
    assert.equal(itemsRendered.length, items.length, 'Expected to find the list item DOM elements.');
  });
});