import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';

module('Integration | Component | item details', function(hooks) {
  setupRenderingTest(hooks);

  const item = { id: '1', name: 'foo' };

  test('renders list details with correct components', async function(assert) {

    this.set('detailsDone', () => {});
    this.set('itemSelection', () => {});
    this.set('itemType', 'Foo');
    this.set('item', item);
    this.set('helpId', { topicId: 'foo', moduleId: 'bar' });

    await render(hbs`{{list-manager/list-manager-container/item-details
      item=item
      itemType=itemType
      detailsDone=detailsDone
      itemSelection=itemSelection
      helpId=helpId
    }}`);

    assert.equal(find('.item-details .title').textContent.trim().toUpperCase(), 'FOO DETAILS');
    assert.ok(find('.item-details .rsa-icon-help-circle-lined'), 'Help icon in details');
    assert.ok(find('.item-details .details-body'), 'Renders Details body');

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons.length, 2);
  });
});
