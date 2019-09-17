import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, find, findAll, click } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { patchReducer } from '../../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | list details - details footer', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
    initialize(this.owner);
  });

  const item = { id: '1', name: 'foo' };
  const listLocation1 = 'listManager';

  test('renders footer for list details with correct components', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState).listName('Foos').build();
    this.set('listLocation', listLocation1);
    this.set('detailsDone', () => {
      assert.ok(true, 'clicking button executes detailsDone');
    });
    this.set('itemSelection', () => {});
    this.set('item', item);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      listLocation=listLocation
      item=item
      detailsDone=detailsDone
      itemSelection=itemSelection
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Close');
    assert.equal(buttons[1].textContent.trim(), 'Select Foo', 'Select option rendered when item details are being viewed');

    await click(buttons[0]);
  });

  test('renders footer for list details with correct components', async function(assert) {
    assert.expect(4);
    new ReduxDataHelper(setState).listName('Foos').build();
    this.set('listLocation', listLocation1);
    this.set('detailsDone', () => {
      assert.ok(true, 'clicking button executes detailsDone');
    });

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      listLocation=listLocation
      item=item
      detailsDone=detailsDone
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[0].textContent.trim(), 'Close');
    await click(buttons[0]);

    assert.equal(buttons[1].textContent.trim(), 'Save Foo', 'Save option rendered when new item is being created');
  });

  test('clicking select from footer executes selection', async function(assert) {
    assert.expect(3);
    new ReduxDataHelper(setState).listName('Foos').build();
    this.set('listLocation', listLocation1);
    this.set('detailsDone', () => {});
    this.set('itemSelection', () => {
      assert.ok(true, 'clicking button executes item selection');
    });
    this.set('item', item);

    await render(hbs`{{list-manager/list-manager-container/item-details/details-footer
      listLocation=listLocation
      item=item
      detailsDone=detailsDone
      itemSelection=itemSelection
    }}`);

    assert.ok(find('footer.details-footer'));

    const buttons = findAll('footer.details-footer button');
    assert.equal(buttons[1].textContent.trim(), 'Select Foo', 'Select option rendered when item is being edited');
    await click(buttons[1]);
  });
});
