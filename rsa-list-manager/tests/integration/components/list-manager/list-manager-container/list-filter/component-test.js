import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { render, click, find } from '@ember/test-helpers';
import { typeInSearch } from 'ember-power-select/test-support/helpers';
import { setupRenderingTest } from 'ember-qunit';
import { patchReducer } from '../../../../../helpers/vnext-patch';
import ReduxDataHelper from '../../../../../helpers/redux-data-helper';
import SELECTORS from '../../selectors';

let setState;

module('Integration | Component | list filter', function(hooks) {
  setupRenderingTest(hooks);

  hooks.beforeEach(function() {
    setState = (state) => {
      patchReducer(this, state);
    };
  });

  const originalList = [ { id: '1', name: 'foo' }, { id: '2', name: 'bar' }];
  const stateLocation1 = 'listManager';
  const listName1 = 'List of Things';

  // selectors
  const {
    filter,
    filterInput,
    filterIcon
  } = SELECTORS;

  test('Filters list with default filtering', async function(assert) {
    new ReduxDataHelper(setState).stateLocation(stateLocation1).list(originalList).listName(listName1).build();
    this.set('stateLocation', stateLocation1);

    await render(hbs`{{list-manager/list-manager-container/list-filter
      stateLocation=stateLocation
    }}`);

    assert.ok(find(filter), 'list filter component found');
    assert.ok(find(`${filter} ${filterIcon}`), 'filter icon found');
    assert.equal(find(filterInput).getAttribute('placeholder'), 'Filter list of things');

    await click(find(filterInput));
    await typeInSearch('b');

    const state1 = this.owner.lookup('service:redux').getState();
    assert.equal(state1.listManager.filterText, 'b', 'filterText is set correctly');

    await click(find(filterInput));
    await typeInSearch('bo');
    const state2 = this.owner.lookup('service:redux').getState();
    assert.equal(state2.listManager.filterText, 'bo', 'filterText is set correctly');
  });
});
