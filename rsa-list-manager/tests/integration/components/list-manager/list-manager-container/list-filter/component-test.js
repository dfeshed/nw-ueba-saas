import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, click, find } from '@ember/test-helpers';
import { typeInSearch } from 'ember-power-select/test-support/helpers';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';

let setState;

module('Integration | Component | list filter', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  const originalList = [ { id: '1', name: 'foo' }, { id: '2', name: 'bar' }];
  const listLocation1 = 'listManager';
  const listName1 = 'List of Things';

  test('Filters list with default filtering', async function(assert) {
    new ReduxDataHelper(setState).stateLocation(listLocation1).list(originalList).listName(listName1).build();
    this.set('stateLocation', listLocation1);

    await render(hbs`{{list-manager/list-manager-container/list-filter
      stateLocation=stateLocation
    }}`);

    assert.ok(find('.list-filter'), 'list filter component found');
    assert.ok(find('.list-filter .rsa-icon-filter-2-filled'), 'filter icon found');
    assert.equal(find('.list-filter input').getAttribute('placeholder'), 'Filter list of things');

    await click(find('.list-filter input'));
    await typeInSearch('b');

    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.filterText, 'b', 'filterText is set correctly');

    await click(find('.list-filter input'));
    await typeInSearch('bo');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.filterText, 'bo', 'filterText is set correctly');
  });
});
