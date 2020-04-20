import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { find, render } from '@ember/test-helpers';
import EventColumnGroups from '../../../../../../data/subscriptions/column-group';

module('Integration | Component | Column Group Item', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('columnGroup item should render correctly', async function(assert) {
    this.set('item', EventColumnGroups[5]);
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-item columnGroup=item}}`);
    const columnGroupItemSelector = '.option-name';
    assert.ok(find(columnGroupItemSelector), 'Column Group list item present');
    assert.equal(find(`${columnGroupItemSelector}`).textContent.trim(), EventColumnGroups[5].name);
  });
});
