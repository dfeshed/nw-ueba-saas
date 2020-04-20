import { run } from '@ember/runloop';
import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { findAll, render, click } from '@ember/test-helpers';

module('Integration | Component | List', function(hooks) {

  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });


  const items = [
    { id: 'a', foo: 'foo-a' },
    { id: 'b', foo: 'foo-b' },
    { id: 'c', foo: 'foo-c' }
  ];

  const selections = [ 'b', 'c' ];
  const fooSelections = [ 'foo-a' ];

  test('it renders the correct number of list items in DOM', async function(assert) {

    this.set('items', items);
    await render(hbs`{{#rsa-list items=items as |item|}}{{item.id}}{{/rsa-list}}`);

    assert.equal(findAll('.rsa-list').length, 1, 'Unable to find the root component DOM element.');

    const itemsRendered = findAll('.rsa-list-item');
    assert.equal(itemsRendered.length, items.length, 'Unable to find the list item DOM elements.');
  });

  test('it marks selected items with the appropriate CSS class', async function(assert) {

    this.setProperties({ items, selections });
    await render(hbs`{{rsa-list items=items selections=selections}}`);

    assert.equal(findAll('.rsa-list').length, 1, 'Unable to find the root component DOM element.');

    const itemsRendered = findAll('.rsa-list-item');
    assert.notOk(itemsRendered[0].classList.contains('is-selected'), 'Expected to  NOT find is-selected CSS class applied in DOM.');
    assert.ok(itemsRendered[1].classList.contains('is-selected'), 'Expected to find is-selected CSS class applied in DOM.');
    assert.ok(itemsRendered[itemsRendered.length - 1].classList.contains('is-selected'), 'Expected to find is-selected CSS class applied in DOM.');
  });

  test('it toggles the is-selected CSS class when a value is removed from selections', async function(assert) {
    this.setProperties({ items, selections });
    await render(hbs`{{rsa-list items=items selections=selections}}`);

    const itemsRendered = findAll('.rsa-list-item');
    assert.ok(itemsRendered[itemsRendered.length - 1].classList.contains('is-selected'), 'Expected to find is-selected CSS class applied in DOM.');

    run(() => {
      selections.popObject();
    });
    assert.notOk(itemsRendered[itemsRendered.length - 1].classList.contains('is-selected'), 'Expected is-selected CSS class to be removed when selection is removed from array.');
  });

  test('it uses the itemIdField attr to determine which attr is the identifier', async function(assert) {
    this.setProperties({ items, fooSelections });
    await render(hbs`{{rsa-list items=items itemIdField="foo" selections=fooSelections}}`);

    const itemsRendered = findAll('.rsa-list-item');
    assert.ok(itemsRendered[0].classList.contains('is-selected'), 'Expected to find is-selected CSS class applied in DOM.');
    assert.notOk(itemsRendered[1].classList.contains('is-selected'), 'Expected to NOT find is-selected CSS class applied in DOM.');
    assert.notOk(itemsRendered[itemsRendered.length - 1].classList.contains('is-selected'), 'Expected to  NOT find is-selected CSS class applied in DOM.');
  });

  test('it invokes the clickAction with the corresponding item data when clicked', async function(assert) {
    const clickAction = function(arg) {
      assert.ok(true, 'Expected clickAction to be invoked.');
      assert.equal(arg, items[0], 'Expected clickAction to received clicked item\'s corresponding data object.');
    };
    this.setProperties({ items, clickAction });
    await render(hbs`{{rsa-list items=items singleSelectAction=clickAction}}`);

    const itemsRendered = findAll('.rsa-list-item');
    await click(itemsRendered[0]);
  });
});