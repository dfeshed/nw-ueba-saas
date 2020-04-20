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

  // selectors
  const groupName = '.column-group-name';
  const scrollBox = '.column-group-details.displayed-details.scroll-box.readonly';
  const scrollBoxName = '.column-group-details.displayed-details.scroll-box.readonly .name';
  const displayedColumns = `${scrollBox} > ul.column-list.value.readonly li`;

  test('columnGroup view renders read-only details', async function(assert) {

    this.set('columnGroup', { name: 'FOO', columns: [{ field: 'foo', title: 'bar' }] });
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details/column-group-view columnGroup=columnGroup}}`);

    assert.equal(find(`${groupName} .name`).textContent.trim(), 'Group Name', 'shall render correct text for column group name name');
    assert.equal(find(`${groupName} .value`).textContent.trim(), 'FOO', 'shall render correct text for column group name value');
    assert.equal(find(scrollBoxName).textContent.trim(), 'Displayed Meta Keys', 'shall render correct text for scroll box name');
    assert.equal(findAll(displayedColumns).length, 1, '1 columns for Summary List rendered');
  });
});
