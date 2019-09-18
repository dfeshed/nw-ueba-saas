import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | list footer', function(hooks) {

  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const originalList = [ { id: '1', name: 'foo' }, { id: '2', name: 'bar' }];
  const listLocation1 = 'listManager';
  const listName1 = 'Meta Groups';

  test('renders footer for list with correct components', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState)
      .listLocation(listLocation1)
      .list(originalList)
      .listName(listName1)
      .build();
    this.set('listLocation', listLocation1);
    this.set('editItem', () => {
      assert.ok(true, 'clicking button executes editItem');
    });

    await render(hbs`{{list-manager/list-manager-container/list-footer
      listLocation=listLocation
      createItem=editItem }}`);

    assert.ok(find('footer.list-footer'));
    assert.notOk(find('.list-help-icon'), 'Help Icon not available');

    const buttons = findAll('footer.list-footer button');
    assert.equal(buttons[0].textContent.trim(), 'New Meta Group');
    await click(buttons[0]);
  });

  test('renders help icon if provided', async function(assert) {
    assert.expect(4);
    const helpId1 = { topicId: 'foo', moduleId: 'bar' };
    new ReduxDataHelper(setState)
      .listLocation(listLocation1)
      .list(originalList)
      .helpId(helpId1)
      .listName(listName1)
      .build();
    this.set('listLocation', listLocation1);
    this.set('editItem', () => {
      assert.ok(true, 'clicking New Meta Group button executes editItem');
    });

    await render(hbs`{{list-manager/list-manager-container/list-footer
      listLocation=listLocation
      createItem=editItem }}`);

    assert.ok(find('footer.list-footer'));
    assert.ok(find('.list-help-icon button'), 'Help Icon available');

    const buttons = findAll('footer.list-footer button');
    assert.equal(buttons[0].textContent.trim(), 'New Meta Group');
    await click(buttons[0]);
  });
});
