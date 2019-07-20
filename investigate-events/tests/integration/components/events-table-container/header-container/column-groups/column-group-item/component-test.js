import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { find, render } from '@ember/test-helpers';
import EventColumnGroups from '../../../../../../data/subscriptions/investigate-columns/data';

module('Integration | Component | Column Group Item', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('columnGroup item should render specific item details', async function(assert) {

    this.set('item', EventColumnGroups[8]);
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-item columnGroup=item}}`);

    const columnGroupItemSelector = '.option-name';

    assert.ok(find(columnGroupItemSelector), 'Column Group list item present');

    assert.equal(find(`${columnGroupItemSelector}`).textContent.trim(), EventColumnGroups[8].name);

  });

  test('columnGroup name if longer than 32 characters must be truncated with ellipsis', async function(assert) {

    const columnGroup = {
      id: 'LONGCOLUMNGROUP',
      name: 'Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean',
      ootb: true,
      columns: [
        { field: 'custom.theme', title: 'Theme' },
        { field: 'size', title: 'Size' },
        { field: 'custom.meta-summary', title: 'Summary', width: null }
      ]
    };

    this.set('item', columnGroup);
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-item columnGroup=item}}`);

    const columnGroupItemSelector = '.option-name';

    assert.ok(find(columnGroupItemSelector), 'Column Group list item present');

    assert.equal(find(`${columnGroupItemSelector}`).textContent.trim(), 'Lorem ipsum dolor sit amet, c...');

  });

});
