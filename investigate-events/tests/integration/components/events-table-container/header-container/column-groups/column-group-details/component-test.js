import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';
import { initialize } from 'ember-dependency-lookup/instance-initializers/dependency-lookup';
import { find, findAll, render } from '@ember/test-helpers';
import EventColumnGroups from '../../../../../../data/subscriptions/column-group/findAll/data';

module('Integration | Component | Column Group Details', function(hooks) {

  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  hooks.beforeEach(function() {
    initialize(this.owner);
  });

  test('columnGroup details should render column details', async function(assert) {

    this.set('item', EventColumnGroups[8]);
    await render(hbs`{{events-table-container/header-container/column-groups/column-group-details columnGroup=item}}`);

    assert.ok(find('.column-group-details'), 'Column Group Details rendered correctly');
    assert.equal(find('.group-name label').textContent.trim(), 'Group Name');
    assert.equal(find('.group-name h3').textContent.trim(), 'Threat Analysis');
    assert.equal(find('.group-details label').textContent.trim(), 'Displayed Meta Keys');
    assert.equal(findAll('.group-details ul.column-list li').length, 57, '57 columns for Threat Analysis rendered');
  });

});
