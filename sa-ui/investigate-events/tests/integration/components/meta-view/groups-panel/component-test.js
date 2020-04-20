import { module, test } from 'qunit';
import hbs from 'htmlbars-inline-precompile';
import { find, findAll, render } from '@ember/test-helpers';
import { setupRenderingTest } from 'ember-qunit';
import engineResolverFor from 'ember-engines/test-support/engine-resolver-for';

module('Integration | Component | Groups Panel', function(hooks) {
  setupRenderingTest(hooks, {
    resolver: engineResolverFor('investigate-events')
  });

  test('it renders', async function(assert) {
    const groups = [
      { id: 1, name: 'Group 1', type: 'group' },
      { id: 2, name: 'Group 2', type: 'group' }
    ];
    this.set('groups', groups);
    await render(hbs`{{meta-view/groups-panel groups=groups}}`);
    assert.ok(find('.rsa-investigate-meta-groups-panel'));
    assert.equal(findAll('li').length, groups.length, 'Expected one list item for each group.');
  });
});