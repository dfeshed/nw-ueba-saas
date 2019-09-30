import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { find, findAll, render } from '@ember/test-helpers';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';

module('Integration | Component | Column Group View', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  const DISPLAYED_COLUMNS = '.group-details > ul.column-list li';

  test('columnGroup view renders read-only details', async function(assert) {

    this.set('columnGroup', { name: 'FOO', columns: [{ field: 'foo', title: 'bar' }] });
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-view columnGroup=columnGroup}}`);

    assert.equal(find('.group-name .name').textContent.trim(), 'Group Name');
    assert.equal(find('.group-name .value').textContent.trim(), 'FOO');
    assert.equal(find('.group-details .name').textContent.trim(), 'Displayed Meta Keys');
    assert.equal(findAll(DISPLAYED_COLUMNS).length, 1, '1 columns for Summary List rendered');

  });
});
