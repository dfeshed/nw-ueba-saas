import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, findAll, find } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | item list', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const listLocation1 = 'listManager';
  const listName1 = 'My Items';
  const list1 = [ { id: '1', name: 'foo' }, { id: '2', name: 'bar' }];

  test('The list renders without ootb indicators when property absent in list items', async function(assert) {
    new ReduxDataHelper(setState).list(list1).listLocation(listLocation1).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container/item-list 
      listLocation=listLocation
      itemSelection=itemSelection 
      as |itemList|}}
        {{itemList.item}}
      {{/list-manager/list-manager-container/item-list}}`);

    assert.ok(find('ul.rsa-item-list'), 'list found');
    assert.equal(findAll('ul.rsa-item-list li').length, 2, '2 items found');
    assert.notOk(find('ul.rsa-item-list li .is-editable-indicator'), 'is-editable indicator not found');
  });

  test('The list renders with ootb indicators when property present in at least one list item', async function(assert) {
    const list2 = [ { id: '1', name: 'foo', isEditable: false }, { id: '2', name: 'bar' }];
    new ReduxDataHelper(setState).list(list2).listLocation(listLocation1).listName(listName1).build();
    this.set('listLocation', listLocation1);
    this.set('itemSelection', () => {});

    await render(hbs`{{#list-manager/list-manager-container/item-list
      listLocation=listLocation
      itemSelection=itemSelection
      as |itemList|}}
        {{itemList.item}}
      {{/list-manager/list-manager-container/item-list}}`);

    assert.ok(find('ul.rsa-item-list'), 'list found');
    assert.equal(findAll('ul.rsa-item-list li').length, 2, '2 items found');
    assert.equal(findAll('ul.rsa-item-list li .is-editable-indicator').length, 2, 'is-editable indicator found');
  });
});
