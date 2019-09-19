import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | item details', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const item = { id: '1', name: 'foo' };
  const list1 = [{ id: '1', name: 'foo' }];
  const listLocation1 = 'listManager';

  test('renders list details with correct components', async function(assert) {
    const helpId1 = { topicId: 'foo', moduleId: 'bar' };
    new ReduxDataHelper(setState)
      .list(list1)
      .listName('Foos')
      .helpId(helpId1)
      .build();
    this.set('listLocation', listLocation1);
    this.set('itemSelection', () => {});
    this.set('item', item);

    await render(hbs`{{list-manager/list-manager-container/item-details
      listLocation=listLocation
      item=item
      itemSelection=itemSelection
    }}`);

    assert.equal(find('.item-details .title').textContent.trim().toUpperCase(), 'FOO DETAILS');
    assert.ok(find('.item-details .rsa-icon-help-circle-lined'), 'Help icon in details');
    assert.ok(find('.item-details .details-body'), 'Renders Details body');

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons.length, 2);
  });
});
