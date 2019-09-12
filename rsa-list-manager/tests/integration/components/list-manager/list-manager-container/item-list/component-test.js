import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | item list', function(hooks) {
  setupRenderingTest(hooks);

  test('The list renders without ootb indicators when property absent in list items', async function(assert) {
    this.set('list', [ { id: '1', name: 'foo' }, { id: '2', name: 'bar' }]);
    this.set('itemSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container/item-list list=list itemSelection=itemSelection as |itemList|}}
        {{itemList.item}}
      {{/list-manager/list-manager-container/item-list}}`);

    assert.ok(find('ul.rsa-item-list'), 'list found');
    assert.equal(findAll('ul.rsa-item-list li').length, 2, '2 items found');
    assert.notOk(find('ul.rsa-item-list li .ootb-indicator'), 'ootb indicator not found');
  });

  test('The list renders with ootb indicators when property present in at least one list item', async function(assert) {
    this.set('list', [ { id: '1', name: 'foo', ootb: false }, { id: '2', name: 'bar' }]);
    this.set('itemSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container/item-list list=list itemSelection=itemSelection as |itemList|}}
        {{itemList.item}}
      {{/list-manager/list-manager-container/item-list}}`);

    assert.ok(find('ul.rsa-item-list'), 'list found');
    assert.equal(findAll('ul.rsa-item-list li').length, 2, '2 items found');
    assert.equal(findAll('ul.rsa-item-list li .ootb-indicator').length, 2, 'ootb indicator found');
  });
});
