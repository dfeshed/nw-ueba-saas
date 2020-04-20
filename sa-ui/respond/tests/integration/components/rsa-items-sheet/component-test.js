import { setupRenderingTest } from 'ember-qunit';
import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { settled, findAll, render } from '@ember/test-helpers';

module('Integration | Component | Items Sheet', function(hooks) {

  setupRenderingTest(hooks, {
    integration: true,
    resolver: engineResolverFor('respond')
  });


  const item1 = {
    id: 'id1',
    foo: 'foo1',
    bar: 'bar1'
  };

  const item2 = {
    id: 'id2',
    foo: 'foo2',
    bar: 'bar2'
  };

  test('it renders the table view initially when given a list of more than 1 item', async function(assert) {
    this.setProperties({
      items: [ item1, item2 ],
      totalCount: 2
    });
    await render(hbs`{{rsa-items-sheet items=items totalCount=totalCount}}`);
    const itemSheets = findAll('.rsa-items-sheet');
    assert.equal(itemSheets.length, 1, 'Expected to find root DOM node');
    const tables = itemSheets[0].querySelectorAll('.rsa-items-sheet__table-view');
    assert.equal(tables.length, 1, 'Expected to find data table DOM node');
    const details = itemSheets[0].querySelectorAll('.rsa-items-sheet__details-view');
    assert.notOk(details.length, 'Expected to NOT find detais view DOM node');
  });

  test('it renders the details view initially when given a list of only 1 item', async function(assert) {
    this.setProperties({
      items: [ item1 ],
      totalCount: 1
    });
    await render(hbs`{{rsa-items-sheet items=items totalCount=totalCount}}`);
    const itemSheets = findAll('.rsa-items-sheet');
    assert.equal(itemSheets.length, 1, 'Expected to find root DOM node');
    const tables = itemSheets[0].querySelectorAll('.rsa-items-sheet__table-view');
    assert.notOk(tables.length, 'Expected to NOT find data table DOM node');
    const details = itemSheets[0].querySelectorAll('.rsa-items-sheet__details-view');
    assert.equal(details.length, 1, 'Expected to find detais view DOM node');
  });

  test('it renders the table view initially when told there will be multiple items', async function(assert) {
    this.setProperties({
      items: [ item1 ],
      totalCount: 2
    });
    await render(hbs`{{rsa-items-sheet items=items totalCount=totalCount}}`);
    const tables = findAll('.rsa-items-sheet__table-view');
    assert.equal(tables.length, 1, 'Expected to find data table DOM node');
    const details = findAll('.rsa-items-sheet__details-view');
    assert.notOk(details.length, 'Expected to NOT find detais view DOM node');
  });

  test('it preserves the selected item when the items list is reset to items that does include the selected item', async function(assert) {
    const item3 = { id: 3 };

    this.setProperties({
      items: [ item1, item2 ],
      totalCount: 2,
      selectedItem: item2
    });
    await render(hbs`{{rsa-items-sheet items=items totalCount=totalCount selectedItem=selectedItem}}`);
    const tables = findAll('.rsa-items-sheet__table-view');
    assert.notOk(tables.length, 'Expected to not find data table DOM node');
    const details = findAll('.rsa-items-sheet__details-view');
    assert.ok(details.length, 'Expected to find detais view DOM node');
    this.setProperties({
      items: [item2, item3],
      totalCount: 2
    });
    await settled().then(() => {
      assert.equal(this.get('selectedItem'), item2, 'Expected selectedItem to be preserved');
    });
  });
});